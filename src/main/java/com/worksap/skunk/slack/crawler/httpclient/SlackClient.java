package com.worksap.skunk.slack.crawler.httpclient;

import java.util.Set;

import com.worksap.skunk.slack.crawler.data.entity.User;

public interface SlackClient {
	public Set<User> getUsers();
}
