package skunk.slack.crawler.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.api.client.repackaged.com.google.common.base.Splitter;

import skunk.slack.crawler.data.entity.model.Message;
import skunk.slack.crawler.service.ServiceFactory;
import skunk.slack.crawler.util.MessageProcessor;
import skunk.slack.crawler.util.ModelCreator;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

public class SearchController implements Controller {

	@Override
	public void setRoutes() {
		Spark.get("search", search);
	}

	private static Route search = new Route() {
		@Override
		public Object handle(Request request, Response response) throws Exception {
			String keyword = request.queryParams("keyword");
			Collection<String> keywords = Splitter.on(" ").omitEmptyStrings().splitToList(keyword);
			String channel = request.queryParams("channel");
			Collection<String> channels = Splitter.on(" ").omitEmptyStrings().splitToList(channel);
			List<Message> messages = ServiceFactory.getMessageService().search(keywords, channels);
			
			List<Map<String, Object>> model = messages.stream().map(m -> {
				Map<String, Object> map = new HashMap<>();
				if (Objects.nonNull(m.getUser())) {
					map.put("user", m.getUser().getName());
					map.put("userRealName", m.getUser().getRealName());
				}
				map.put("ts", m.getTs());
				map.put("type", m.getType());
				map.put("text", MessageProcessor.toHtml(m));
				map.put("channelId", m.getChannel().getId());
				map.put("channelName", m.getChannel().getName());
				return map;
			}).collect(Collectors.toList());
			return ModelCreator.createJsonModelFromCollectionOfMap(model);
		}
	};
}
