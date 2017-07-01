package skunk.slack.crawler.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import skunk.slack.crawler.data.entity.model.User;
import skunk.slack.crawler.service.ServiceFactory;
import skunk.slack.crawler.util.ModelCreator;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

public class UserController implements Controller {

	@Override
	public void setRoutes() {
		Spark.get("user/all", all);
	}

	private static Route all = new Route() {
		@Override
		public Object handle(Request request, Response response) throws Exception {
			Set<User> users = ServiceFactory.getUserService().getUsers();

			Set<Map<String, Object>> model = users.stream().map(u -> {
				Map<String, Object> map = new HashMap<>();
				map.put("name", u.getName());
				map.put("realName", u.getRealName());
				return map;
			}).collect(Collectors.toSet());
			return ModelCreator.createJsonModelFromCollectionOfMap(model);
		}
	};
}
