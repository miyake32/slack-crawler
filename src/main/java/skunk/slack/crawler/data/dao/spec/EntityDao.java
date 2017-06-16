package skunk.slack.crawler.data.dao.spec;

import java.util.Collection;
import java.util.Set;

public interface EntityDao<E> {
	public void save(Collection<E> entities);
	public Set<E> getAll();
}
