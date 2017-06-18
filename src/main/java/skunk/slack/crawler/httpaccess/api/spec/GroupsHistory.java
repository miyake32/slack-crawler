package skunk.slack.crawler.httpaccess.api.spec;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import skunk.slack.crawler.data.entity.model.Message;
import skunk.slack.crawler.data.entity.model.User;
import skunk.slack.crawler.service.UserService;
import skunk.slack.crawler.util.parser.JsonElementType;

public class GroupsHistory implements SlackAPISpec<Message> {
	private static final Map<String, JsonElementType> RESPONSE_STRUCTURE = ImmutableMap
			.<String, JsonElementType> builder().put("ok", JsonElementType.BOOLEAN)
			.put("has_more", JsonElementType.BOOLEAN).put("messages", JsonElementType.ARRAY_OF_TARGET_OBJECT).build();
	private final Function<JsonObject, Message> converter = obj -> {
		String text = obj.get("text").getAsString();
		Message message = Message.builder()
				.type(obj.get("type").getAsString())
				.ts(obj.get("ts").getAsString())
				.text(text).build();
		JsonElement user = obj.get("user");
		if (Objects.nonNull(user)) {
			message.setUser(this.userFetchService.getUser(user.getAsString()));
		}
		Pattern userRefPattern = Pattern.compile("<@([A-Za-z0-9]+)(\\|.*)?>");
		Matcher userRefMatches = userRefPattern.matcher(text);
		Set<User> referencedUsers = new HashSet<>();
		while (userRefMatches.find()) {
			referencedUsers.add(this.userFetchService.getUser(userRefMatches.group(1)));
		}
		message.setReferencedUsers(referencedUsers);
		return message;
	};
	private UserService userFetchService;

	public GroupsHistory(UserService userFetchService) {
		this.userFetchService = userFetchService;
	}

	@Override
	public Function<JsonObject, Message> getConverter() {
		return converter;
	}

	@Override
	public Map<String, JsonElementType> getJsonResponseStructure() {
		return RESPONSE_STRUCTURE;
	}

	@Override
	public String getApiMethod() {
		return "groups.history";
	}

	@Override
	public Map<String, String> getFixedParameter() {
		// TODO Auto-generated method stub
		return null;
	}
}
