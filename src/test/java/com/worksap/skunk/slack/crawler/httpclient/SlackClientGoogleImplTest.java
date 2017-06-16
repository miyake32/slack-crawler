package com.worksap.skunk.slack.crawler.httpclient;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import skunk.slack.crawler.data.entity.model.Channel;
import skunk.slack.crawler.data.entity.model.Message;
import skunk.slack.crawler.data.entity.model.User;
import skunk.slack.crawler.data.entity.type.ChannelType;
import skunk.slack.crawler.httpclient.SlackClient;
import skunk.slack.crawler.httpclient.SlackClientGoogleImpl;
import skunk.slack.crawler.main.SlackCrawlerPropertiesHolder;

public class SlackClientGoogleImplTest {
	SlackClient client = new SlackClientGoogleImpl(SlackCrawlerPropertiesHolder.getTeamUrl(),
			SlackCrawlerPropertiesHolder.getToken());

	@Test
	public void getUsersTest() {
		List<User> users = client.getUsers().getList();
		Assert.assertNotEquals(0, users.size());
		User user = users.stream().findAny().get();
		Assert.assertNotNull(user.getId());
		Assert.assertNotNull(user.getName());
	}
	
	@Test
	public void getPublicChannelsTest() {
		List<Channel> channels = client.getPublicChannels().getList();
		Assert.assertNotEquals(0, channels.size());
		Channel channel = channels.stream().findAny().get();
		Assert.assertNotNull(channel.getId());
		Assert.assertNotNull(channel.getName());
		Assert.assertEquals(ChannelType.PUBLIC_CHANNEL, channel.getType());
	}
	
	@Test
	public void getMessagesTest1() {
		Channel channel = client.getPublicChannels().getList()
							.stream().filter(c -> c.getIsMember())//.findAny().get();
							.collect(Collectors.toList()).get(10);
		List<Message> messages = client.getMessages(channel).getList();
		Assert.assertNotEquals(0, messages.size());

		Message message = messages.stream().findAny().get();
		System.out.println(message);
	}
}
