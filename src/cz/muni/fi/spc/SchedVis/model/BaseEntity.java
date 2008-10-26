/**
 * 
 */
package cz.muni.fi.spc.SchedVis.model;

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public abstract class BaseEntity implements Cloneable {

	protected static Criteria getCriteria(final Class<?> clazz,
			final boolean cacheable) {
		final Criteria crit = Database.getInstance().getSession().createCriteria(
				clazz);
		crit.setCacheMode(CacheMode.NORMAL);
		crit.setCacheable(cacheable);
		return crit;
	}

}
