package skunk.slack.crawler.controller.response;

import lombok.Data;
import lombok.experimental.Builder;

@SuppressWarnings("deprecation")
@Builder
@Data
public class CrawlerSwitchControllerResponse {
	private Boolean enabled;
	private Integer minsInterval;
}
