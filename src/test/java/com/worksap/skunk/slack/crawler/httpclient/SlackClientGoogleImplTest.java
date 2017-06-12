package com.worksap.skunk.slack.crawler.httpclient;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.worksap.skunk.slack.crawler.data.entity.User;
import com.worksap.skunk.slack.crawler.main.SlackCrawlerPropertiesHolder;

public class SlackClientGoogleImplTest {
	SlackClient client = new SlackClientGoogleImpl(SlackCrawlerPropertiesHolder.getTeamUrl(),
			SlackCrawlerPropertiesHolder.getToken());

	@Test
	public void getUsersTest() {
		Set<User> users = client.getUsers();
		Assert.assertNotEquals(0, users.size());
	}
}
