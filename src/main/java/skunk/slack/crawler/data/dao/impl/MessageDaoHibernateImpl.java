package skunk.slack.crawler.data.dao.impl;

import java.io.IOException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import lombok.extern.slf4j.Slf4j;
import skunk.slack.crawler.data.core.hibernate.SessionFactory;
import skunk.slack.crawler.data.dao.spec.MessageDao;
import skunk.slack.crawler.data.entity.model.Channel;
import skunk.slack.crawler.data.entity.model.Message;

@Slf4j
public class MessageDaoHibernateImpl extends AbstractEntityDaoHibernateImpl<Message> implements MessageDao {

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<Message> getInRange(Channel channel, Integer count, String maxTs) {
		try (Session session = SessionFactory.openSession();) {
			return session.createCriteria(Message.class).add(Restrictions.eq("channel", channel))
					.add(Restrictions.le("ts", maxTs)).addOrder(Order.desc("ts")).setMaxResults(count).list();
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

}
