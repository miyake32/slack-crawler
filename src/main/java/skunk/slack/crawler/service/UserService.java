package skunk.slack.crawler.service;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import skunk.slack.crawler.data.dao.spec.UserDao;
import skunk.slack.crawler.data.entity.model.User;
import skunk.slack.crawler.httpaccess.client.SlackClient;

public class UserService {
	private SlackClient slackClient;
	private Map<String, User> users;
	private UserDao userDao;

	public UserService(SlackClient slackClient, UserDao userDao) {
		this.slackClient = slackClient;
		this.userDao = userDao;
	}

	public Set<User> getUsers() {
		return userDao.getAll();
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
