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

import cz.muni.fi.spc.SchedVis.background.Importer;
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

	public static final int JOB_HINT_NONE = 0;
	public static final int JOB_HINT_MOVE_OK = 1;
	public static final int JOB_HINT_MOVE_NOK = 2;
	public static final int JOB_HINT_ARRIVAL = 3;
	private static int maxJobSpan = -1;

	/**
	 * Get the maximum length of a job. Actually computed only first time, every
	 * other time over the app instance lifetime, the first result is retrieved.
	 * 
	 * @return The length in ticks.
	 */
	public static int getMaxSpan() {
		if (Job.maxJobSpan == -1) {
			Job.maxJobSpan = (Integer) ((Session) Database.getEntityManager()
			    .getDelegate())
			    .createSQLQuery(
			        "SELECT max(expectedEnd - clock) AS s FROM Job GROUP BY parent, machine_id ORDER BY s DESC LIMIT 1") //$NON-NLS-1$
			    .list().get(0);
		}
		return Job.maxJobSpan;
	}

	private int id;
	private Machine srcMachine;
	private int deadline;
	private int expectedEnd;
	private int expectedStart;
	private int job;
	private int neededCPUs;
	private int neededHDD;
	private int neededRAM;
	private String neededPlatform;
	private Event parent;
	private String assignedCPUs;
	private int clock;
	private boolean bringsSchedule = false;
	private int jobHint = Job.JOB_HINT_NONE;

	@Override
	public int compareTo(final Job o) {
		return Integer.valueOf(this.getId()).compareTo(Integer.valueOf(o.getId()));
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

	public int getClock() {
		return this.clock;
	}

	/**
	 * Get deadline clock value for a job.
	 * 
	 * @return If this is -1, there is no deadline.
	 */
	public int getDeadline() {
		return this.deadline;
	}

	public int getExpectedEnd() {
		return this.expectedEnd;
	}

	public int getExpectedStart() {
		return this.expectedStart;
	}

	@Id
	@GeneratedValue
	public int getId() {
		return this.id;
	}

	public int getJob() {
		return this.job;
	}

	/**
	 * A "hint" to the renderer as to how to render the job. There is a
	 * "just arrived" hint, a "moved good" hint and a "moved bad" hint. See
	 * {@link Importer} class for more details.
	 * 
	 * @return The hint.
	 */
	public int getJobHint() {
		return this.jobHint;
	}

	@ManyToOne
	public Machine getMachine() {
		return this.srcMachine;
	}

	public int getNeededCPUs() {
		return this.neededCPUs;
	}

	public int getNeededHDD() {
		return this.neededHDD;
	}

	public String getNeededPlatform() {
		return this.neededPlatform;
	}

	public int getNeededRAM() {
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

	public void setClock(final int clock) {
		this.clock = clock;
	}

	public void setDeadline(final int value) {
		this.deadline = value;
	}

	public void setExpectedEnd(final int value) {
		this.expectedEnd = value;
	}

	public void setExpectedStart(final int value) {
		this.expectedStart = value;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public void setJob(final int value) {
		this.job = value;
	}

	public void setJobHint(final int value) {
		this.jobHint = value;
	}

	public void setMachine(final Machine machine) {
		this.srcMachine = machine;
	}

	public void setNeededCPUs(final int value) {
		this.neededCPUs = value;
	}

	public void setNeededHDD(final int value) {
		this.neededHDD = value;
	}

	public void setNeededPlatform(final String value) {
		this.neededPlatform = value;
	}

	public void setNeededRAM(final int value) {
		this.neededRAM = value;
	}

	public void setParent(final Event parent) {
		this.parent = parent;
	}

}
