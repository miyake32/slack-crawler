package skunk.slack.crawler.httpaccess.api.spec;

import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;

import skunk.slack.crawler.data.entity.model.Channel;
import skunk.slack.crawler.data.entity.type.ChannelType;
import skunk.slack.crawler.util.parser.JsonElementType;

public class ChannelsList implements SlackAPISpec<Channel> {
	private static final Map<String, JsonElementType> RESPONSE_STRUCTURE
					= ImmutableMap.<String, JsonElementType>builder()
						.put("ok", JsonElementType.BOOLEAN)
						.put("channels", JsonElementType.ARRAY_OF_TARGET_OBJECT)
						.build();
	private static final Function<JsonObject, Channel> CONVERTER = obj -> Channel.builder()
							.type(ChannelType.PUBLIC_CHANNEL)
							.id(obj.get("id").getAsString())
							.name(obj.get("name").getAsString())
							.isMember(obj.get("is_member").getAsBoolean()).build();
	private static final Map<String, String> FIXED_PARAMETERS
					= ImmutableMap.<String, String>builder()
						.put("exclude_members", "true").build();
	
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
		return "channels.list";
	}
	@Override
	public Map<String, String> getFixedParameter() {
		return FIXED_PARAMETERS;
	}
}
