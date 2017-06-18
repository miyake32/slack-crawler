package skunk.slack.crawler.util.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import lombok.extern.slf4j.Slf4j;
import skunk.slack.crawler.httpaccess.api.spec.SlackAPISpec;

@Slf4j
public class JsonResponseParser {
	private static Gson gson = new Gson();

	public static <E> JsonResponseParseResult<E> parseResponse(InputStream input, SlackAPISpec<E> responseConverter)
			throws IOException {
		InputStreamReader isr = new InputStreamReader(input);
		JsonReader reader = new JsonReader(isr);
		Map<String, JsonElementType> structure = responseConverter.getJsonResponseStructure();
		Set<String> jsonKeys = structure.keySet();
		JsonResponseParseResult<E> result = new JsonResponseParseResult<>(responseConverter);

		reader.beginObject();
		while (reader.hasNext()) {
			String key = reader.nextName();
			if (!jsonKeys.contains(key)) {
				gson.fromJson(reader, JsonElement.class);
				continue;
			}
			try {
				switch (structure.get(key)) {
				case OBJECT:
					result.put(key, gson.fromJson(reader, JsonObject.class));
					break;

				case ARRAY:
					result.put(key, gson.fromJson(reader, JsonArray.class));
					break;

				case STRING:
					result.put(key, reader.nextString());
					break;

				case NUMBER:
					String numberStr = reader.nextString();
					if (numberStr.contains(".")) {
						result.put(key, new BigDecimal(numberStr));
					} else {
						result.put(key, Long.parseLong(numberStr));
					}
					break;

				case BOOLEAN:
					result.put(key, reader.nextBoolean());
					break;

				case ARRAY_OF_TARGET_OBJECT:
					log.info("Start to read {}", key);
					processArrayOfTargetObject(reader, responseConverter, result);
					break;
				}
			} catch (IOException | JsonIOException | JsonSyntaxException e) {
				log.error("Exception occurred while processing {}", key);
				throw new IOException(e);
			}
		}
		return result;
	}

	private static <E> void processArrayOfTargetObject(JsonReader reader, SlackAPISpec<E> responseConverter,
			JsonResponseParseResult<E> result) throws JsonIOException, JsonSyntaxException, IOException {
		reader.beginArray();
		while (reader.hasNext()) {
			JsonObject obj = gson.fromJson(reader, JsonObject.class);
			result.add(responseConverter.getConverter().apply(obj));
		}
		reader.endArray();
		log.info("Finished to read target objects. Number of objects was {}", result.getList().size());
	}
}
