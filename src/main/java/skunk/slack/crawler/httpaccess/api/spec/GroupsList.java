package skunk.slack.crawler.httpaccess.api.spec;

import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;

import skunk.slack.crawler.data.entity.model.Channel;
import skunk.slack.crawler.data.entity.model.Channel.ChannelBuilder;
import skunk.slack.crawler.data.entity.type.ChannelType;
import skunk.slack.crawler.util.parser.JsonElementType;

public class GroupsList implements SlackAPISpec<Channel> {
	private static final Map<String, JsonElementType> RESPONSE_STRUCTURE = ImmutableMap
			.<String, JsonElementType> builder().put("ok", JsonElementType.BOOLEAN)
			.put("groups", JsonElementType.ARRAY_OF_TARGET_OBJECT).build();
	private static final Function<JsonObject, Channel> CONVERTER = obj -> {
							ChannelBuilder builder = Channel.builder()
									.type(ChannelType.PRIVATE_CHANNEL)
									.id(obj.get("id").getAsString())
									.name(obj.get("name").getAsString())
									.isMember(true);
							
							String rawName = obj.get("name").getAsString();
							if (rawName.matches("^mpdm-(.+--){2,}.*$")) {
								builder.type(ChannelType.MULTIPARTY_DIRECT_MESSAGE);
								builder.name(rawName.replaceAll("^mpdm-", "").replaceAll("--", ",").replaceAll("-[0-9]+$", ""));
							} else {
								builder.type(ChannelType.PRIVATE_CHANNEL).name(rawName);
							}
							
							return builder.build();
							};
	private static final Map<String, String> FIXED_PARAMETERS = ImmutableMap.<String, String> builder()
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
