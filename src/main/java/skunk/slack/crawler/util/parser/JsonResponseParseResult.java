package skunk.slack.crawler.util.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import skunk.slack.crawler.httpaccess.api.spec.SlackAPISpec;

public class JsonResponseParseResult<E> {
	public JsonResponseParseResult(SlackAPISpec<E> converter) {
		this.jsonStructure = converter.getJsonResponseStructure();
		this.list = new ArrayList<>();
		this.supplementaryInfo = new HashMap<>();
	}
	private List<E> list;
	private Map<String, Object> supplementaryInfo;
	private Map<String, JsonElementType> jsonStructure;
	
	public List<E> getList() {
		return list;
	}
	
	public String getString(String key) {
		if (this.jsonStructure.get(key) != JsonElementType.STRING) {
			return null;
		}
		return (String) this.supplementaryInfo.get(key);
	}
	
	public Number getNumber(String key) {
		if (this.jsonStructure.get(key) != JsonElementType.NUMBER) {
			return null;
		}
		return (Number) this.supplementaryInfo.get(key);
	}
	
	public Boolean getBoolean(String key) {
		if (this.jsonStructure.get(key) != JsonElementType.BOOLEAN) {
			return null;
		}
		return (Boolean) this.supplementaryInfo.get(key);
	}
	
	public JsonObject getObject(String key) {
		if (this.jsonStructure.get(key) != JsonElementType.OBJECT) {
			return null;
		}
		return (JsonObject) this.supplementaryInfo.get(key);
	}
	
	public JsonArray getArray(String key) {
		if (this.jsonStructure.get(key) != JsonElementType.ARRAY) {
			return null;
		}
		return (JsonArray) this.supplementaryInfo.get(key);
	}
	
	public void put(String key, Object value) {
		this.supplementaryInfo.put(key, value);
	}
	
	public void add(E entity) {
		this.list.add(entity);
	}
	
	public void set(List<E> list) {
		this.list = list;
	}
}
