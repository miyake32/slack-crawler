package com.worksap.skunk.slack.crawler.data;

import java.lang.reflect.Method;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Before;

import skunk.slack.crawler.data.core.hibernate.SnakeCaseNamingStrategy;

public class AbstractHibernateDataAccessTest {
	private static SessionFactory sessionFactory = getSessionFactory();
	private Session session;

	@Before
	public void before() throws Exception {
		session = sessionFactory.openSession();
	}

	static private SessionFactory getSessionFactory() {
		Configuration configuration = new Configuration();
		configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
		configuration.setProperty("hibernate.connection.url", "jdbc:h2:~/slack-crawler-test");
		configuration.setProperty("hibernate.hbm2ddl.auto", "create");
		configuration.setProperty("hibernate.show_sql", "true");
		configuration.setPhysicalNamingStrategy(new SnakeCaseNamingStrategy());
		try {
			Method setEntities = skunk.slack.crawler.data.core.hibernate.SessionFactory.class.getDeclaredMethod("setEntities", Configuration.class);
			setEntities.setAccessible(true);
			setEntities.invoke(null, configuration);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return configuration.buildSessionFactory();
	}
	
	public Session getSession() {
		return session;
	}
}
