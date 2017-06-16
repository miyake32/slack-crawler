package skunk.slack.crawler.data.dao.impl;

import skunk.slack.crawler.data.dao.spec.ChannelDao;
import skunk.slack.crawler.data.entity.model.Channel;

public class ChannelDaoHibernateImpl extends AbstractEntityDaoHibernateImpl<Channel> implements ChannelDao {

	@Override
	Class<Channel> getEntityClass() {
		return Channel.class;
	}

}
