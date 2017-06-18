package skunk.slack.crawler.util;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

import com.google.api.client.repackaged.com.google.common.base.Splitter;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimeStampUtils {
	public static Timestamp convertSlackTsToTimeStamp(String ts) {
		// unixTimeStamp = System.currentTimeMillis() / 1000L
		Long unixTimeStamp = Long.parseLong(Splitter.on(".").splitToList(ts).get(0));
		Long timeMills = unixTimeStamp * 1000;
		return new Timestamp(timeMills);
	}
	private static int COUNTER_LENGTH = 6;
	
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
		String newCounterStr = String.format("%0" + COUNTER_LENGTH +"d", newCounterLong);
		
		return unixTimeStamp + "." + newCounterStr;
	}
	
	public static String decrementTs(String ts) {
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
		
		Long newCounterLong = Long.parseLong(counterStr) - 1;
		if (newCounterLong < 0) {
			Long newUnixTimeStampLong = Long.parseLong(unixTimeStamp) - 1;
			unixTimeStamp = newUnixTimeStampLong.toString();
			newCounterLong = 0L;
		}
		String newCounterStr = String.format("%0" + COUNTER_LENGTH +"d", newCounterLong);
		
		return unixTimeStamp + "." + newCounterStr;
	}

	
	public static String now() {
		Long unixTimeStamp = System.currentTimeMillis() / 1000L;
		String counter = Strings.repeat("9", COUNTER_LENGTH);
		return unixTimeStamp.toString() + "." + counter;
	}
}
