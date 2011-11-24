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
package cz.muni.fi.spc.SchedVis.parsers.schedule;

import cz.muni.fi.spc.SchedVis.parsers.Token;

/**
 * Class representing a data holder for job data inside schedule. Gets filled
 * from the parser, gets read in the Importer.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 */
public final class ScheduleJobData {

	private int id;
	private int neededCPUs;
	private int neededMemory;
	private int neededSpace;
	private int startClock;
	private int endClock;
	private int deadline;
	private char[] architecture;
	private char[] assignedCPUs;

	public void assignCPUs(final Token assignedCPUs) {
		this.assignedCPUs = assignedCPUs.toString().toCharArray();
	}

	public int ends() {
		return this.endClock;
	}

	public String getArch() {
		return String.valueOf(this.architecture);
	}

	public String getAssignedCPUs() {
		return String.valueOf(this.assignedCPUs);
	}

	public int getDeadline() {
		return this.deadline;
	}

	public int getId() {
		return this.id;
	}

	public int getNeededCPUs() {
		return this.neededCPUs;
	}

	public int getNeededMemory() {
		return this.neededMemory;
	}

	public int getNeededSpace() {
		return this.neededSpace;
	}

	public void setArch(final Token arch) {
		this.architecture = arch.toString().toCharArray();
	}

	public void setDeadline(final Token clock) {
		this.deadline = Integer.valueOf(clock.toString()).intValue();
	}

	public void setEnds(final Token clock) {
		this.endClock = Integer.valueOf(clock.toString()).intValue();
	}

	public void setId(final Token jobId) {
		this.id = Integer.valueOf(jobId.toString()).intValue();
	}

	public void setNeedsCPUs(final Token neededCPUs) {
		this.neededCPUs = Integer.valueOf(neededCPUs.toString()).intValue();
	}

	public void setNeedsMemory(final Token memory) {
		this.neededMemory = Integer.valueOf(memory.toString()).intValue();
	}

	public void setNeedsSpace(final Token space) {
		this.neededSpace = Integer.valueOf(space.toString()).intValue();
	}

	public void setStarts(final Token clock) {
		this.startClock = Integer.valueOf(clock.toString()).intValue();
	}

	public int starts() {
		return this.startClock;
	}
}
