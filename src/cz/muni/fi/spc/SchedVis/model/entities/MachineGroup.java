/**
 * 
 */
package cz.muni.fi.spc.SchedVis.model.entities;

import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.Criteria;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import cz.muni.fi.spc.SchedVis.model.BaseEntity;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class MachineGroup extends BaseEntity {

    @SuppressWarnings("unchecked")
    public static List<MachineGroup> getAll() {
	final Criteria crit = BaseEntity.getCriteria(MachineGroup.class, true);
	crit.addOrder(Order.asc("name"));
	return crit.list();
    }

    public static MachineGroup getWithId(final Integer id) {
	final Criteria crit = BaseEntity.getCriteria(MachineGroup.class, true);
	crit.add(Restrictions.idEq(id));
	return (MachineGroup) crit.uniqueResult();
    }

    public static MachineGroup getWithName(final String name) {
	final Criteria crit = BaseEntity.getCriteria(MachineGroup.class, true);
	crit.add(Restrictions.eq("name", name));
	return (MachineGroup) crit.uniqueResult();
    }

    private Integer id;

    private String name;

    private Set<Machine> machines;

    public void addMachine(final Machine me) {
	this.machines.add(me);
    }

    @GeneratedValue
    @Id
    public Integer getId() {
	return this.id;
    }

    @OneToMany(mappedBy = "group")
    public Set<Machine> getMachines() {
	return this.machines;
    }

    public String getName() {
	return this.name;
    }

    public void removeMachine(final Machine me) {
	this.machines.remove(me);
    }

    protected void setId(final Integer id) {
	this.id = id;
    }

    protected void setMachines(final Set<Machine> machines) {
	this.machines = machines;
    }

    public void setName(final String name) {
	this.name = name;
    }

}