package skunk.slack.crawler.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;
import skunk.slack.crawler.data.entity.model.Channel;
import skunk.slack.crawler.data.entity.model.Message;
import skunk.slack.crawler.data.entity.model.User;
import skunk.slack.crawler.service.ServiceFactory;

@Slf4j
public class MessageProcessor {
	private static final Pattern REF_USER = Pattern.compile("<@([0-9A-Za-z]+)(\\|[^>]+)?>");
	private static final Pattern REF_CHANNEL = Pattern.compile("<#([0-9A-Za-z]+)(\\|[^>]+)?>");
	private static final Pattern URL = Pattern.compile("<(https?://[^>|]+)(\\|[^>]+)?>");
	private static final String URL_REP = "<a href='$1' target='_blank'>$1</a>";
	private static final Pattern VARIABLE = Pattern.compile("<!([a-zA-Z0-9]+)(\\|[^>]*)?>");
	private static final String VARIABLE_REP = "<a>@$1</a>";
	private static final Pattern ITALIC = Pattern.compile("(?=\\s|^)_([^_]+)_");
	private static final String ITALIC_REP = "<em>$1</em>";
	private static final Pattern BOLD = Pattern.compile("(?=\\s|^)\\*([^\\*]+)\\*");
	private static final String BOLD_REP = "<strong>$1</strong>";
	private static final Pattern STRIKE = Pattern.compile("(?=\\s|^)~([^~]+)~");
	private static final String STRIKE_REP = "<s>$1</s>";
	private static final Pattern CODE = Pattern.compile("`([^`]+)`");
	private static final String CODE_REP = "<code>$1</code>";
	private static final Pattern PRE = Pattern.compile("\\n?```\\n?([^`]+)\\n?```\\n?");
	private static final String PRE_REP = "</p><div class='well'>$1</div><p>";
	private static final Pattern QUOTE = Pattern.compile("(?m)^>([^\\n]*)\\n?$");
	private static final String QUOTE_REP = "<blockquote>$1</blockquote>";
	
	private static final Pattern LT = Pattern.compile("(<)(?![^<]+>)");
	private static final String LT_REP = "&lt;";
	private static final Pattern GT = Pattern.compile("(?<!<[^>]{1,100})(>)");
	private static final String GT_REP = "&gt;";
	private static final Pattern BR = Pattern.compile("\\n");
	private static final String BR_REP = "<br/>";

	public static String toHtml(Message message) {
		StringBuilder builder = new StringBuilder();
		builder.append("<p ");
		builder.append("class='message-body' ");
		builder.append("data-ts='").append(message.getTs()).append("' ");
		builder.append("data-type='").append(message.getType()).append("' ");
		builder.append(">");

		String text = message.getText();
		
		text = ITALIC.matcher(text).replaceAll(ITALIC_REP);
		text = BOLD.matcher(text).replaceAll(BOLD_REP);
		text = STRIKE.matcher(text).replaceAll(STRIKE_REP);
		text = PRE.matcher(text).replaceAll(PRE_REP);
		text = CODE.matcher(text).replaceAll(CODE_REP);
//		text = QUOTE.matcher(text).replaceAll(QUOTE_REP);
		text = text.replaceAll("</blockquote><blockquote>", "");

		text = URL.matcher(text).replaceAll(URL_REP);
		text = VARIABLE.matcher(text).replaceAll(VARIABLE_REP);

		Map<String, User> users = Maps.newHashMap();
		if (Objects.nonNull(message.getReferencedUsers())) {
			 users = message.getReferencedUsers().stream()
					.collect(Collectors.toMap(m -> m.getId(), m -> m));
		}
		Matcher userRefMatches = REF_USER.matcher(text);
		while (userRefMatches.find()) {
			String userId = userRefMatches.group(1);
			User referencedUser = users.get(userId);
			if (Objects.isNull(referencedUser)) {
				referencedUser = ServiceFactory.getUserService().getUser(userId);
			}
			if (Objects.nonNull(referencedUser)) {
				text = text.replace(userRefMatches.group(),
						"<a class='referenced-user' data-user-id='" + referencedUser.getId() + "' title='"
								+ referencedUser.getRealName() + "'>@" + referencedUser.getName() + "</a>");
			} else {
				log.error("User is not found [userId:{}, channelId: {}, messageTs:{}]", userId,
						message.getChannel().getId(), message.getTs());
			}
		}

		Matcher channelRefMatches = REF_CHANNEL.matcher(text);
		while (channelRefMatches.find()) {
			String channelId = channelRefMatches.group(1);
			Channel channel = ServiceFactory.getChannelService().getChannel(channelId);
			if (Objects.nonNull(channel)) {
				text = text.replace(channelRefMatches.group(), "<a class='referenced-user' data-user-id='"
						+ channel.getId() + "' title='" + channel.getName() + "'>#" + channel.getName() + "</a>");
			} else {
				log.error("Channel is not found [referencedChannelId:{}, channelId: {}, messageTs:{}]", channelId,
						message.getChannel().getId(), message.getTs());
			}
		}
		
//		text = LT.matcher(text).replaceAll(LT_REP);
//		text = GT.matcher(text).replaceAll(GT_REP);
		text = BR.matcher(text).replaceAll(BR_REP);
		
		builder.append(text);
		builder.append("</p>");

		return builder.toString();
	}

	public static Set<User> getReferencedUsers(Message message) {
		Matcher userRefMatches = REF_USER.matcher(message.getText());
		Set<User> referencedUsers = new HashSet<>();
		while (userRefMatches.find()) {
			User referencedUser = ServiceFactory.getUserService().getUser(userRefMatches.group(1));
			if (Objects.nonNull(referencedUser)) {
				referencedUsers.add(referencedUser);
			}
		}
		return referencedUsers;
	}

}
