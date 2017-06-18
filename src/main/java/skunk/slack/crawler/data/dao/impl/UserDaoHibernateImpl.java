package skunk.slack.crawler.data.dao.impl;

import skunk.slack.crawler.data.dao.spec.UserDao;
import skunk.slack.crawler.data.entity.model.User;

public class UserDaoHibernateImpl extends AbstractEntityDaoHibernateImpl<User> implements UserDao {
	@Override
	Class<User> getEntityClass() {
		return User.class;
	}
}
