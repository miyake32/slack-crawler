package skunk.slack.crawler.util;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

import com.google.api.client.repackaged.com.google.common.base.Splitter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimeStampUtils {
	public static Timestamp convertSlackTsToTimeStamp(String ts) {
		// unixTimeStamp = System.currentTimeMillis() / 1000L
		Long unixTimeStamp = Long.parseLong(Splitter.on(".").splitToList(ts).get(0));
		Long timeMills = unixTimeStamp * 1000;
		return new Timestamp(timeMills);
	}
	
	public static String incrementTs(String ts) {
		if (Objects.isNull(ts)) {
			return "0";
		}
		List<String> splittedTs = Splitter.on(".").splitToList(ts);
		if (splittedTs.size() != 2) {
			IllegalArgumentException e = new IllegalArgumentException("Invalid ts format");
			log.error("Exception occurred during processing ts : {}", ts);
			throw e;
		}
		String unixTimeStamp = splittedTs.get(0);
		String counterStr = splittedTs.get(1);
		
		Long newCounterLong = Long.parseLong(counterStr) + 1;
		String newCounterStr = String.format("%0" + counterStr.length() +"d", newCounterLong);
		
		return unixTimeStamp + "." + newCounterStr;
	}
}
