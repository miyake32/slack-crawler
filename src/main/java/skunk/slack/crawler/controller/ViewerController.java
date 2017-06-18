package skunk.slack.crawler.controller;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;
import skunk.slack.crawler.data.entity.model.Channel;
import skunk.slack.crawler.data.entity.model.Message;
import skunk.slack.crawler.data.entity.model.User;
import skunk.slack.crawler.service.ServiceFactory;
import skunk.slack.crawler.util.ModelCreator;
import skunk.slack.crawler.util.TimeStampUtils;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

@Slf4j
public class ViewerController implements Controller {
	private static List<String> CHANNEL_IDS = Arrays.asList("C0JEDJ5C3", "C0JEBVBHB", "C3Z7RTHCK", "G5QKEMHA8");
	private TemplateViewRoute viewer = new TemplateViewRoute() {
		@Override
		public ModelAndView handle(Request request, Response response) throws Exception {
			List<Channel> channels = ServiceFactory.getChannelService()
					.getChannels(c -> CHANNEL_IDS.contains(c.getId())).stream()
					.sorted((a, b) -> a.getName().compareTo(b.getName())).collect(Collectors.toList());

			Map<String, Object> model = new HashMap<>();
			model.put("channels", ModelCreator.createModelFromCollection(channels));

			return new ModelAndView(model, "viewer");
		}
	};
	private Route getMessages = new Route() {
		@Override
		public Object handle(Request request, Response response) throws Exception {
			String channelId = request.queryParams("channelId");
			String currentMinTs = request.queryParams("currentMinTs");
			String maxTs;
			if (!CHANNEL_IDS.contains(channelId)) {
				log.error("channelId {} is not allowed", channelId);
				response.status(403);
				return null;
			}
			if (Strings.isNullOrEmpty(currentMinTs)) {
				maxTs = TimeStampUtils.now();
			} else {
				maxTs = TimeStampUtils.decrementTs(currentMinTs);
			}
			List<Message> messages = ServiceFactory.getMessageService().getMessages(channelId, 1000, maxTs);
			return ModelCreator.createJsonModelFromCollection(messages);
		}
	};
	
	private Route getUsers = new Route() {
		
		@Override
		public Object handle(Request request, Response response) throws Exception {
			Collection<User> users = ServiceFactory.getUserService().getUsers();
			return ModelCreator.createJsonModelFromCollection(users);
		}
	};

	@Override
	public void setRoutes() {
		Spark.get("/", viewer, new ThymeleafTemplateEngine());
		Spark.get("/getMessages", getMessages);
		Spark.get("/getUsers", getUsers);
	}
}
