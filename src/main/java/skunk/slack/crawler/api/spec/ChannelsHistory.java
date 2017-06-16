package skunk.slack.crawler.api.spec;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import skunk.slack.crawler.data.entity.model.Message;
import skunk.slack.crawler.parser.JsonElementType;
import skunk.slack.crawler.service.fetch.UserService;

public class ChannelsHistory implements SlackAPISpec<Message> {
	private static final Map<String, JsonElementType> RESPONSE_STRUCTURE = ImmutableMap
			.<String, JsonElementType> builder().put("ok", JsonElementType.BOOLEAN)
			.put("has_more", JsonElementType.BOOLEAN).put("messages", JsonElementType.ARRAY_OF_TARGET_OBJECT).build();
	private final Function<JsonObject, Message> converter = obj -> {
		Message message = Message.builder()
				.messageType(obj.get("type").getAsString())
				.ts(obj.get("ts").getAsString())
				.text(obj.get("text").getAsString()).build();
		JsonElement user = obj.get("user");
		if (Objects.nonNull(user)) {
			message.setUser(this.userFetchService.getUser(user.getAsString()));
		}
		return message;
	};
	private UserService userFetchService;

	public ChannelsHistory(UserService userFetchService) {
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
		return "channels.history";
	}

	@Override
	public Map<String, String> getFixedParameter() {
		// TODO Auto-generated method stub
		return null;
	}
}
