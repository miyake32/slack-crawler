package skunk.slack.crawler.controller;


import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import spark.Spark;
import spark.servlet.SparkApplication;

@Slf4j
public class ControllerInitializer implements SparkApplication {
	private static final List<Controller> CONTROLLERS = Arrays.asList(
			new CrawlerSwitchController(),
			new ViewerController()
			);

	@Override
	public void init() {
		CONTROLLERS.stream().forEach(c -> c.setRoutes());
		Spark.staticFileLocation("/static");
		log.info("finished initializing paths");
	}
}
