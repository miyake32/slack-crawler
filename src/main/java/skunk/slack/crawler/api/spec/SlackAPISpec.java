package skunk.slack.crawler.api.spec;

import java.util.Map;
import java.util.function.Function;

import com.google.gson.JsonObject;

import skunk.slack.crawler.parser.JsonElementType;

public interface SlackAPISpec<T> {
	/**
	 * @return Function object that converts JsonObject to Entity Object 
	 */
	public Function<JsonObject, T> getConverter();
	
	/**
	 * Provide structure information about json response</br>
	 * Returned map may not contains unnecessary information for json-entity conversion</br>
	 * @return Map of top-level key names and type of corresponding values
	 */
	public Map<String, JsonElementType> getJsonResponseStructure();
	
	/**
	 * @return API Method name which will be appended to base url
	 */
	public String getApiMethod();
	
	/**
	 * @return key and value of prefixed API parameters
	 */
	public Map<String, String> getFixedParameter();
}
