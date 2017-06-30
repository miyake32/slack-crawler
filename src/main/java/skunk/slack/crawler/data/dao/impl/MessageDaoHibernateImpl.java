package skunk.slack.crawler.data.dao.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
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

@Slf4j
public class MessageDaoHibernateImpl extends AbstractEntityDaoHibernateImpl<Message> implements MessageDao {
	private static final Pattern KEYWORD = Pattern.compile("[^\")';%]+");

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
		try (Session session = SessionFactory.openSession();) {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM message m ").append("LEFT JOIN user u ON m.user_id = u.id WHERE ");
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
				sql.append("(m.text LIKE '%").append(keyword).append("%' OR u.name = '").append(keyword).append("') ");
			}
			sql.append("AND m.channel_id IN ('").append(Joiner.on("','").join(channelIds)).append("') ");
			sql.append("ORDER BY m.ts DESC");

			return session.createSQLQuery(sql.toString()).addEntity(Message.class).list();
		} catch (HibernateException | IOException e) {
			log.error("Failed to retrieve messages [channels:{},keywords:{}]", channelIds, keywords);
			log.error("", e);
			return null;
		}
	}
}
