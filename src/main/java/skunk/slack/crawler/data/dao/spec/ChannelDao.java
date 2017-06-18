package skunk.slack.crawler.data.dao.spec;

import skunk.slack.crawler.data.entity.model.Channel;

public interface ChannelDao extends EntityDao<Channel> {
	public Channel get(String id);
}
