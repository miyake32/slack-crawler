package skunk.slack.crawler.main;

import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

import com.google.api.client.repackaged.com.google.common.base.Splitter;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SlackCrawlerPropertiesHolder {
	private static Properties PROPERTIES = null;
	private static final String FILE = "slack-crawler.properties";

	public static void readProperties() {
		PROPERTIES = new Properties();
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			InputStream inputStream = classLoader.getResourceAsStream(FILE);
			PROPERTIES.load(inputStream);
			inputStream.close();
		} catch (Exception e) {
			log.error("Exception occurred while processing {}", FILE, e);
		}
	}

	private static String getProperty(String key) {
		if (Objects.isNull(PROPERTIES)) {
			readProperties();
		}
		return PROPERTIES.getProperty(key);
	}
	
	public static String getTeamUrl() {
		return getProperty("team_url");
	}

	public static String getToken() {
		return getProperty("token");
	}

	public static Set<String> getTargetPublicChannels() {
		return Sets.newHashSet(
				Splitter.on(",")
				.omitEmptyStrings()
				.split(getProperty("public_channels")));
	}
}
