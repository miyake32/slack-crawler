package skunk.slack.crawler.service;

import java.util.List;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import skunk.slack.crawler.data.entity.model.Channel;
import skunk.slack.crawler.data.entity.model.Message;

@Slf4j
public class SlackCrawler {
	public static void main(String[] args) {
		try {
			crawl();
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void crawl() {
		Set<Channel> channels = ServiceFactory.getChannelService().fetchChannels(c -> c.getIsMember());
		System.out.println(channels.size());
		for (Channel channel : channels) {
			log.info("start collect messages in {}", channel.getName());
			List<Message> messages = ServiceFactory.getSlackClient().getMessages(channel).getList();
			log.info("start save {} messages in {}", messages.size(), channel.getName());
			ServiceFactory.getMessageService().save(messages);
			log.info("Finish");
		}
	}
}
