package com.worksap.skunk.slack.crawler.test.data.core.hibernate;

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
import org.mockito.Mockito;

import com.worksap.skunk.slack.crawler.data.core.hibernate.SessionFactory;



public class SessionFactoryTest {
	// must change when adding entity class
	int NUM_OF_ENTITY_CLASSES = 4;
	@Test
	public void openSession() throws HibernateException, IOException {
		Session session = SessionFactory.openSession();
		assertNotNull(session);
		SessionFactory.closeSession();
	}

	@Test
	public void setEntities() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method setEntities = SessionFactory.class
								.getDeclaredMethod("setEntities", Configuration.class);
		setEntities.setAccessible(true);
		Configuration configuration = mock(Configuration.class);
		setEntities.invoke(null, configuration);
		
		verify(configuration, times(NUM_OF_ENTITY_CLASSES)).addAnnotatedClass(Mockito.any(Class.class));
	}
}
