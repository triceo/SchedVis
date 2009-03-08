/**
 * 
 */
package cz.muni.fi.spc.SchedVis.model.entities;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.Criteria;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import cz.muni.fi.spc.SchedVis.model.BaseEntity;
import cz.muni.fi.spc.SchedVis.model.Database;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Event extends BaseEntity {

    public static Event getFirst() {
	final Criteria crit = BaseEntity.getCriteria(Event.class, true);
	crit.addOrder(Property.forName("id").asc());
	crit.add(Restrictions.isNull("parent"));
	crit.setMaxResults(1);
	return (Event) crit.uniqueResult();
    }

    public static Event getLast() {
	final Criteria crit = BaseEntity.getCriteria(Event.class, true);
	crit.addOrder(Property.forName("id").desc());
	crit.add(Restrictions.isNull("parent"));
	crit.setMaxResults(1);
	return (Event) crit.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public static Integer getMaxJobSpan() {
	final List<Integer> l = Database
		.getSession()
		.createSQLQuery(
			"SELECT sum(expectedEnd-expectedStart) AS s FROM Event GROUP BY parent_fk, sourceMachine_id ORDER BY s DESC LIMIT 1")
		.list();
	return l.get(0);
    }

    @SuppressWarnings("unchecked")
    public static Integer getMinExpectedStartTime(final Integer clock) {
	final List<Integer> l = Database
		.getSession()
		.createSQLQuery(
			"SELECT max(expectedStart) AS s FROM Event WHERE clock <= "
				+ clock.intValue()
				+ " AND parent_fk IS NOT NULL GROUP BY sourceMachine_id ORDER BY s ASC LIMIT 1")
		.list();
	if (l.size() > 0) {
	    return l.get(0);
	} else {
	    return 0;
	}
    }

    public static Event getNext(final Integer eventId) {
	final Criteria crit = BaseEntity.getCriteria(Event.class, true);
	crit.addOrder(Property.forName("id").asc());
	crit.add(Restrictions.isNull("parent"));
	crit.add(Restrictions.gt("id", eventId));
	crit.setMaxResults(1);
	return (Event) crit.uniqueResult();
    }

    public static Event getPrevious(final Integer eventId) {
	final Criteria crit = BaseEntity.getCriteria(Event.class, true);
	crit.addOrder(Property.forName("id").desc());
	crit.add(Restrictions.isNull("parent"));
	crit.add(Restrictions.le("id", eventId));
	crit.setMaxResults(1);
	return (Event) crit.uniqueResult();
    }

    private Integer id;
    private EventType eventType;
    private Machine srcMachine;
    private Machine dstMachine;
    private Integer clock;
    private Integer deadline;
    private Integer expectedEnd;
    private Integer expectedStart;
    private Integer job;
    private Integer neededCPUs;
    private Integer neededHDD;

    private Integer neededRAM;

    private String neededPlatform;

    private Event parent;

    private String assignedCPUs;

    private Set<Event> events = new HashSet<Event>();

    public void addChild(final Event e) {
	this.events.add(e);
    }

    public String getAssignedCPUs() {
	return this.assignedCPUs;
    }

    public Integer getClock() {
	return this.clock;
    }

    public Integer getDeadline() {
	return this.deadline;
    }

    public Integer getExpectedEnd() {
	return this.expectedEnd;
    }

    public Integer getExpectedStart() {
	return this.expectedStart;
    }

    @OneToMany(mappedBy = "parent")
    public Set<Event> getChildren() {
	return this.events;
    }

    @Id
    @GeneratedValue
    public Integer getId() {
	return this.id;
    }

    public Integer getJob() {
	return this.job;
    }

    public Integer getNeededCPUs() {
	return this.neededCPUs;
    }

    public Integer getNeededHDD() {
	return this.neededHDD;
    }

    public String getNeededPlatform() {
	return this.neededPlatform;
    }

    public Integer getNeededRAM() {
	return this.neededRAM;
    }

    @ManyToOne
    @JoinColumn(name = "parent_fk")
    public Event getParent() {
	return this.parent;
    }

    @OneToOne
    public Machine getSourceMachine() {
	return this.srcMachine;
    }

    @OneToOne
    public Machine getTargetMachine() {
	return this.dstMachine;
    }

    @ManyToOne
    public EventType getType() {
	return this.eventType;
    }

    public void removeChild(final Event e) {
	this.events.remove(e);
    }

    public void setAssignedCPUs(final String value) {
	this.assignedCPUs = value;
    }

    public void setClock(final Integer value) {
	this.clock = value;
    }

    public void setDeadline(final Integer value) {
	this.deadline = value;
    }

    public void setExpectedEnd(final Integer value) {
	this.expectedEnd = value;
    }

    public void setExpectedStart(final Integer value) {
	this.expectedStart = value;
    }

    protected void setChildren(final Set<Event> events) {
	this.events = events;
    }

    public void setId(final Integer id) {
	this.id = id;
    }

    public void setJob(final Integer value) {
	this.job = value;
    }

    public void setNeededCPUs(final Integer value) {
	this.neededCPUs = value;
    }

    public void setNeededHDD(final Integer value) {
	this.neededHDD = value;
    }

    public void setNeededPlatform(final String value) {
	this.neededPlatform = value;
    }

    public void setNeededRAM(final Integer value) {
	this.neededRAM = value;
    }

    public void setParent(final Event parent) {
	this.parent = parent;
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
