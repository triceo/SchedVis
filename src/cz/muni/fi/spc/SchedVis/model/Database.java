/**
 * Provides Hibernate integration for SchedVis.
 */
package cz.muni.fi.spc.SchedVis.model;

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

	private static final Map<String, EntityManager>	ems	= new HashMap<String, EntityManager>();

	private static EntityManagerFactory							factory;

	private static EntityManager										currentEM;

	public static EntityManager getEntityManager() {
		return Database.currentEM;
	}

	public static Session getSession() {
		return (Session) Database.getEntityManager().getDelegate();
	}

	public static void persist(final BaseEntity e) {
		final List<BaseEntity> list = new Vector<BaseEntity>();
		list.add(e);
		Database.persist(list);
	}

	public static void persist(final Collection<? extends BaseEntity> c) {
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

	public static void remove(final Collection<BaseEntity> c) {
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
		if (!Database.ems.containsKey(name)) {
			final Map<String, String> map = new HashMap<String, String>();
			map.put("hibernate.connection.url", "jdbc:sqlite:/" + name + ".sqlite");
			Database.factory = Persistence
					.createEntityManagerFactory("SchedVis", map);
			Database.ems.put(name, Database.factory.createEntityManager());
		}
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
