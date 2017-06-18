package skunk.slack.crawler.test.data.core.hibernate;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.junit.Test;

import skunk.slack.crawler.data.core.hibernate.SessionFactory;
import skunk.slack.crawler.data.entity.model.Channel;
import skunk.slack.crawler.data.entity.model.User;



public class SessionFactoryTest {
	@Test
	public void openSession() throws HibernateException, IOException {
		Session session = SessionFactory.openSession();
		assertNotNull(session);
	}

	@Test
	public void setEntities() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method setEntities = SessionFactory.class
								.getDeclaredMethod("setEntities", Configuration.class);
		setEntities.setAccessible(true);
		Configuration configuration = mock(Configuration.class);
		setEntities.invoke(null, configuration);
		
		verify(configuration, times(1)).addAnnotatedClass(Channel.class);
		verify(configuration, times(1)).addAnnotatedClass(User.class);

	}
}
