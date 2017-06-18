package skunk.slack.crawler.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TaskManager {
	private static Map<String, Timer> REGISTERED_TASKS = new HashMap<>();

	public static void start(String key, TimerTask task, int minsInterval) {
		if (REGISTERED_TASKS.containsKey(key)) {
			throw new IllegalArgumentException(key + "is already started");
		}
		Timer timer = new Timer(true);
		timer.schedule(task, 0, minsInterval * 60000);
		REGISTERED_TASKS.put(key, timer);
	}
	
	public static void stop(String key) {
		if (!REGISTERED_TASKS.containsKey(key)) {
			throw new IllegalArgumentException(key + "is not started");
		}
		REGISTERED_TASKS.get(key).cancel();
		REGISTERED_TASKS.get(key).purge();
		REGISTERED_TASKS.remove(key);
	}
	
	public static boolean isStarted(String key) {
		return REGISTERED_TASKS.containsKey(key);
	}
}
