package skunk.slack.crawler.service.data;

import java.util.Collection;
import java.util.List;

import skunk.slack.crawler.data.dao.impl.ChannelDaoHibernateImpl;
import skunk.slack.crawler.data.dao.impl.MessageDaoHibernateImpl;
import skunk.slack.crawler.data.dao.spec.MessageDao;
import skunk.slack.crawler.data.entity.model.Channel;
import skunk.slack.crawler.data.entity.model.Message;
import skunk.slack.crawler.httpaccess.client.SlackClient;
import skunk.slack.crawler.util.TimeStampUtils;

public class MessageService {
	private MessageDao dao;
	private ChannelService channelService;
	
	public MessageService(SlackClient slackClient) {
		this.channelService = new ChannelService(slackClient, new ChannelDaoHibernateImpl());
		this.dao = new MessageDaoHibernateImpl();
	}
	
	public List<Message> getMessages(String channelId, int count, String maxTs) {
		Channel channel = channelService.getChannel(channelId);
		return dao.getInRange(channel, count, maxTs);
	}
	
	public List<Message> getMessages(String channelId, int count) {
		return getMessages(channelId, count, TimeStampUtils.now());
	}
	
	public List<Message> search(Collection<String> keywords, Collection<String> channelIds) {
		return dao.search(keywords, channelIds);
	}
	
	public void save(Collection<Message> messages) {
		dao.save(messages);
	}
}
