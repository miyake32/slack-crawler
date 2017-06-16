package skunk.slack.crawler.main;

import java.util.List;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import skunk.slack.crawler.data.dao.impl.ChannelDaoHibernateImpl;
import skunk.slack.crawler.data.dao.impl.MessageDaoHibernateImpl;
import skunk.slack.crawler.data.dao.spec.ChannelDao;
import skunk.slack.crawler.data.dao.spec.MessageDao;
import skunk.slack.crawler.data.entity.model.Channel;
import skunk.slack.crawler.data.entity.model.Message;
import skunk.slack.crawler.httpclient.SlackClient;
import skunk.slack.crawler.httpclient.SlackClientGoogleImpl;
import skunk.slack.crawler.service.fetch.ChannelService;

@Slf4j
public class SlackCrawler {
	public static void main(String[] args) {
		SlackClient slackClient = new SlackClientGoogleImpl(SlackCrawlerPropertiesHolder.getTeamUrl(),
				SlackCrawlerPropertiesHolder.getToken());
		MessageDao messageDao = new MessageDaoHibernateImpl();
		ChannelDao channelDao = new ChannelDaoHibernateImpl();
		ChannelService channelService = new ChannelService(slackClient, channelDao);

		try {
			Set<Channel> channels = channelService.getChannels(c -> c.getIsMember());
			System.out.println(channels.size());
			for (Channel channel : channels) {
				log.info("start collect messages in {}", channel.getName());
				List<Message> messages = slackClient.getMessages(channel).getList();
				log.info("start save {} messages in {}", messages.size(), channel.getName());
				messageDao.save(messages);
				log.info("Finish");
			}
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

	}
}
