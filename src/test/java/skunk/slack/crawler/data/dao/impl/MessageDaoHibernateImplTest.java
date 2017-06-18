package skunk.slack.crawler.data.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Transaction;
import org.junit.Test;

import skunk.slack.crawler.data.AbstractHibernateDataAccessTest;
import skunk.slack.crawler.data.dao.impl.MessageDaoHibernateImpl;
import skunk.slack.crawler.data.dao.spec.MessageDao;
import skunk.slack.crawler.data.entity.model.Channel;
import skunk.slack.crawler.data.entity.model.Message;
import skunk.slack.crawler.data.entity.model.User;
import skunk.slack.crawler.data.entity.type.ChannelType;

public class MessageDaoHibernateImplTest extends AbstractHibernateDataAccessTest {
	private static MessageDao messageDao = new MessageDaoHibernateImpl();
	@Test
	public void saveTest() {
		Transaction tx = getSession().beginTransaction();
		List<Message> messages = new ArrayList<>();
		Channel channel = new Channel();
		channel.setId("test");
		channel.setIsMember(true);
		channel.setName("channel");
		channel.setType(ChannelType.PUBLIC_CHANNEL);
		
		User user1 = new User();
		user1.setId("00001");
		user1.setName("first");
		user1.setRealName("First User");
		
		Message message1 = new Message();
		message1.setChannel(channel);
		message1.setUser(user1);
		message1.setTs("1358546515.000008");
		message1.setType("message");
		message1.setText("this is first message");
		
		messages.add(message1);
		
		messageDao.save(messages);
		tx.commit();
	}
}
