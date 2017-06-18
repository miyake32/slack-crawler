package skunk.slack.crawler.httpaccess.client;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import lombok.extern.slf4j.Slf4j;
import skunk.slack.crawler.data.entity.model.Channel;
import skunk.slack.crawler.data.entity.model.Message;
import skunk.slack.crawler.data.entity.model.User;
import skunk.slack.crawler.data.entity.type.ChannelType;
import skunk.slack.crawler.httpaccess.api.spec.ChannelsHistory;
import skunk.slack.crawler.httpaccess.api.spec.ChannelsList;
import skunk.slack.crawler.httpaccess.api.spec.GroupsHistory;
import skunk.slack.crawler.httpaccess.api.spec.GroupsList;
import skunk.slack.crawler.httpaccess.api.spec.SlackAPISpec;
import skunk.slack.crawler.httpaccess.api.spec.UsersList;
import skunk.slack.crawler.service.ServiceFactory;
import skunk.slack.crawler.service.UserService;
import skunk.slack.crawler.util.TimeStampUtils;
import skunk.slack.crawler.util.parser.JsonResponseParseResult;
import skunk.slack.crawler.util.parser.JsonResponseParser;

@Slf4j
public class SlackClientGoogleImpl implements SlackClient {
	private static HttpTransport httpTransport = new NetHttpTransport();
	private HttpRequestFactory requestFactory;
	private String baseUrl;
	private String token;

	public SlackClientGoogleImpl(String slackTeamUrl, String token) {
		StringBuilder url = new StringBuilder();
		this.baseUrl = url.append("https://").append(slackTeamUrl).append(".slack.com/api/").toString();
		this.token = token;
		this.requestFactory = httpTransport.createRequestFactory();
	}

	public JsonResponseParseResult<User> getUsers() {
		GenericUrl url = new GenericUrl(baseUrl + "users.list");
		url.set("token", this.token);
		try {
			HttpResponse response = requestFactory.buildGetRequest(url).execute();
			return JsonResponseParser.parseResponse(response.getContent(), new UsersList());
		} catch (IOException e) {
			log.error("Exception occurred while getting users", e);
			return null;
		}
	}

	@Override
	public JsonResponseParseResult<Channel> getPublicChannels() {
		return getChannel(ChannelType.PUBLIC_CHANNEL);
	}

	@Override
	public JsonResponseParseResult<Channel> getPrivateChannels() {
		return getChannel(ChannelType.PRIVATE_CHANNEL);
	}

	@Override
	public JsonResponseParseResult<Channel> getDirectMessages() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonResponseParseResult<Channel> getMultipartyDirectMessages() {
		// TODO Auto-generated method stub
		return null;
	}

	private JsonResponseParseResult<Channel> getChannel(ChannelType type) {
		SlackAPISpec<Channel> spec = null;
		switch (type) {
		case PUBLIC_CHANNEL:
			spec = new ChannelsList();
			break;
		case PRIVATE_CHANNEL:
			spec = new GroupsList();
			break;
		default:
			break;
		}
		GenericUrl url = new GenericUrl(baseUrl + spec.getApiMethod());
		setPrefixedParameter(url, spec);
		url.set("token", token);
		try {
			HttpResponse response = requestFactory.buildGetRequest(url).execute();
			return JsonResponseParser.parseResponse(response.getContent(), spec);
		} catch (IOException e) {
			log.error("Exception occurred while getting users", e);
			return null;
		}
	}

	@Override
	public JsonResponseParseResult<Message> getMessages(Channel channel) {
		Objects.requireNonNull(channel);
		SlackAPISpec<Message> spec = null;
		UserService userService = ServiceFactory.getUserService();

		switch (channel.getType()) {
		case PUBLIC_CHANNEL:
			spec = new ChannelsHistory(userService);
			break;
		case PRIVATE_CHANNEL:
		case MULTIPARTY_DIRECT_MESSAGE:
			spec = new GroupsHistory(userService);
			break;
		}
		log.info("Start to retrieve channel {}", channel.getName());
		Boolean hasMore = true;
		String latest = channel.getLastFetchedTs();
		String nextOldest = TimeStampUtils.incrementTs(latest);
		Set<Message> messageSet = new HashSet<>();
		JsonResponseParseResult<Message> result = null;
		SlackAPISpec<Message> converter = new ChannelsHistory(ServiceFactory.getUserService());

		while (hasMore) {
			log.info("Retrieve channel from {}, limit {}", channel.getLastFetchedTs(), 1000);
			GenericUrl url = new GenericUrl(baseUrl + spec.getApiMethod());

			url.set("token", this.token).set("channel", channel.getId()).set("count", 1000).set("oldest", nextOldest)
					.set("inclusive", true);
			try {
				HttpResponse response = requestFactory.buildGetRequest(url).execute();
				result = JsonResponseParser.parseResponse(response.getContent(), converter);
				messageSet.addAll(result.getList());
				hasMore = result.getBoolean("has_more");
				if (!result.getList().isEmpty()) {
					latest = result.getList().stream().filter(Objects::nonNull).max((a, b) -> {
						if (Objects.isNull(a.getTimeStamp())) {
							return -1;
						}
						if (Objects.isNull(b.getTimeStamp())) {
							return 1;
						}
						return a.getTimeStamp().compareTo(b.getTimeStamp());
					}).get().getTs();
					nextOldest = TimeStampUtils.incrementTs(latest);
				}
			} catch (IOException e) {
				log.error("Exception occurred while getting users", e);
				return null;
			}
		}
		result.set(messageSet.stream().map(msg -> {
			msg.setChannel(channel);
			return msg;
		}).sorted().collect(Collectors.toList()));
		return result;
	}

	private void setPrefixedParameter(GenericUrl url, SlackAPISpec<?> spec) {
		spec.getFixedParameter().entrySet().stream().forEach(e -> url.set(e.getKey(), e.getValue()));
	}
}