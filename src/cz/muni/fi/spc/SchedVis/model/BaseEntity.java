/*
 * This file is part of SchedVis.
 * 
 * SchedVis is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * SchedVis is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * SchedVis. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * 
 */
package cz.muni.fi.spc.SchedVis.model;

import java.sql.Connection;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.Session;

import cz.muni.fi.spc.SchedVis.util.Database;

/**
 * The base class for all the JPA entities. Serves only as a helper.
 * 
 * Entities have various parameters, accessed through their getters and setters.
 * Getters and setters of the subclasses are undocumented except for the cases
 * where they actually do anything documentable.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 */
public abstract class BaseEntity implements Cloneable {

	@SuppressWarnings("deprecation")
	protected static Connection getConnection(final EntityManager em) {
		return BaseEntity.getSession(em).connection();
	}

	/**
	 * Get a fresh criteria query. Using this method violates JPA as it does not
	 * support criteria queries and thus locks the application to Hibernate.
	 * 
	 * @param clazz
	 *          The class on which the Criteria query will operate.
	 * @return New criteria query.
	 */
	protected static Criteria getCriteria(final Class<?> clazz) {
		final Criteria crit = BaseEntity.getSession(Database.getEntityManager())
		    .createCriteria(clazz);
		crit.setCacheable(true);
		return crit;
	}

	/**
	 * Get a session associated with the given entity manager.
	 * 
	 * Might violate the JPA as it returns instance of a Session, possibly a
	 * Hibernate-only class.
	 * 
	 * @return Session associated with the entity manager.
	 */
	protected static Session getSession(final EntityManager em) {
		return (Session) em.getDelegate();
	}

}
