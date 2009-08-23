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
package cz.muni.fi.spc.SchedVis.model.entities;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.Criteria;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import cz.muni.fi.spc.SchedVis.model.BaseEntity;
import cz.muni.fi.spc.SchedVis.util.Database;

/**
 * JPA Entity that represents a machine.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public final class Machine extends BaseEntity implements Comparable<Machine> {

	/**
	 * Holds so-called "machine event types" - those change the state of a
	 * machine, such as failure or restart. For performance reasons, this is
	 * static and filled lazily when needed.
	 */
	private static EventType[] machineEvents = new EventType[0];

	private static final Map<Integer, Machine> byId = new HashMap<Integer, Machine>();
	private static final Map<String, Machine> byName = new HashMap<String, Machine>();

	/**
	 * Retrieve all the machines in a given group.
	 * 
	 * @param groupId
	 *          ID of the group whose machines will be retrieved. If null,
	 *          machines in no group are retrieved.
	 * @return The machines.
	 */
	@SuppressWarnings("unchecked")
	public static Set<Machine> getAll(final Integer groupId) {
		final Criteria crit = BaseEntity.getCriteria(Machine.class);
		if (groupId != null) {
			crit.add(Restrictions.eq("group", MachineGroup.get(groupId))); //$NON-NLS-1$
		} else {
			crit.add(Restrictions.isNull("group")); //$NON-NLS-1$
		}
		crit.addOrder(Order.asc("name")); //$NON-NLS-1$
		return new TreeSet<Machine>(crit.list());
	}

	/**
	 * Retrieve all machines, regardless whether they are or are not in some
	 * group.
	 * 
	 * @return The machines.
	 */
	@SuppressWarnings("unchecked")
	public static Set<Machine> getAllGroupless() {
		final Criteria crit = BaseEntity.getCriteria(Machine.class);
		crit.addOrder(Order.asc("name")); //$NON-NLS-1$
		return new TreeSet<Machine>(crit.list());
	}

	/**
	 * Get the latest schedule that concerns given machine at a given time. This
	 * method ties directly to SQLite, even skipping Hibernate - it is essential
	 * that this method is as fast as possible.
	 * 
	 * @param which
	 *          The machine in which we are interested.
	 * @param evt
	 *          The latest schedule is looked for in the interval of event 1 to
	 *          this number.
	 * @return Jobs in the schedule.
	 */
	public static List<Job> getLatestSchedule(final Machine which, final Event evt) {
		try {
			if (Machine.s == null) {
				final String query = "SELECT id, assignedCPUs, deadline, job, jobHint, expectedStart, expectedEnd, bringsSchedule FROM Job WHERE machine_id = ? AND parent = (SELECT max(parent) FROM Job WHERE machine_id = ? AND parent <= ?)"; //$NON-NLS-1$
				Machine.s = BaseEntity.getConnection(Database.getEntityManager())
				    .prepareStatement(query);
			}
			Machine.s.setInt(1, which.getId().intValue());
			Machine.s.setInt(2, which.getId().intValue());
			Machine.s.setInt(3, evt.getId());
			final ResultSet rs = Machine.s.executeQuery();
			final List<Job> schedules = new Vector<Job>();
			while (rs.next()) {
				final Job schedule = new Job();
				schedule.setId(rs.getInt(1));
				schedule.setAssignedCPUs(rs.getString(2));
				schedule.setDeadline(rs.getInt(3));
				schedule.setJob(rs.getInt(4));
				schedule.setJobHint(rs.getInt(5));
				schedule.setExpectedStart(rs.getInt(6));
				schedule.setExpectedEnd(rs.getInt(7));
				if (rs.getInt(8) == 1) {
					schedule.setBringsSchedule(true);
				} else {
					schedule.setBringsSchedule(false);
				}
				schedules.add(schedule);
			}
			rs.close();
			Machine.s.clearParameters();
			return schedules;
		} catch (final SQLException e) {
			e.printStackTrace();
			return new Vector<Job>();
		}
	}

	private static Machine getWithName(final String name) {
		final Criteria crit = BaseEntity.getCriteria(Machine.class);
		crit.add(Restrictions.eq("name", name)); //$NON-NLS-1$
		crit.setMaxResults(1);
		return (Machine) crit.uniqueResult();
	}

	/**
	 * Retrieve machine with a given name.
	 * 
	 * @param name
	 *          The name in question.
	 * @param cache
	 *          Whether or not to use the local entity cache.
	 * @return The machine.
	 */
	public synchronized static Machine getWithName(final String name,
	    final boolean cache) {
		if (!cache) {
			return Machine.getWithName(name);
		}
		if (!Machine.byName.containsKey(name)) {
			final Machine m = Machine.getWithName(name);
			if (m == null) {
				return null;
			}
			Machine.byName.put(name, m);
		}
		final Machine m = Machine.byName.get(name);
		if (!Machine.byId.containsKey(m.getId())) {
			Machine.byId.put(m.getId(), m);
		}
		return m;
	}

	/**
	 * Determine whether a machine is active (not off-line) at a given point in
	 * time.
	 * 
	 * @param m
	 *          Machine in question.
	 * @param evt
	 *          The given point of time.
	 * @return False when the last machine event up to and including the given
	 *         time is machine failure. True otherwise, especially when there are
	 *         no such events.
	 */
	public static boolean isActive(final Machine m, final Event evt) {
		synchronized (Machine.machineEvents) {
			if (Machine.machineEvents.length == 0) {
				Machine.machineEvents = new EventType[] {
				    EventType.get(EventType.EVENT_MACHINE_FAILURE),
				    EventType.get(EventType.EVENT_MACHINE_FAILURE_JOB_MOVE_BAD),
				    EventType.get(EventType.EVENT_MACHINE_FAILURE_JOB_MOVE_GOOD),
				    EventType.get(EventType.EVENT_MACHINE_RESTART) };
			}
		}
		final Criteria crit = BaseEntity.getCriteria(Event.class);
		crit.add(Restrictions.in("type", Machine.machineEvents)); //$NON-NLS-1$
		crit.add(Restrictions.lt("id", evt.getId())); //$NON-NLS-1$
		crit.setProjection(Projections.max("id")); //$NON-NLS-1$
		final Integer evtId = (Integer) crit.uniqueResult();
		if (evtId == null) {
			return true;
		}
		final Event e = Database.getEntityManager().find(Event.class, evtId);
		if (e == null) {
			return true;
		}
		final Integer id = e.getType().getId();
		if ((id.equals(EventType.EVENT_MACHINE_FAILURE))
		    || (id.equals(EventType.EVENT_MACHINE_FAILURE_JOB_MOVE_BAD))
		    || (id.equals(EventType.EVENT_MACHINE_FAILURE_JOB_MOVE_GOOD))) {
			return false;
		}
		return true;
	}

	private String os;
	private Integer id;
	private Integer cpus;
	private Integer hdd;
	private String name;
	private String platform;
	private Integer ram;

	private Integer speed;

	private MachineGroup group;

	private static PreparedStatement s;

	@Override
	public int compareTo(final Machine o) {
		return this.getId().compareTo(o.getId());
	}

	public Integer getCPUs() {
		return this.cpus;
	}

	@ManyToOne
	@JoinColumn(name = "group_fk")
	public MachineGroup getGroup() {
		return this.group;
	}

	public Integer getHDD() {
		return this.hdd;
	}

	@Id
	@GeneratedValue
	public Integer getId() {
		return this.id;
	}

	@Index(name = "mnIndex")
	public String getName() {
		return this.name;
	}

	public String getOS() {
		return this.os;
	}

	public String getPlatform() {
		return this.platform;
	}

	public Integer getRAM() {
		return this.ram;
	}

	public Integer getSpeed() {
		return this.speed;
	}

	public void setCPUs(final Integer cpus) {
		this.cpus = cpus;
	}

	public void setGroup(final MachineGroup group) {
		this.group = group;
	}

	public void setHDD(final Integer hdd) {
		this.hdd = hdd;
	}

	protected void setId(final Integer id) {
		this.id = id;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setOS(final String os) {
		this.os = os;
	}

	public void setPlatform(final String platform) {
		this.platform = platform;
	}

	public void setRAM(final Integer ram) {
		this.ram = ram;
	}

	public void setSpeed(final Integer speed) {
		this.speed = speed;
	}

}
