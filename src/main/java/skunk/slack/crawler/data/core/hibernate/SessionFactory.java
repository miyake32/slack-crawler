package skunk.slack.crawler.data.core.hibernate;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Stream;

import javax.persistence.Entity;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;

import com.google.common.reflect.ClassPath;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SessionFactory {
	private static org.hibernate.SessionFactory sessionFactory = null;
	private static Object lock = new Object();
	private static String ENTITY_PACKAGE = "skunk.slack.crawler.data.entity";

	public static Session openSession() throws HibernateException, IOException {
		synchronized (lock) {
			if (Objects.isNull(sessionFactory) || sessionFactory.isClosed()) {
				sessionFactory = configure().buildSessionFactory();
			}
		}
		return sessionFactory.openSession();
	}

	private static Configuration configure() throws IOException {
		Configuration configuration = new Configuration().configure();
		configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
		configuration.setProperty("hibernate.hbm2ddl.auto", "update");
		configuration.setProperty("hibernate.show_sql", "false");
		configuration.setPhysicalNamingStrategy(new SnakeCaseNamingStrategy());
		setEntities(configuration);
		return configuration;
	}

	private static void setEntities(Configuration configuration) throws IOException {
		getClassesUnder(ENTITY_PACKAGE).filter(Objects::nonNull)
				.filter(clazz -> clazz.isAnnotationPresent(Entity.class)).forEach(clazz -> {
					log.info("added entity class {}", clazz.getName());
					configuration.addAnnotatedClass(clazz);
				});
	}

	private static Stream<Class<?>> getClassesUnder(String packageName) throws IOException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try {
			return ClassPath.from(loader).getTopLevelClassesRecursive(packageName).stream().map(info -> info.load());
		} catch (IOException e) {
			log.error("Error occurred while loading classes under {}", packageName);
			throw e;
		}
	}
}
