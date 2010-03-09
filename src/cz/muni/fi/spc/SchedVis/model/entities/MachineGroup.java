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

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.Criteria;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import cz.muni.fi.spc.SchedVis.model.BaseEntity;
import cz.muni.fi.spc.SchedVis.util.Database;

/**
 * JPA Entity that represents a group of machines.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public final class MachineGroup extends BaseEntity implements
    Comparable<MachineGroup> {

	private static AtomicInteger internalIdCounter = new AtomicInteger(0);

	/**
	 * Get all the machine groups.
	 * 
	 * @return The machine groups.
	 */
	@SuppressWarnings("unchecked")
	public static Set<MachineGroup> getAll() {
		final Criteria crit = BaseEntity.getCriteria(MachineGroup.class);
		crit.addOrder(Order.asc("name"));
		return new TreeSet<MachineGroup>(crit.list());
	}

	/**
	 * Get machine group with a given ID. It is expected that there will be a very
	 * small number of groups, so they are cached locally.
	 * 
	 * @param id
	 *          The id in question.
	 * @return The machine.
	 */
	public static MachineGroup getWithId(final int id) {
		if (!MachineGroup.byId.containsKey(id)) {
			MachineGroup.byId.put(id, Database.getEntityManager().find(
			    MachineGroup.class, id));
		}
		final MachineGroup m = MachineGroup.byId.get(id);
		MachineGroup.byName.putIfAbsent(m.getName(), m);
		return m;
	}

	private static MachineGroup getWithName(final String name) {
		final Criteria crit = BaseEntity.getCriteria(MachineGroup.class);
		crit.add(Restrictions.eq("name", name));
		crit.setMaxResults(1);
		return (MachineGroup) crit.uniqueResult();
	}

	/**
	 * Get machine group with a given name.
	 * 
	 * @param name
	 *          The machine name in question.
	 * @param cache
	 *          Whether or not to use the local entity cache.
	 * @return The machine.
	 */
	public static MachineGroup getWithName(final String name, final boolean cache) {
		if (!cache) {
			return MachineGroup.getWithName(name);
		}
		if (!MachineGroup.byName.containsKey(name)) {
			final Criteria crit = BaseEntity.getCriteria(MachineGroup.class);
			crit.add(Restrictions.eq("name", name));
			crit.setMaxResults(1);
			final MachineGroup mg = MachineGroup.getWithName(name);
			if (mg == null) {
				return null;
			}
			MachineGroup.byName.put(name, mg);
		}
		final MachineGroup m = MachineGroup.byName.get(name);
		if (m != null) {
			MachineGroup.byId.putIfAbsent(m.getId(), m);
		}
		return m;
	}

	private int internalId;

	private static final ConcurrentMap<Integer, MachineGroup> byId = new ConcurrentHashMap<Integer, MachineGroup>();

	private static final ConcurrentMap<String, MachineGroup> byName = new ConcurrentHashMap<String, MachineGroup>();

	private int id;

	private String name;

	private Set<Machine> machines;

	public MachineGroup() {
		this.setInternalId(MachineGroup.internalIdCounter.incrementAndGet());
	}

	/**
	 * Add a machine to the group.
	 * 
	 * @param me
	 *          The machine.
	 */
	public void addMachine(final Machine me) {
		if (this.machines == null) {
			this.machines = new HashSet<Machine>();
		}
		this.machines.add(me);
	}

	@Override
	public int compareTo(final MachineGroup o) {
		return this.getName().compareTo(o.getName());
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
		if (!(obj instanceof MachineGroup)) {
			return false;
		}
		final MachineGroup other = (MachineGroup) obj;
		if (this.internalId != other.internalId) {
			return false;
		}
		return true;
	}

	@GeneratedValue
	@Id
	public int getId() {
		return this.id;
	}

	public int getInternalId() {
		return this.internalId;
	}

	/**
	 * Get all machines in a group.
	 * 
	 * @return The machines.
	 */
	@OneToMany(mappedBy = "group")
	public Set<Machine> getMachines() {
		return this.machines;
	}

	@Index(name = "mgnIndex")
	public String getName() {
		return this.name;
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

	/**
	 * Remove machine from the group.
	 * 
	 * @param me
	 *          The machine.
	 */
	public void removeMachine(final Machine me) {
		this.machines.remove(me);
	}

	protected void setId(final int id) {
		this.id = id;
	}

	private void setInternalId(final int id) {
		this.internalId = id;
	}

	protected void setMachines(final Set<Machine> machines) {
		this.machines = machines;
	}

	public void setName(final String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MachineGroup [id=" + this.id + ", "
		    + (this.machines != null ? "machines=" + this.machines + ", " : "")
		    + (this.name != null ? "name=" + this.name : "") + "]";
	}

}