package skunk.slack.crawler.httpclient;

import skunk.slack.crawler.data.entity.model.Channel;
import skunk.slack.crawler.data.entity.model.Message;
import skunk.slack.crawler.data.entity.model.User;
import skunk.slack.crawler.parser.JsonResponseParseResult;

public interface SlackClient {
	public JsonResponseParseResult<User> getUsers();
	public JsonResponseParseResult<Channel> getPublicChannels();
	public JsonResponseParseResult<Channel> getPrivateChannels();
	public JsonResponseParseResult<Channel> getDirectMessages();
	public JsonResponseParseResult<Channel> getMultipartyDirectMessages();
	public JsonResponseParseResult<Message> getMessages(Channel channel);
}
