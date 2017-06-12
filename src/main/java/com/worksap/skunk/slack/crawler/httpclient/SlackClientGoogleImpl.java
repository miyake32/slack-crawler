package com.worksap.skunk.slack.crawler.httpclient;

import java.io.IOException;
import java.util.Set;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.worksap.skunk.slack.crawler.data.entity.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SlackClientGoogleImpl implements SlackClient {
	private static HttpTransport httpTransport = new NetHttpTransport();
	private HttpRequestFactory requestFactory;
	private String baseUrl;
	private String token; 
	
	public SlackClientGoogleImpl(String slackTeamUrl, String token) {
		StringBuilder url = new StringBuilder();
		this.baseUrl = url.append("https://").append(slackTeamUrl)
				.append(".slack.com/api/").toString();
		this.token = token;
		this.requestFactory = httpTransport.createRequestFactory();
	}
	
	public Set<User> getUsers() {
		GenericUrl usersList = new GenericUrl(baseUrl + "users.list");
		usersList.set("token", this.token);
		try {
			HttpResponse response = requestFactory.buildGetRequest(usersList).execute();
			return SlackResponseParser.parseResponse(response.getContent(), obj -> {
				User user = new User();
				user.setId(obj.get("id").getAsString());
				user.setName(obj.get("name").getAsString());
				user.setRealName(obj.get("profile").getAsJsonObject().get("real_name").getAsString());
				return user;
			});
		} catch (IOException e) {
			log.error("Exception occurred while getting users", e);
			return null;
		}
	}
}
