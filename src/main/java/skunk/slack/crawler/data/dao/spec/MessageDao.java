package skunk.slack.crawler.data.dao.spec;

import java.util.List;

import skunk.slack.crawler.data.entity.model.Channel;
import skunk.slack.crawler.data.entity.model.Message;

public interface MessageDao extends EntityDao<Message> {
	public List<Message> getFromLatest(Channel channel, Integer count, String maxTs);
}
