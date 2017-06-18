package skunk.slack.crawler.controller;

import java.util.TimerTask;

import skunk.slack.crawler.controller.response.CrawlerSwitchControllerResponse;
import skunk.slack.crawler.service.SlackCrawler;
import skunk.slack.crawler.util.ModelCreator;
import skunk.slack.crawler.util.TaskManager;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

class CrawlerSwitchController implements Controller {
	private static String CRAWLER_TASK_KEY = "SLACK_CRAWLER";
	private static int MINS_INTERVAL = 60;

	@Override
	public void setRoutes() {
		Spark.get("crawler/enable", enableCrawler);
		Spark.get("crawler/disable", disableCrawler);
	}

	private static Route enableCrawler = new Route() {
		@Override
		public Object handle(Request request, Response response) throws Exception {
			if (TaskManager.isStarted(CRAWLER_TASK_KEY)) {
				return ModelCreator.createJsonModel(
						CrawlerSwitchControllerResponse.builder().enabled(true).minsInterval(MINS_INTERVAL).build());
			}
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					SlackCrawler.crawl();
				}
			};
			TaskManager.start(CRAWLER_TASK_KEY, task, MINS_INTERVAL);
			return ModelCreator.createJsonModel(
					CrawlerSwitchControllerResponse.builder().enabled(true).minsInterval(MINS_INTERVAL).build());
		}
	};

	private static Route disableCrawler = new Route() {
		@Override
		public Object handle(Request request, Response response) throws Exception {
			if (!TaskManager.isStarted(CRAWLER_TASK_KEY)) {
				return ModelCreator.createJsonModel(
						CrawlerSwitchControllerResponse.builder().enabled(false).minsInterval(MINS_INTERVAL).build());
			}
			TaskManager.stop(CRAWLER_TASK_KEY);
			return ModelCreator.createJsonModel(
					CrawlerSwitchControllerResponse.builder().enabled(false).minsInterval(MINS_INTERVAL).build());
		}
	};
}
