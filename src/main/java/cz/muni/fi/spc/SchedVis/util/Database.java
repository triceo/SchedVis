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
 * Provides Hibernate integration for SchedVis.
 */
package cz.muni.fi.spc.SchedVis.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import cz.muni.fi.spc.SchedVis.model.BaseEntity;

/**
 * Integrate Hibernate (through JPA) as a handler of all the implemented
 * database operations.
 * 
 * It is a singleton because we will never need more than one model.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public final class Database {

	private static EntityManagerFactory currentEMF;

	private static ThreadLocal<EntityManager> entityManager = new ThreadLocal<EntityManager>() {
		@Override
		protected synchronized EntityManager initialValue() {
			Database.use();
			return Database.currentEMF.createEntityManager();
		}
	};

	/**
	 * Returns the current entity manager. Client code should not close it.
	 * 
	 * @return The EntityManager.
	 */
	public static EntityManager getEntityManager() {
		return Database.entityManager.get();
	}

	/**
	 * Call JPA merge() on a single entity.
	 * 
	 * @param e
	 *          The entity.
	 */
	public static void merge(final BaseEntity e) {
		final List<BaseEntity> list = new ArrayList<BaseEntity>();
		list.add(e);
		Database.merge(list);
	}

	/**
	 * Call JPA merge() on a bunch of entities, resulting in them all being merged
	 * inside a transaction.
	 * 
	 * @param c
	 *          A collection of entities to merge.
	 */
	public static synchronized void merge(final Collection<? extends BaseEntity> c) {
		boolean endTransaction = false;
		if (!Database.getEntityManager().getTransaction().isActive()) {
			Database.getEntityManager().getTransaction().begin();
			endTransaction = true;
		}
		for (final BaseEntity e : c) {
			Database.getEntityManager().merge(e);
		}
		if (endTransaction) {
			Database.getEntityManager().getTransaction().commit();
		}
	}

	/**
	 * Call JPA persist() on a single entity.
	 * 
	 * @param e
	 *          The entity.
	 */
	public static void persist(final BaseEntity e) {
		final List<BaseEntity> list = new ArrayList<BaseEntity>();
		list.add(e);
		Database.persist(list);
	}

	/**
	 * Call JPA persist() on a bunch of entities, resulting in them all being
	 * persisted inside a transaction.
	 * 
	 * @param c
	 *          A collection of entities to persist.
	 */
	public static synchronized void persist(
	    final Collection<? extends BaseEntity> c) {
		boolean endTransaction = false;
		if (!Database.getEntityManager().getTransaction().isActive()) {
			Database.getEntityManager().getTransaction().begin();
			endTransaction = true;
		}
		for (final BaseEntity e : c) {
			Database.getEntityManager().persist(e);
		}
		if (endTransaction) {
			Database.getEntityManager().getTransaction().commit();
		}
	}

	/**
	 * Call JPA remove() on a single entity.
	 * 
	 * @param e
	 *          The entity.
	 */
	public static void remove(final BaseEntity e) {
		final List<BaseEntity> list = new ArrayList<BaseEntity>();
		list.add(e);
		Database.remove(list);
	}

	/**
	 * Call JPA remove() on a bunch of entities, resulting in them all being
	 * removed inside a transaction.
	 * 
	 * @param c
	 *          A collection of entities to remove.
	 */
	public static synchronized void remove(final Collection<BaseEntity> c) {
		boolean endTransaction = false;
		if (!Database.getEntityManager().getTransaction().isActive()) {
			Database.getEntityManager().getTransaction().begin();
			endTransaction = true;
		}
		for (final BaseEntity e : c) {
			Database.getEntityManager().remove(e);
		}
		if (endTransaction) {
			Database.getEntityManager().getTransaction().commit();
		}
	}

	/**
	 * Creates the appropriate entity manager factory and an entity manager.
	 * Calling this method more than once has no effect.
	 */
	private static synchronized void use() {
		if (Database.currentEMF == null) {
			final Map<String, String> map = new HashMap<String, String>();
			map.put("hibernate.connection.url", "jdbc:sqlite:/"
			    + Configuration.getDatabaseFile().getAbsolutePath());
			Database.currentEMF = Persistence.createEntityManagerFactory("SchedVis",
			    map);
		}
	}

}
