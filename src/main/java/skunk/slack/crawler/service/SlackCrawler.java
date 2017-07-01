package skunk.slack.crawler.service;

import java.util.List;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import skunk.slack.crawler.data.entity.model.Channel;
import skunk.slack.crawler.data.entity.model.Message;

@Slf4j
public class SlackCrawler {
	public static void main(String[] args) {
		System.exit(crawl());
	}

	public static int crawl() {
		int exceptionCount = 0;
		Set<Channel> channels = ServiceFactory.getChannelService().fetchChannels(c -> c.getIsMember());
		log.info("Number of fetched channels : {}", channels.size());
		for (Channel channel : channels) {
			try {
				log.info("start collect messages in {}", channel.getName());
				List<Message> messages = ServiceFactory.getSlackClient().getMessages(channel).getList();
				log.info("start save {} messages in {}", messages.size(), channel.getName());
				ServiceFactory.getMessageService().save(messages);
				log.info("Finish");
			} catch (Exception e) {
				exceptionCount++;
				log.error("Error occurred while fetching {}", channel.getName(), e);
			}
		}
		return exceptionCount;
	}
}
