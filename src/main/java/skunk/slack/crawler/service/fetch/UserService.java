package skunk.slack.crawler.service.fetch;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import skunk.slack.crawler.data.entity.model.User;
import skunk.slack.crawler.httpclient.SlackClient;

public class UserService {
	private SlackClient slackClient;
	private static Map<String, User> users;

	public UserService(SlackClient slackClient) {
		this.slackClient = slackClient;
	}

	public User getUser(String id) {
		if (Objects.isNull(users)) {
			fetchUsers();
		}
		return users.get(id);
	}

	private void fetchUsers() {
		users = slackClient.getUsers().getList().stream().collect(Collectors.toMap(u -> u.getId(), u -> u));
	}

	public void setSlackClient(SlackClient slackClient) {
		this.slackClient = slackClient;
	}
}
