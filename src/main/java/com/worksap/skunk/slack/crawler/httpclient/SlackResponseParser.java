package com.worksap.skunk.slack.crawler.httpclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SlackResponseParser {
	private static Gson gson = new Gson();

	public static <E> Set<E> parseResponse(InputStream input, Function<JsonObject, E> converter) throws IOException {
		InputStreamReader isr = new InputStreamReader(input);
		JsonReader reader = new JsonReader(isr);
		reader.beginObject();
		if (!(reader.hasNext() && reader.nextName().equals("ok") && reader.nextBoolean())) {
			log.error("Failed to retrieve json");
			return null;
		}
		if (reader.hasNext()) {
			log.info("Start to read {}", reader.nextName());
		}
		reader.beginArray();
		
		Set<E> entitySet = new HashSet<>();
		while (reader.hasNext()) {
			JsonObject obj = gson.fromJson(reader, JsonObject.class);
			entitySet.add(converter.apply(obj));
		}
		reader.endArray();
		
		return entitySet;
	}
}
