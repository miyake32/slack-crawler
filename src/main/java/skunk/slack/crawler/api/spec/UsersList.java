package skunk.slack.crawler.api.spec;

import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;

import skunk.slack.crawler.data.entity.model.User;
import skunk.slack.crawler.parser.JsonElementType;

public class UsersList implements SlackAPISpec<User> {
	private static final Map<String, JsonElementType> RESPONSE_STRUCTURE = ImmutableMap
			.<String, JsonElementType> builder().put("ok", JsonElementType.BOOLEAN)
			.put("members", JsonElementType.ARRAY_OF_TARGET_OBJECT).build();
	private static final Function<JsonObject, User> CONVERTER = obj -> User.builder().id(obj.get("id").getAsString())
			.name(obj.get("name").getAsString())
			.realName(obj.get("profile").getAsJsonObject().get("real_name").getAsString()).build();

	@Override
	public Function<JsonObject, User> getConverter() {
		return CONVERTER;
	}

	@Override
	public Map<String, JsonElementType> getJsonResponseStructure() {
		return RESPONSE_STRUCTURE;
	}

	@Override
	public String getApiMethod() {
		return "users.list";
	}

	@Override
	public Map<String, String> getFixedParameter() {
		// TODO Auto-generated method stub
		return null;
	}
}
