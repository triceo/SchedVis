/**
 * Provides Hibernate integration for SchedVis.
 */
package cz.muni.fi.spc.SchedVis.model;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

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

	public static Database getInstance() {
		if (Database.instance == null) {
			Database.instance = new Database();
		}
		return Database.instance;
	}

	private final Map<String, SessionFactory>	factories				= new HashMap<String, SessionFactory>();
	private SessionFactory										currentSessFact	= null;

	private Session														currentSession	= null;

	private Database() {

	}

	@Override
	public void finalize() {
		for (final SessionFactory factory : this.factories.values()) {
			factory.close();
		}
	}

	public Session getSession() {
		return this.currentSession;
	}

	public boolean use(final String name) {
		if (!this.factories.containsKey(name)) {
			final AnnotationConfiguration cfg = new AnnotationConfiguration()
					.configure();
			cfg.setProperty("hibernate.connection.url", "jdbc:sqlite:/" + name
					+ ".sqlite");
			this.factories.put(name, cfg.buildSessionFactory());
		}
		this.currentSessFact = this.factories.get(name);
		this.currentSession = this.currentSessFact.openSession();
		return true;
	}
}
