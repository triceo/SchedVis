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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.Session;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Table;

import cz.muni.fi.spc.SchedVis.model.BaseEntity;
import cz.muni.fi.spc.SchedVis.util.Database;

/**
 * JPA Entity that represents a single event.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(appliesTo = "Job", indexes = { @Index(name = "multiIndex", columnNames = {
    "machine_id", "parent" }) })
public final class Job extends BaseEntity implements Comparable<Job> {

	public static final Integer JOB_HINT_NONE = 0;
	public static final Integer JOB_HINT_MOVE_OK = 1;
	public static final Integer JOB_HINT_MOVE_NOK = 2;
	public static final Integer JOB_HINT_ARRIVAL = 3;
	private static Integer maxJobSpan = -1;

	/**
	 * Get the maximum length of a job. Actually computed only first time, every
	 * other time over the app lifetime, the first result is retrieved.
	 * 
	 * @return The length in ticks.
	 */
	public static Integer getMaxSpan() {
		if (Job.maxJobSpan == -1) {
			Job.maxJobSpan = (Integer) ((Session) Database.getEntityManager()
			    .getDelegate())
			    .createSQLQuery(
			        "SELECT max(expectedEnd - clock) AS s FROM Job GROUP BY parent, machine_id ORDER BY s DESC LIMIT 1") //$NON-NLS-1$
			    .list().get(0);
		}
		return Job.maxJobSpan;
	}

	private Integer id;
	private Machine srcMachine;
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
	private Integer clock;
	private boolean bringsSchedule = false;
	private Integer jobHint = Job.JOB_HINT_NONE;

	@Override
	public int compareTo(final Job o) {
		return this.getId().compareTo(o.getId());
	}

	/**
	 * Get CPUs assigned to a job.
	 * 
	 * @return A string containing integers (numbers of assigned CPUs) separated
	 *         by commas.
	 */
	public String getAssignedCPUs() {
		return this.assignedCPUs;
	}

	public boolean getBringsSchedule() {
		return this.bringsSchedule;
	}

	public Integer getClock() {
		return this.clock;
	}

	/**
	 * Get deadline clock value for a job.
	 * 
	 * @return If this is -1, there is no deadline.
	 */
	public Integer getDeadline() {
		return this.deadline;
	}

	public Integer getExpectedEnd() {
		return this.expectedEnd;
	}

	public Integer getExpectedStart() {
		return this.expectedStart;
	}

	@Id
	@GeneratedValue
	public Integer getId() {
		return this.id;
	}

	public Integer getJob() {
		return this.job;
	}

	/**
	 * A "hint" to the renderer as to how to render the job. There is a
	 * "just arrived" hint, a "moved good" hint and a "moved bad" hint.
	 * 
	 * @return The hint.
	 */
	public Integer getJobHint() {
		return this.jobHint;
	}

	@ManyToOne
	public Machine getMachine() {
		return this.srcMachine;
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
	@JoinColumn(name = "parent")
	public Event getParent() {
		return this.parent;
	}

	public void setAssignedCPUs(final String value) {
		this.assignedCPUs = value;
	}

	public void setBringsSchedule(final boolean value) {
		this.bringsSchedule = value;
	}

	public void setClock(final Integer clock) {
		this.clock = clock;
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

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setJob(final Integer value) {
		this.job = value;
	}

	public void setJobHint(final Integer value) {
		this.jobHint = value;
	}

	public void setMachine(final Machine machine) {
		this.srcMachine = machine;
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

}
