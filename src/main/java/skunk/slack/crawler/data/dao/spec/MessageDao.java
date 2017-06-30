package skunk.slack.crawler.data.dao.spec;

import java.util.Collection;
import java.util.List;

import skunk.slack.crawler.data.entity.model.Channel;
import skunk.slack.crawler.data.entity.model.Message;

public interface MessageDao extends EntityDao<Message> {
	public List<Message> getInRange(Channel channel, Integer count, String maxTs);
	public List<Message> search(Collection<String> keywords, Collection<String> channelIds);
}
