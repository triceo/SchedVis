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
import javax.persistence.Transient;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import cz.muni.fi.spc.SchedVis.model.BaseEntity;
import cz.muni.fi.spc.SchedVis.model.EventType;
import cz.muni.fi.spc.SchedVis.util.Database;

/**
 * JPA Entity that represents a single event.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public final class Event extends BaseEntity implements Comparable<Event> {

	private static Event firstEvent = null;
	private static Event lastEvent = null;

	private static Integer internalIdCounter = 0;

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
	public static Event getFirst() {
		if (Event.firstEvent == null) {
			final Criteria crit = BaseEntity.getCriteria(Event.class);
			crit.addOrder(Order.asc("id"));
			crit.setMaxResults(1);
			Event.firstEvent = (Event) crit.uniqueResult();
		}
		return Event.firstEvent;
	}

	/**
	 * Get the last event on the timeline.
	 * 
	 * @return The event.
	 */
	public static Event getLast() {
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
	 * Get the event that immediately preceeds the specified event, with relation
	 * to a given machine.
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
	 * Retrieve an event with a given event id.
	 * 
	 * @param eventId
	 *          The id of the event in question.
	 * @return The event.
	 */
	public static Event getWithId(final int eventId) {
		final Event evt = (Event) Database.find(Event.class, eventId);
		if (evt == null) {
			return Event.getFirst();
		}
		return evt;
	}

	private int id;
	private int eventType;
	private Machine srcMachine;
	private Machine dstMachine;
	private int clock;

	private int job;

	private int internalId;

	public Event() {
		synchronized (Event.internalIdCounter) {
			this.setInternalId(Event.internalIdCounter++);
		}
	}

	@Override
	public int compareTo(final Event o) {
		return Integer.valueOf(this.getId()).compareTo(Integer.valueOf(o.getId()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Event)) {
			return false;
		}
		final Event other = (Event) obj;
		if (this.internalId != other.internalId) {
			return false;
		}
		return true;
	}

	public int getClock() {
		return this.clock;
	}

	@Index(name = "tIndex")
	protected int getEventTypeId() {
		return this.eventType;
	}

	@Id
	@GeneratedValue
	public int getId() {
		return this.id;
	}

	public int getInternalId() {
		return this.internalId;
	}

	public int getJob() {
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

	@Transient
	public EventType getType() {
		return EventType.getWithId(this.getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.internalId;
		return result;
	}

	public void setClock(final int value) {
		this.clock = value;
	}

	protected void setEventTypeId(final int id) {
		this.eventType = id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	private void setInternalId(final int id) {
		this.internalId = id;
	}

	public void setJob(final int value) {
		this.job = value;
	}

	public void setSourceMachine(final Machine machine) {
		this.srcMachine = machine;
	}

	public void setTargetMachine(final Machine machine) {
		this.dstMachine = machine;
	}

	public void setType(final EventType type) {
		this.setEventTypeId(type.getId());
	}

	@Override
	public String toString() {
		return "Event [clock="
		    + this.clock
		    + ", "
		    + (this.dstMachine != null ? "dstMachine=" + this.dstMachine + ", "
		        : "") + "eventType=" + this.eventType + ", id=" + this.id
		    + ", job=" + this.job + ", "
		    + (this.srcMachine != null ? "srcMachine=" + this.srcMachine : "")
		    + "]";
	}

}