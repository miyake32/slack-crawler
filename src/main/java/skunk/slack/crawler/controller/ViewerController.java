package skunk.slack.crawler.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Strings;

import skunk.slack.crawler.data.entity.model.Channel;
import skunk.slack.crawler.data.entity.model.Message;
import skunk.slack.crawler.service.PropertiesHolder;
import skunk.slack.crawler.service.ServiceFactory;
import skunk.slack.crawler.util.MessageProcessor;
import skunk.slack.crawler.util.ModelCreator;
import skunk.slack.crawler.util.TimeStampUtils;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

public class ViewerController implements Controller {
	private TemplateViewRoute getViewer(Set<String> channelNames) {
		return new TemplateViewRoute() {
			@Override
			public ModelAndView handle(Request request, Response response) throws Exception {
				List<Channel> channels = ServiceFactory.getChannelService().getChannels().stream()
						.filter(c -> Objects.isNull(channelNames) || channelNames.contains(c.getName()))
						.sorted((a, b) -> a.getType().compareTo(b.getType()) * 100 + a.getName().compareTo(b.getName()))
						.collect(Collectors.toList());
				Map<String, Object> model = new HashMap<>();
				model.put("channels", ModelCreator.createModelFromCollection(channels));

				return new ModelAndView(model, "viewer");
			}
		};
	}

	private Route getMessages = new Route() {
		@Override
		public Object handle(Request request, Response response) throws Exception {
			String channelId = request.queryParams("channelId");
			String currentMinTs = request.queryParams("currentMinTs");
			String maxTs;
			if (Strings.isNullOrEmpty(currentMinTs)) {
				maxTs = TimeStampUtils.now();
			} else {
				maxTs = TimeStampUtils.decrementTs(currentMinTs);
			}
			List<Message> messages = ServiceFactory.getMessageService().getMessages(channelId, 30, maxTs);
			List<Map<String, Object>> model = messages.stream().map(m -> {
				Map<String, Object> map = new HashMap<>();
				if (Objects.nonNull(m.getUser())) {
					map.put("user", m.getUser().getName());
					map.put("userRealName", m.getUser().getRealName());
				}
				map.put("ts", m.getTs());
				map.put("type", m.getType());
				map.put("text", MessageProcessor.toHtml(m));
				return map;
			}).collect(Collectors.toList());
			return ModelCreator.createJsonModelFromCollectionOfMap(model);
		}
	};

	@Override
	public void setRoutes() {
		Spark.get("/", getViewer(PropertiesHolder.getOpenChannels()), new ThymeleafTemplateEngine());
		Spark.get("/secret/" + PropertiesHolder.getSecretPath(), getViewer(null), new ThymeleafTemplateEngine());
		Spark.get("/getMessages", getMessages);
	}
}
