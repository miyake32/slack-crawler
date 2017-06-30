package skunk.slack.crawler.util;

import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import skunk.slack.crawler.data.entity.model.Channel;
import skunk.slack.crawler.data.entity.model.Message;
import skunk.slack.crawler.data.entity.model.User;
import skunk.slack.crawler.service.ServiceFactory;
import skunk.slack.crawler.service.data.ChannelService;
import skunk.slack.crawler.service.data.UserService;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ServiceFactory.class)
public class MessageProcessorTest {
	private static Message TEST_MESSAGE = Message.builder()
			.text("<@U111111|test1> testmessage\n<http://testurl.com> was mensioned in <#C111111|channel>\n<@U222222|test2> Please look it.")
			.build();
	private static User USER1 = User.builder().id("U111111").realName("Test User1").name("test1").build();
	private static User USER2 = User.builder().id("U222222").realName("Test User2").name("test2").build();
	private static Channel CHANNEL1 = Channel.builder().id("C111111").name("test-channel1").build();

	@Before
	public void prepare() throws NoSuchMethodException, SecurityException, Exception {
		PowerMockito.mockStatic(ServiceFactory.class);

		UserService mockUserService = Mockito.mock(UserService.class);
		Mockito.when(mockUserService.getUser("U111111")).thenReturn(USER1);
		Mockito.when(mockUserService.getUser("U222222")).thenReturn(USER2);
		PowerMockito.when(ServiceFactory.getUserService()).thenReturn(mockUserService);

		ChannelService mockChannelService = Mockito.mock(ChannelService.class);
		Mockito.when(mockChannelService.getChannel("C111111")).thenReturn(CHANNEL1);
		PowerMockito.when(ServiceFactory.getChannelService()).thenReturn(mockChannelService);
	}

	@Test
	public void slackVariablesTest() throws ParserConfigurationException {
		String html = MessageProcessor.toHtml(TEST_MESSAGE);
		Assert.assertEquals(
				"<p class='message-body'data-ts='null' data-type='null' ><a class='referenced-user' data-user-id='U111111' title='Test User1'>@test1</a> testmessage<br/><a href='http://testurl.com' target='_blank'>http://testurl.com</a> was mensioned in <a class='referenced-user' data-user-id='C111111' title='test-channel1'>#test-channel1</a><br/><a class='referenced-user' data-user-id='U222222' title='Test User2'>@test2</a> Please look it.</p>",
				html);
	}
	
	@Test
	public void boldTest() throws ParserConfigurationException {
		Message message = Message.builder().text("aaa*bold text*bbb").build();
		Assert.assertTrue(MessageProcessor.toHtml(message).contains("aaa<strong>bold text</strong>bbb"));
	}
	
	@Test
	public void italicTest() throws ParserConfigurationException {
		Message message = Message.builder().text("aaa _italic text_ bbb_not_italic_ccc").build();
		Assert.assertTrue(MessageProcessor.toHtml(message).contains("aaa <em>italic text</em> bbb_not_italic_ccc"));
	}
	
	@Test
	public void codeTest() throws ParserConfigurationException {
		Message message = Message.builder().text("aaa`code text`bbb").build();
		Assert.assertTrue(MessageProcessor.toHtml(message).contains("aaa<code>code text</code>bbb"));
	}
	
	@Test
	public void preTest() throws ParserConfigurationException {
		Message message = Message.builder().text("aaa\n```aiueo\nkakiku\nsashisu```bbb").build();
		System.out.println(MessageProcessor.toHtml(message));
		Assert.assertTrue(MessageProcessor.toHtml(message).contains("aaa</p><div class='well'>aiueo<br/>kakiku<br/>sashisu</div><p>bbb"));
	}

	@Test
	public void getReferencedUsersTest() {
		Set<User> refUsers = MessageProcessor.getReferencedUsers(TEST_MESSAGE);
		Assert.assertTrue(refUsers.contains(USER1));
		Assert.assertTrue(refUsers.contains(USER2));
		Assert.assertEquals(2, refUsers.size());
	}
}
