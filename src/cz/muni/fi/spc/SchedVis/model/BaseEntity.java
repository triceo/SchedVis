/*
 * This file is part of SchedVis.
 * 
 * SchedVis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SchedVis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SchedVis. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * 
 */
package cz.muni.fi.spc.SchedVis.model;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The base class for all the JPA entities. Servers only as a helper.
 * 
 * Entities have various parameters, accessed through their getters and setters.
 * Getters and setters of the subclasses are undocumented except for the cases
 * where they actually do anything documentable.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 */
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public abstract class BaseEntity implements Cloneable {

	/**
	 * Get a fresh criteria query. Using this method violates JPA as it does not
	 * support criteria queries and thus locks the application to Hibernate.
	 * 
	 * @param em
	 *          The entity manager using which we should retrieve the query
	 * @param clazz
	 *          The class on which the Criteria query will operate.
	 * @param cacheable
	 *          Whether or not the query is allowed to be cached. Every query
	 *          should set this to true, unless there is a serious reason not to.
	 * @return New criteria query.
	 */
	protected static Criteria getCriteria(final EntityManager em,
	    final Class<?> clazz, final boolean cacheable) {
		final Criteria crit = BaseEntity.getSession(em).createCriteria(clazz);
		crit.setCacheable(cacheable);
		return crit;
	}

	/**
	 * Get a session associated with the given entity manager.
	 * 
	 * Might
	 * violate the JPA as it returns instance of a Session, possibly a
	 * Hibernate-only class.
	 * 
	 * @return Session associated with the entity manager.
	 */
	protected static Session getSession(final EntityManager em) {
		return (Session) em.getDelegate();
	}

}
