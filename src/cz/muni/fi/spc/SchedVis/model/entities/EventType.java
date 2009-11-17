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
package cz.muni.fi.spc.SchedVis.model.entities;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import cz.muni.fi.spc.SchedVis.model.BaseEntity;
import cz.muni.fi.spc.SchedVis.util.Database;

/**
 * JPA Entity that represents type of an event.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public final class EventType extends BaseEntity {

	public static Integer EVENT_JOB_ARRIVAL = 1;
	public static Integer EVENT_JOB_EXECUTION_START = 2;
	public static Integer EVENT_JOB_CANCEL = 3;
	public static Integer EVENT_MACHINE_RESTART = 4;
	public static Integer EVENT_MACHINE_FAILURE = 5;
	public static Integer EVENT_JOB_MOVE_GOOD = 6;
	public static Integer EVENT_JOB_MOVE_BAD = 7;
	public static Integer EVENT_MACHINE_FAILURE_JOB_MOVE_GOOD = 10;
	public static Integer EVENT_MACHINE_FAILURE_JOB_MOVE_BAD = 11;
	public static Integer EVENT_JOB_COMPLETION = 12;

	private static final Map<Integer, EventType> byId = new HashMap<Integer, EventType>();

	/**
	 * Retrieve event type with a given id.
	 * 
	 * @param eventTypeId
	 *          The id of the event type in question.
	 * @return The event type.
	 */
	public static synchronized EventType getWithId(final int eventTypeId) {
		if (!EventType.byId.containsKey(eventTypeId)) {
			EventType.byId.put(eventTypeId, Database.getEntityManager().find(
			    EventType.class, eventTypeId));
		}
		return EventType.byId.get(eventTypeId);
	}

	private String name;

	private Integer id;

	/**
	 * This ID is not auto-generated by JPA because Importer needs to map IDs to
	 * well-known event types. Thus, the Importer creates the IDs on its own.
	 * 
	 * @return ID of the event type.
	 */
	@Id
	public Integer getId() {
		return this.id;
	}

	@Index(name = "etnIndex")
	public String getName() {
		return this.name;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setName(final String name) {
		this.name = name;
	}

}
