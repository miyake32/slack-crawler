package skunk.slack.crawler.data.dao.impl;

import java.io.IOException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import lombok.extern.slf4j.Slf4j;
import skunk.slack.crawler.data.core.hibernate.SessionFactory;
import skunk.slack.crawler.data.dao.spec.ChannelDao;
import skunk.slack.crawler.data.entity.model.Channel;

@Slf4j
public class ChannelDaoHibernateImpl extends AbstractEntityDaoHibernateImpl<Channel> implements ChannelDao {

	@Override
	Class<Channel> getEntityClass() {
		return Channel.class;
	}

	@Override
	public Channel get(String id) {
		try (Session session = SessionFactory.openSession();) {
			return (Channel) session.createCriteria(Channel.class).add(Restrictions.eq("id", id)).list().get(0);
		} catch (HibernateException | IOException e) {
			log.error("Failed to retrieve channel [id:{}]", id, e);
			return null;
		}
	}

}
