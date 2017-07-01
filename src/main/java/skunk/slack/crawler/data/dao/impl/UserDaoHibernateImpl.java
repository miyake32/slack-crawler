package skunk.slack.crawler.data.dao.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import lombok.extern.slf4j.Slf4j;
import skunk.slack.crawler.data.core.hibernate.SessionFactory;
import skunk.slack.crawler.data.dao.spec.UserDao;
import skunk.slack.crawler.data.entity.model.User;

@Slf4j
public class UserDaoHibernateImpl extends AbstractEntityDaoHibernateImpl<User> implements UserDao {
	@Override
	Class<User> getEntityClass() {
		return User.class;
	}
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	List<User> getUsers(Collection<String> userNames) {
		try (Session session = SessionFactory.openSession();) {
			return session.createCriteria(User.class).add(Restrictions.in("name", userNames)).list();
		} catch (HibernateException | IOException e) {
			log.error("Failed to retrieve users [userNames:{}]", userNames, e);
			return null;
		}
	}
}
