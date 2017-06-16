package skunk.slack.crawler.data.dao.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import lombok.extern.slf4j.Slf4j;
import skunk.slack.crawler.data.core.hibernate.SessionFactory;
import skunk.slack.crawler.data.dao.spec.EntityDao;

@Slf4j
public abstract class AbstractEntityDaoHibernateImpl<E> implements EntityDao<E> {
	public void save(Collection<E> entities) {
		Transaction tx = null;
		try (Session session = SessionFactory.openSession()) {
			tx = session.beginTransaction();
			entities.stream().forEach(msg -> session.save(msg));
			tx.commit();
		} catch (HibernateException | IOException e) {
			log.error("Failed to save {}", getEntityClass().getSimpleName(), e);
			if (Objects.nonNull(tx)) {
				tx.rollback();
			}
		}
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public Set<E> getAll() {
		try (Session session = SessionFactory.openSession()) {
			return (Set<E>) session.createCriteria(getEntityClass()).list().stream().collect(Collectors.toSet());
		} catch (HibernateException | IOException e) {
			log.error("Failed to get all records of {}", getEntityClass().getSimpleName(), e);
		}
		return null;
	}

	abstract Class<E> getEntityClass();
}
