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
 * 
 */
package cz.muni.fi.spc.SchedVis.model.entities;

import java.util.List;
import java.util.Vector;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import cz.muni.fi.spc.SchedVis.model.BaseEntity;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Machine extends BaseEntity {

    @SuppressWarnings("unchecked")
    public static List<Machine> getAll(final Integer groupId) {
	final Criteria crit = BaseEntity.getCriteria(Machine.class, true);
	if (groupId != null) {
	    crit.add(Restrictions.eq("group", MachineGroup.getWithId(groupId)));
	} else {
	    crit.add(Restrictions.isNull("group"));
	}
	crit.addOrder(Order.asc("name"));
	return crit.list();
    }

    @SuppressWarnings("unchecked")
    public static List<Machine> getAllGroupless() {
	final Criteria crit = BaseEntity.getCriteria(Machine.class, true);
	crit.addOrder(Order.asc("name"));
	return crit.list();
    }

    @SuppressWarnings("unchecked")
    public static synchronized List<Event> getLatestSchedule(
	    final Machine which,
	    final Integer eventId) {
	Criteria crit = BaseEntity.getCriteria(Event.class, true);
	crit.add(Restrictions.eq("sourceMachine", which));
	crit.add(Restrictions.le("clock", eventId));
	crit.add(Restrictions.isNotNull("parent"));
	crit.addOrder(Property.forName("id").desc());
	crit.setMaxResults(1);
	final Event evt;
	try {
	    evt = (Event) crit.uniqueResult();
	    if (evt == null) {
		return new Vector<Event>();
	    }
	} catch (NullPointerException e) {
	    Logger.getLogger(Machine.class).error(
		    "NPE while fetching schedule for machine "
		    + which.getName() + " at " + eventId
			    + ". This shouldn't happen, please investigate.");
	    return new Vector<Event>();
	}
	crit = BaseEntity.getCriteria(Event.class, true);
	crit.add(Restrictions.eq("sourceMachine", which));
	crit.add(Restrictions.eq("parent", evt.getParent()));
	crit.addOrder(Property.forName("expectedStart").asc());
	return crit.list();
    }

    public static Event getLatestStateChange(final Machine which, final Integer clock) {
	Criteria crit = BaseEntity.getCriteria(Event.class, true);
	crit.add(Restrictions.eq("sourceMachine", which));
	crit.add(Restrictions.le("clock", clock));
	crit.add(Restrictions.in("type", new EventType[] {
		EventType.get(EventType.EVENT_MACHINE_FAILURE),
		EventType.get(EventType.EVENT_MACHINE_RESTART) }));
	crit.addOrder(Property.forName("id").desc());
	crit.setMaxResults(1);
	return (Event) crit.uniqueResult();
    }

    public static Machine getWithId(final Integer id) {
	final Criteria crit = BaseEntity.getCriteria(Machine.class, true);
	crit.add(Restrictions.idEq(id));
	return (Machine) crit.uniqueResult();
    }

    public static Machine getWithName(final String name) {
	final Criteria crit = BaseEntity.getCriteria(Machine.class, true);
	crit.add(Restrictions.eq("name", name));
	return (Machine) crit.uniqueResult();
    }

    public static boolean isActive(final Machine m, final Integer clock) {
	final Criteria crit = BaseEntity.getCriteria(Event.class, true);
	crit
	.add(Restrictions
		.in(
			"type",
			new EventType[] {
				EventType
				.get(EventType.EVENT_MACHINE_FAILURE),
				EventType
				.get(EventType.EVENT_MACHINE_FAILURE_JOB_MOVE_BAD),
				EventType
				.get(EventType.EVENT_MACHINE_FAILURE_JOB_MOVE_GOOD),
				EventType
				.get(EventType.EVENT_MACHINE_RESTART),
				EventType
				.get(EventType.EVENT_MACHINE_RESTART_JOB_MOVE_BAD),
				EventType
				.get(EventType.EVENT_MACHINE_RESTART_JOB_MOVE_GOOD) }));
	crit.add(Restrictions.eq("sourceMachine", m));
	crit.add(Restrictions.lt("clock", clock));
	crit.addOrder(Order.desc("clock"));
	crit.setMaxResults(1);
	Event e = (Event) crit.uniqueResult();
	try {
	    Integer id = e.getType().getId();
	    if ((id == EventType.EVENT_MACHINE_FAILURE)
		    || (id == EventType.EVENT_MACHINE_FAILURE_JOB_MOVE_BAD)
		    || (id == EventType.EVENT_MACHINE_FAILURE_JOB_MOVE_GOOD)) {
		return false;
	    } else {
		return true;
	    }
	} catch (NullPointerException ex) {
	    // when no such event is found, the machine is active.
	    return true;
	}
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

    @Index(name = "NameIndex")
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
