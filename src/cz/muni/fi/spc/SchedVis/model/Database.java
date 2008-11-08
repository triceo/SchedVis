/**
 * Provides Hibernate integration for SchedVis.
 */
package cz.muni.fi.spc.SchedVis.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

	private static Database	instance	= null;

	public static EntityManager getEntityManager() {
		return Database.getInstance().currentEM;
	}

	protected static Database getInstance() {
		if (Database.instance == null) {
			Database.instance = new Database();
		}
		return Database.instance;
	}

	public static Session getSession() {
		return (Session) Database.getInstance().currentEM.getDelegate();
	}

	public static void persist(final BaseEntity e) {
		Database.getEntityManager().getTransaction().begin();
		Database.getEntityManager().persist(e);
		Database.getEntityManager().getTransaction().commit();
	}

	public static void persist(final Collection<? extends BaseEntity> c) {
		Database.getEntityManager().getTransaction().begin();
		for (final BaseEntity e : c) {
			Database.getEntityManager().persist(e);
		}
		Database.getEntityManager().getTransaction().commit();
	}

	public static void remove(final BaseEntity e) {
		Database.getEntityManager().getTransaction().begin();
		Database.getEntityManager().remove(e);
		Database.getEntityManager().getTransaction().commit();
	}

	public static void remove(final Collection<BaseEntity> c) {
		Database.getEntityManager().getTransaction().begin();
		for (final BaseEntity e : c) {
			Database.getEntityManager().remove(e);
		}
		Database.getEntityManager().getTransaction().commit();
	}

	public static boolean use(final String name) {
		final Database db = Database.getInstance();
		if (!db.ems.containsKey(name)) {
			final Map<String, String> map = new HashMap<String, String>();
			map.put("hibernate.connection.url", "jdbc:sqlite:/" + name + ".sqlite");
			db.factory = Persistence.createEntityManagerFactory("SchedVis", map);
			db.ems.put(name, db.factory.createEntityManager());
		}
		db.currentEM = db.ems.get(name);
		return true;
	}

	private final Map<String, EntityManager>	ems	= new HashMap<String, EntityManager>();

	private EntityManagerFactory							factory;

	private EntityManager											currentEM;

	private Database() {

	}

	@Override
	public void finalize() {
		for (final EntityManager em : this.ems.values()) {
			em.close();
		}
	}
}
