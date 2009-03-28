/*
    This file is part of SchedVis.

    SchedVis is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SchedVis is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with SchedVis.  If not, see <http://www.gnu.org/licenses/>.

 */
/**
 * Provides Hibernate integration for SchedVis.
 */
package cz.muni.fi.spc.SchedVis.model;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.Session;

/**
 * Integrate Hibernate as a handler of all the implemented database operations.
 * 
 * It is a singleton because we will never need more than one model.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class Database {

	private static final Map<String, EntityManager> ems = new HashMap<String, EntityManager>();

	private static EntityManagerFactory factory;

	private static EntityManager currentEM;

	private static String currentName;

	public static EntityManager getEntityManager() {
		return Database.currentEM;
	}

	public static String getName() {
		return Database.currentName;
	}

	public static Session getSession() {
		return (Session) Database.getEntityManager().getDelegate();
	}

	public static EntityManager newEntityManager() {
		return Database.factory.createEntityManager();
	}

	public static void persist(final BaseEntity e) {
		final List<BaseEntity> list = new Vector<BaseEntity>();
		list.add(e);
		Database.persist(list);
	}

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

	public static void remove(final BaseEntity e) {
		final List<BaseEntity> list = new Vector<BaseEntity>();
		list.add(e);
		Database.remove(list);
	}

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

	public static boolean use(final String name) {
		synchronized (Database.ems) {
			if (!Database.ems.containsKey(name)) {
				final Map<String, String> map = new HashMap<String, String>();
				map.put("hibernate.connection.url", "jdbc:sqlite:/"
				    + new File(name).getAbsolutePath());
				Database.factory = Persistence.createEntityManagerFactory("SchedVis",
				    map);
				Database.ems.put(name, Database.factory.createEntityManager());
			}
		}
		Database.currentName = name;
		Database.currentEM = Database.ems.get(name);
		return true;
	}

	private Database() {

	}

	@Override
	public void finalize() {
		for (final EntityManager em : Database.ems.values()) {
			em.close();
		}
	}
}
