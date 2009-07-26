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
 */
package cz.muni.fi.spc.SchedVis.model.entities;

import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import cz.muni.fi.spc.SchedVis.model.BaseEntity;
import cz.muni.fi.spc.SchedVis.util.Database;

/**
 * JPA Entity that represents a single event.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public final class Event extends BaseEntity implements Comparable<Event> {

	public static final Integer JOB_HINT_NONE = 0;
	public static final Integer JOB_HINT_MOVE_OK = 1;
	public static final Integer JOB_HINT_MOVE_NOK = 2;
	public static final Integer JOB_HINT_ARRIVAL = 3;

	private static Event firstEvent = null;
	private static Event lastEvent = null;

	/**
	 * Whether or not there exists an event with a given ID.
	 * 
	 * @param eventId
	 *          The event id in question.
	 * @return True if such event exists, false otherwise.
	 */
	public static boolean exists(final Integer eventId) {
		return (Database.getEntityManager().find(Event.class, eventId) != null);
	}

	/**
	 * Get numbers of all existing events.
	 * 
	 * @return All the existing events.
	 */
	@SuppressWarnings("unchecked")
	public static Set<Integer> getAllTicks() {
		return new TreeSet<Integer>(((Session) Database.getEntityManager()
		    .getDelegate()).createSQLQuery(
		    "SELECT DISTINCT id FROM Event ORDER BY id ASC").list());
	}

	/**
	 * Get first event on the timeline.
	 * 
	 * @return The event.
	 */
	public synchronized static Event getFirst() {
		if (Event.firstEvent == null) {
			Event.firstEvent = Database.getEntityManager().find(Event.class, 1);
		}
		return Event.firstEvent;
	}

	/**
	 * Get the last event on the timeline.
	 * 
	 * @return The event.
	 */
	public synchronized static Event getLast() {
		if (Event.lastEvent == null) {
			final Criteria crit = BaseEntity.getCriteria(Event.class);
			crit.addOrder(Order.desc("id"));
			crit.setMaxResults(1);
			Event.lastEvent = (Event) crit.uniqueResult();
		}
		return Event.lastEvent;
	}

	/**
	 * Get the event that immediately follows the specified event.
	 * 
	 * @param evt
	 *          The event in question.
	 * @return The next event.
	 */
	public static Event getNext(final Event evt) {
		return Event.getNext(evt, null);
	}

	/**
	 * Get the event that immediately follows the specified event with relation to
	 * the given machine.
	 * 
	 * @param evt
	 *          The event in question.
	 * @param m
	 *          ID of the machine in question. If null, no machine is considered.
	 * 
	 * @return The next event.
	 */
	public static Event getNext(final Event evt, final Machine m) {
		final Criteria crit = BaseEntity.getCriteria(Job.class);
		crit.addOrder(Order.asc("id"));
		crit.add(Restrictions.gt("parent", evt));
		if (m != null) {
			crit.add(Restrictions.eq("machine", m));
		}
		crit.setMaxResults(1);
		final Job job = (Job) crit.uniqueResult();
		if (job == null) {
			return Event.getLast();
		}
		return job.getParent();
	}

	/**
	 * Get the event that immediately preceeds the specified event.
	 * 
	 * @param evt
	 *          Event in question.
	 * @return The previous event.
	 */
	public static Event getPrevious(final Event evt) {
		return Event.getPrevious(evt, null);
	}

	/**
	 * Get the event that immediately preceeds the specified event.
	 * 
	 * @param evt
	 *          The event in question.
	 * @param m
	 *          ID of the machine in question. If null, no machine is considered.
	 * 
	 * @return The previous event.
	 */
	public static Event getPrevious(final Event evt, final Machine m) {
		final Criteria crit = BaseEntity.getCriteria(Job.class);
		crit.addOrder(Order.desc("id"));
		crit.add(Restrictions.lt("parent", evt));
		if (m != null) {
			crit.add(Restrictions.eq("machine", m));
		}
		crit.setMaxResults(1);
		final Job job = (Job) crit.uniqueResult();
		if (job == null) {
			return Event.getFirst();
		}
		return job.getParent();
	}

	/**
	 * Retrieve a clock with a given event id.
	 * 
	 * @param eventId
	 *          The id of the event in question.
	 * @return The clock value.
	 */
	public static Event getWithId(final int eventId) {
		final Event evt = Database.getEntityManager().find(Event.class, eventId);
		if (evt == null) {
			return Event.getWithId(1);
		}
		return evt;
	}

	private Integer id;
	private EventType eventType;
	private Machine srcMachine;
	private Machine dstMachine;
	private Integer clock;
	private Integer job;

	@Override
	public int compareTo(final Event o) {
		return this.getId().compareTo(o.getId());
	}

	public Integer getClock() {
		return this.clock;
	}

	@Id
	@GeneratedValue
	public Integer getId() {
		return this.id;
	}

	public Integer getJob() {
		return this.job;
	}

	@ManyToOne
	public Machine getSourceMachine() {
		return this.srcMachine;
	}

	@ManyToOne
	public Machine getTargetMachine() {
		return this.dstMachine;
	}

	@ManyToOne
	@Index(name = "tIndex")
	public EventType getType() {
		return this.eventType;
	}

	public void setClock(final Integer value) {
		this.clock = value;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setJob(final Integer value) {
		this.job = value;
	}

	public void setSourceMachine(final Machine machine) {
		this.srcMachine = machine;
	}

	public void setTargetMachine(final Machine machine) {
		this.dstMachine = machine;
	}

	public void setType(final EventType type) {
		this.eventType = type;
	}

}