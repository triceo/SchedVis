package cz.muni.fi.spc.SchedVis.parsers;

import java.util.List;
import java.util.Vector;

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
	private final List<Integer> assignedCPUs = new Vector<Integer>();

	public void assignCPUs(final Token assignedCPUs) {
		for (String CPU: assignedCPUs.toString().split(",")) {
			this.assignedCPUs.add(new Integer(CPU));
		}
	}
	
	public List<Integer> getAssignedCPUs() {
		return this.assignedCPUs;
	}

	public Integer getId() {
		return this.id;
	}

	public Integer getNeededCPUs() {
		return this.neededCPUs;
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
	
	public Integer getNeededMemory() {
		return this.neededMemory;
	}

	public void setNeedsSpace(final Token space) {
		this.neededSpace = new Integer(space.toString());
	}
	
	public Integer getNeededSpace() {
		return this.neededSpace;
	}

	public void setStarts(final Token clock) {
		this.startClock = new Integer(clock.toString());
	}
	
	public Integer starts() {
		return this.startClock;
	}

	public void setEnds(final Token clock) {
		this.endClock = new Integer(clock.toString());
	}

	public Integer ends() {
		return this.endClock;
	}

	public void setDeadline(final Token clock) {
		this.deadline = new Integer(clock.toString());
	}
	
	public Integer getDeadline() {
		return this.deadline;
	}
	
	public void setArch(final Token arch) {
		this.architecture = arch.toString();
	}
	
	public String getArch() {
		return this.architecture;
	}
}
