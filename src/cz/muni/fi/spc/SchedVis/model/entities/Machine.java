/**
 * 
 */
package cz.muni.fi.spc.SchedVis.model.entities;

import java.util.List;

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
import org.hibernate.criterion.Restrictions;

import cz.muni.fi.spc.SchedVis.model.BaseEntity;

/**
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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

	private String				os;
	private Integer				id;
	private Integer				cpus;
	private Integer				hdd;
	private String				name;
	private String				platform;
	private Integer				ram;
	private Integer				speed;

	private MachineGroup	group;

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
