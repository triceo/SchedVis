/**
 * 
 */
package cz.muni.fi.spc.SchedVis.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class EntitySet<T extends Entity> implements Iterable<T>, Iterator<T> {

	private final ResultSet rs;
	private final T cls;

	public EntitySet(final ResultSet results, final T example) {
		this.rs = results;
		this.cls = example;
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
			try {
				final Entity e = this.cls.getClass().newInstance();
				e.load(id);
				return (T) e;
			} catch (final IllegalAccessException e) {
				return null;
			} catch (final InstantiationException e) {
				return null;
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
