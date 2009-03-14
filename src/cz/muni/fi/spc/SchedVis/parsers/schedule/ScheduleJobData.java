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
package cz.muni.fi.spc.SchedVis.parsers.schedule;

/**
 * Class representing a data holder for job data inside schedule. Gets filled
 * from the parser, gets read in the Importer.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 */
public class ScheduleJobData {

    private Integer id;
    private Integer neededCPUs;
    private Integer neededMemory;
    private Integer neededSpace;
    private Integer startClock;
    private Integer endClock;
    private Integer deadline;
    private String architecture;
    private String assignedCPUs;

    public void assignCPUs(final Token assignedCPUs) {
	this.assignedCPUs = assignedCPUs.toString();
    }

    public Integer ends() {
	return this.endClock;
    }

    public String getArch() {
	return this.architecture;
    }

    public String getAssignedCPUs() {
	return this.assignedCPUs;
    }

    public Integer getDeadline() {
	return this.deadline;
    }

    public Integer getId() {
	return this.id;
    }

    public Integer getNeededCPUs() {
	return this.neededCPUs;
    }

    public Integer getNeededMemory() {
	return this.neededMemory;
    }

    public Integer getNeededSpace() {
	return this.neededSpace;
    }

    public void setArch(final Token arch) {
	this.architecture = arch.toString();
    }

    public void setDeadline(final Token clock) {
	this.deadline = new Integer(clock.toString());
    }

    public void setEnds(final Token clock) {
	this.endClock = new Integer(clock.toString());
    }

    public void setId(final Token jobId) {
	this.id = new Integer(jobId.toString());
    }

    public void setNeedsCPUs(final Token neededCPUs) {
	this.neededCPUs = new Integer(neededCPUs.toString());
    }

    public void setNeedsMemory(final Token memory) {
	this.neededMemory = new Integer(memory.toString());
    }

    public void setNeedsSpace(final Token space) {
	this.neededSpace = new Integer(space.toString());
    }

    public void setStarts(final Token clock) {
	this.startClock = new Integer(clock.toString());
    }

    public Integer starts() {
	return this.startClock;
    }
}
