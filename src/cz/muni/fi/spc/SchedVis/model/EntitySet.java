/**
 * 
 */
package cz.muni.fi.spc.SchedVis.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class EntitySet<T extends Entity> implements Iterable<T>, Iterator<T> {

	public static void clearCache() {
		EntitySet.cache.clear();
	}

	private final ResultSet rs;
	private final T cls;

	private static Map<Class<?>, Map<Integer, Entity>> cache = new HashMap<Class<?>, Map<Integer, Entity>>();

	public EntitySet(final ResultSet results, final T example) {
		this.rs = results;
		this.cls = example;
		// if sub-map not initialized, initialize it
		if (this.getCache() == null) {
			EntitySet.cache.put(this.cls.getClass(),
					new HashMap<Integer, Entity>());
		}
	}

	private Map<Integer, Entity> getCache() {
		if (EntitySet.cache.containsKey(this.cls.getClass())) {
			return EntitySet.cache.get(this.cls.getClass());
		} else {
			return null;
		}
	}

	public boolean hasNext() {
		try {
			return !this.rs.isLast();
		} catch (final SQLException e) {
			return false;
		}
	}

	public Iterator<T> iterator() {
		return this;
	}

	public T next() {
		try {
			this.rs.next();
			final Integer id = this.rs.getInt(1);
			if (this.getCache().containsKey(id)) { // load entity from cache
				return (T) this.getCache().get(id);
			} else { // load entity and cache it
				try {

					final Entity e = this.cls.getClass().newInstance();
					e.load(id);
					this.getCache().put(id, e);
					return (T) e;
				} catch (final IllegalAccessException e) {
					return null;
				} catch (final InstantiationException e) {
					return null;
				}
			}
		} catch (final SQLException e) {
			return null;
		}
	}

	public void remove() {
		try {
			this.rs.deleteRow();
		} catch (final SQLException e) {
			// nothing to do
		}
	}

}
