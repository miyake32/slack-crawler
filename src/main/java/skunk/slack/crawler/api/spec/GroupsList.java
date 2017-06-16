package skunk.slack.crawler.api.spec;

import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;

import skunk.slack.crawler.data.entity.model.Channel;
import skunk.slack.crawler.data.entity.type.ChannelType;
import skunk.slack.crawler.parser.JsonElementType;

public class GroupsList implements SlackAPISpec<Channel> {
	private static final Map<String, JsonElementType> RESPONSE_STRUCTURE
					= ImmutableMap.<String, JsonElementType>builder()
						.put("ok", JsonElementType.BOOLEAN)
						.put("groups", JsonElementType.ARRAY_OF_TARGET_OBJECT)
						.build();
	private static final Function<JsonObject, Channel> CONVERTER = obj -> Channel.builder()
							.type(ChannelType.PRIVATE_CHANNEL)
							.id(obj.get("id").getAsString())
							.name(obj.get("name").getAsString())
							.isMember(true).build();
	private static final Map<String, String> FIXED_PARAMETERS
				= ImmutableMap.<String, String>builder()
						.put("exclude_archived", "false").build();
	
	@Override
	public Function<JsonObject, Channel> getConverter() {
		return CONVERTER;
	}
	@Override
	public Map<String, JsonElementType> getJsonResponseStructure() {
		return RESPONSE_STRUCTURE;
	}
	@Override
	public String getApiMethod() {
		return "groups.list";
	}
	@Override
	public Map<String, String> getFixedParameter() {
		return FIXED_PARAMETERS;
	}
}
