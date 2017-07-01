package skunk.slack.crawler.data.dao.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.google.common.base.Joiner;

import lombok.extern.slf4j.Slf4j;
import skunk.slack.crawler.data.core.hibernate.SessionFactory;
import skunk.slack.crawler.data.dao.spec.MessageDao;
import skunk.slack.crawler.data.entity.model.Channel;
import skunk.slack.crawler.data.entity.model.Message;
import skunk.slack.crawler.data.entity.model.User;

@Slf4j
public class MessageDaoHibernateImpl extends AbstractEntityDaoHibernateImpl<Message> implements MessageDao {
	private static final Pattern KEYWORD = Pattern.compile("[^\")';%]+");
	private static final UserDaoHibernateImpl userDao = new UserDaoHibernateImpl();

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<Message> getInRange(Channel channel, Integer count, String maxTs) {
		try (Session session = SessionFactory.openSession();) {
			// omit duplicated Message with distinct
			// because multiple Message is created if multiple referenced_users exist
			return (List<Message>) session.createCriteria(Message.class).add(Restrictions.eq("channel", channel))
					.add(Restrictions.le("ts", maxTs)).addOrder(Order.desc("ts")).setMaxResults(count).list().stream()
					.distinct().collect(Collectors.toList());
		} catch (HibernateException | IOException e) {
			log.error("Failed to retrieve messages [channel:{},count:{},maxTs:{}]", channel.getName(), count, maxTs);
			log.error("", e);
			return null;
		}
	}

	@Override
	Class<Message> getEntityClass() {
		return Message.class;
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public List<Message> search(Collection<String> keywords, Collection<String> channelIds) {
		List<User> users = userDao.getUsers(keywords);
		Set<String> userIds = new HashSet<>();
		Set<String> userNames = new HashSet<>();
		Set<String> channelIdSet = new HashSet<>(channelIds);
		for (User user : users) {
			userIds.add(user.getId());
			userNames.add(user.getName());
		}
		Set<String> keywordSet = keywords.stream().filter(k -> !userNames.contains(k)).collect(Collectors.toSet());

		try (Session session = SessionFactory.openSession();) {
			String sql = createSql(keywordSet, userIds, channelIdSet);
			return (List<Message>) session.createSQLQuery(sql.toString()).addEntity(Message.class).list().stream()
					.distinct().collect(Collectors.toList());
		} catch (HibernateException | IOException e) {
			log.error("Failed to retrieve messages [channels:{},keywords:{}]", channelIds, keywords);
			log.error("", e);
			return null;
		}
	}

	private String createSql(Set<String> keywords, Set<String> userIds, Set<String> channelIds) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM message m ");
		if (!userIds.isEmpty()) {
			sql.append("LEFT JOIN message__user u ON m.message_type = u.message_message_type AND m.ts = u.message_ts ");
		}
		sql.append("WHERE (");
		if (!keywords.isEmpty()) {
			sql.append("(");
		}
		boolean isFirstKeyword = true;
		for (String keyword : keywords) {
			if (!KEYWORD.matcher(keyword).matches()) {
				continue;
			}
			if (isFirstKeyword) {
				isFirstKeyword = false;
			} else {
				sql.append("AND ");
			}
			sql.append("m.text LIKE '%").append(keyword).append("%' ");
		}
		if (!keywords.isEmpty()) {
			sql.append(") ");
		}
		if (!keywords.isEmpty() && !userIds.isEmpty()) {
			sql.append("AND ");
		}
		if (!userIds.isEmpty()) {
			String userIdsStr = new StringBuilder("('").append(Joiner.on("','").join(userIds)).append("') ").toString();
			sql.append("(m.user_id IN ").append(userIdsStr);
			sql.append("OR u.referenced_users_id IN ").append(userIdsStr).append(")");
		}
		sql.append(") AND m.channel_id IN ('").append(Joiner.on("','").join(channelIds)).append("') ");
		sql.append("ORDER BY m.ts DESC");

		return sql.toString();
	}
}
