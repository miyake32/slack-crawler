package skunk.slack.crawler.service;

import java.util.Objects;

import skunk.slack.crawler.data.dao.impl.ChannelDaoHibernateImpl;
import skunk.slack.crawler.data.dao.impl.UserDaoHibernateImpl;
import skunk.slack.crawler.httpaccess.client.SlackClient;
import skunk.slack.crawler.httpaccess.client.SlackClientGoogleImpl;

public class ServiceFactory {
	private static SlackClient slackClient = new SlackClientGoogleImpl(SlackCrawlerPropertiesHolder.getTeamUrl(), SlackCrawlerPropertiesHolder.getToken());
	private static ChannelService channelService = null;
	private static MessageService messageService = null;
	private static UserService userService = null;
	
	public static ChannelService getChannelService() {
		if (Objects.isNull(channelService)) {
			channelService = new ChannelService(slackClient, new ChannelDaoHibernateImpl());
		}
		return channelService;
	}
	public static MessageService getMessageService() {
		if (Objects.isNull(messageService)) {
			messageService = new MessageService(slackClient);
		}
		return messageService;
	}
	
	public static UserService getUserService() {
		if (Objects.isNull(userService)) {
			userService = new UserService(slackClient, new UserDaoHibernateImpl());
		}
		return userService;
	}
	
	public static SlackClient getSlackClient() {
		return slackClient;
	}
}
