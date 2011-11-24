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
package cz.muni.fi.spc.SchedVis.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

import cz.muni.fi.spc.SchedVis.model.EventType;
import cz.muni.fi.spc.SchedVis.model.JobHint;
import cz.muni.fi.spc.SchedVis.model.entities.Event;
import cz.muni.fi.spc.SchedVis.model.entities.Job;
import cz.muni.fi.spc.SchedVis.model.entities.Machine;
import cz.muni.fi.spc.SchedVis.model.entities.MachineGroup;
import cz.muni.fi.spc.SchedVis.parsers.ParseException;
import cz.muni.fi.spc.SchedVis.parsers.machines.MachineData;
import cz.muni.fi.spc.SchedVis.parsers.machines.MachinesParser;
import cz.muni.fi.spc.SchedVis.parsers.schedule.EventHasData;
import cz.muni.fi.spc.SchedVis.parsers.schedule.EventIsJobRelated;
import cz.muni.fi.spc.SchedVis.parsers.schedule.EventIsMachineRelated;
import cz.muni.fi.spc.SchedVis.parsers.schedule.ScheduleEvent;
import cz.muni.fi.spc.SchedVis.parsers.schedule.ScheduleEventIO;
import cz.muni.fi.spc.SchedVis.parsers.schedule.ScheduleEventMove;
import cz.muni.fi.spc.SchedVis.parsers.schedule.ScheduleJobData;
import cz.muni.fi.spc.SchedVis.parsers.schedule.ScheduleMachineData;
import cz.muni.fi.spc.SchedVis.parsers.schedule.ScheduleParser;
import cz.muni.fi.spc.SchedVis.util.l10n.Messages;

/**
 * A tool to import data from specific files into the SQL database used by this
 * application.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public final class Importer extends SwingWorker<Void, Void> {

	private final File machinesFile;

	private final Integer machinesLineCount;
	private final File dataFile;
	private final Integer dataLineCount;
	private Integer parsedLines = 0;

	private Integer totalLines = 0;
	private boolean result = false;

	private final Map<Integer, Job> allJobs = new HashMap<Integer, Job>();

	private final Map<String, String[]> CPUstatus = new HashMap<String, String[]>();

	/**
	 * A constructor to the class.
	 * 
	 * @param machinesFile
	 *          A file to read the list of available machines from.
	 * @param dataFile
	 *          A file to read the list of events from.
	 */
	public Importer(final File machinesFile, final File dataFile) {
		this.machinesFile = machinesFile;
		this.machinesLineCount = this.countLines(machinesFile);
		this.dataFile = dataFile;
		this.dataLineCount = this.countLines(dataFile);
	}

	private String convertCPUs(final String[] cpus) {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < cpus.length; i++) {
			sb.append(cpus[i]);
			if (i < (cpus.length - 1)) {
				sb.append(",");
			}
		}
		return sb.toString();
	}

	/**
	 * Calculate the number of lines in a given file.
	 * 
	 * @param file
	 *          The file to count the lines in.
	 * @return Number of lines in a file.
	 */
	private Integer countLines(final File file) {
		try {
			final LineNumberReader reader = new LineNumberReader(new FileReader(file));
			Integer count = 0;
			while (reader.readLine() != null) {
				count++;
			}
			return count;
		} catch (final FileNotFoundException e) {
			return 0;
		} catch (final IOException e) {
			return 0;
		}
	}

	/**
	 * The method that is executed when this SwingWorker is executed. Handles all
	 * the reading, parsing and storing in the database of all the required data.
	 */
	@Override
	public Void doInBackground() {
		if (!this.machinesFile.canRead() || !this.dataFile.canRead()) {
			return null;
		}
		try {
			this.parseMachines(new BufferedReader(new FileReader(this.machinesFile)));
			this.parseDataSet(new BufferedReader(new FileReader(this.dataFile)));
		} catch (final FileNotFoundException e) {
			return null;
		} catch (final ParseException e) {
			return null;
		}
		this.result = true;
		return null;
	}

	/**
	 * Whether or not the task finished and succeeded.
	 * 
	 * @return True when the task is over and successful, false otherwise.
	 */
	public boolean isSuccess() {
		return this.isDone() && this.result;
	}

	/**
	 * Update progress of the overall task when the next line has been parsed.
	 */
	public void nextLineParsed() {
		this.parsedLines++;
		final Double progress = (this.parsedLines * 100)
		    / (double) (this.totalLines + 1);
		if (progress > 100) {
			this.setProgress(100);
		} else if (progress < 0) {
			this.setProgress(0);
		} else {
			this.setProgress(progress.intValue());
		}
	}

	/**
	 * Parse the available events and insert the data into database, each line of
	 * which looks as defined in the grammar.
	 * 
	 * @param reader
	 *          Reader pointing to the data set to parse.
	 * @throws ParseException
	 *           When, for some reason, the file cannot be parsed. Might indicate
	 *           a syntax error or, less possibly, JavaCC bug.
	 */
	private void parseDataSet(final BufferedReader reader) throws ParseException {
		this.setProgress(0);
		try {
			// parse data set
			this.parsedLines = 0;
			this.totalLines = this.dataLineCount;
			final ScheduleParser parser = new ScheduleParser(reader);
			parser.setImporter(this);
			final List<ScheduleEvent> events;
			try {
				events = parser.read();
			} catch (final Throwable e) {
				e.printStackTrace();
				throw new ParseException();
			}
			// fill the event's table
			final Integer totalEvents = events.size();
			Integer lineId = 0;
			Integer eventId = 0;
			Database.getEntityManager().getTransaction().begin();
			for (final ScheduleEvent event : events) {
				lineId++;
				eventId++;
				final Event evt = new Event();
				evt.setType(EventType.getWithName(event.getName()));
				evt.setClock(event.getClock());
				JobHint jobHint = JobHint.NONE;
				if (event instanceof EventIsJobRelated) {
					evt.setJob(((EventIsJobRelated) event).getJob());
					if (event.getName().equals("job-arrival")) {
						// if in this clock a new job arrives, remember it
						jobHint = JobHint.ARRIVAL;
					} else if (event.getName().equals("job-execution-start")) {
						this.processUsedCPUs((ScheduleEventIO) event);
					} else if (event.getName().equals("good-move")
					    || event.getName().equals("machine-failure-move-good")) {
						jobHint = JobHint.MOVE_OK;
					} else if (event.getName().equals("bad-move")
					    || event.getName().equals("machine-failure-move-bad")) {
						jobHint = JobHint.MOVE_NOK;
					} else if (event.getName().equals("job-completion")) {
						this.processUsedCPUs((ScheduleEventIO) event);
					}
				}
				if (event instanceof EventIsMachineRelated) {
					evt.setSourceMachine(Machine.getWithName(
					    ((EventIsMachineRelated) event).getMachine(), true));
					if (event instanceof ScheduleEventMove) {
						evt.setTargetMachine(Machine.getWithName(
						    ((ScheduleEventMove) event).getTargetMachine(), true));
					}
				}
				Database.persist(evt);
				if (event instanceof EventHasData) {
					for (final ScheduleMachineData machine : ((EventHasData) event)
					    .getData()) {
						eventId++;
						/*
						 * And now post the real schedule.
						 */
						for (final ScheduleJobData job : machine.getJobs()) {
							final Job schedule1 = new Job();
							schedule1.setBringsSchedule(true);
							schedule1.setMachine(Machine.getWithName(machine.getMachineId(),
							    true));
							schedule1.setNeededCPUs(job.getNeededCPUs());
							schedule1.setAssignedCPUs(job.getAssignedCPUs());
							schedule1.setNeededPlatform(job.getArch());
							schedule1.setNeededRAM(job.getNeededMemory());
							schedule1.setNeededHDD(job.getNeededSpace());
							schedule1.setDeadline(job.getDeadline());
							schedule1.setExpectedStart(job.starts());
							schedule1.setExpectedEnd(job.ends());
							schedule1.setNumber(job.getId());
							schedule1.setClock(evt.getClock());
							schedule1.setParent(evt);
							if (job.getId() == ((EventIsJobRelated) event).getJob()) {
								schedule1.setHint(jobHint);
							}
							this.allJobs.put(job.getId(), schedule1);
							Database.persist(schedule1);
						}
						/*
						 * Create a "dummy" event so that we know that the given machine
						 * posted some schedule. This is used to tell when the schedule is
						 * empty. (A "dummy" is found but the real schedule is not.)
						 */
						final Job schedule2 = new Job();
						schedule2.setMachine(Machine.getWithName(machine.getMachineId(),
						    true));
						try {
							schedule2.setAssignedCPUs(this.convertCPUs(this.CPUstatus
							    .get(schedule2.getMachine().getName())));
						} catch (final NullPointerException ex) {
							schedule2.setAssignedCPUs("");
						}
						schedule2.setClock(evt.getClock());
						schedule2.setBringsSchedule(false);
						schedule2.setParent(evt);
						Database.persist(schedule2);
					}
				}
				// update progress
				final Double progress = (((lineId * 100) / (double) totalEvents) / 2) + 50;
				this.setProgress(progress.intValue());
				if (eventId % 1000 == 0) { // persist some items
					try {
						Database.getEntityManager().getTransaction().commit();
						Database.getEntityManager().clear(); // save some memory
						Database.getEntityManager().getTransaction().begin();
					} catch (final Throwable e) {
						e.printStackTrace();
					}
				}
				event.clear(); // save some more memory
			}
			Database.getEntityManager().getTransaction().commit();
		} catch (final Throwable t) {
			t.printStackTrace();
		}
	}

	/**
	 * Parse the machines' input file, each line of which looks as defined in the
	 * grammar.
	 * 
	 * @param reader
	 *          Reader pointing to the data set to parse.
	 * @throws ParseException
	 *           When, for some reason, the file cannot be parsed. Might indicate
	 *           a syntax error or, less possibly, JavaCC bug.
	 */
	private void parseMachines(final BufferedReader reader) throws ParseException {
		// ready the parser
		this.parsedLines = 0;
		this.totalLines = this.machinesLineCount;
		final MachinesParser parser = new MachinesParser(reader);
		parser.setImporter(this);
		List<MachineData> machines = null;
		try {
			machines = parser.read();
		} catch (final Throwable e) {
			e.printStackTrace();
			throw new ParseException();
		}
		// fill the machines' table
		final Integer totalMachines = machines.size();
		Integer machineId = 0;
		final List<Machine> machinesList = new ArrayList<Machine>();
		for (final MachineData machine : machines) {
			machineId++;
			// persist data
			final Machine mcn = new Machine();
			mcn.setName(machine.getName());
			mcn.setCPUs(machine.getCPUCount());
			mcn.setSpeed(machine.getSpeed());
			mcn.setOS(machine.getOperatingSystem());
			mcn.setPlatform(machine.getArchitecture());
			mcn.setHDD(machine.getSpace());
			mcn.setRAM(machine.getMemory());
			machinesList.add(mcn);
			// update progress
			final Double progress = ((machineId * 100) / (double) totalMachines) / 2;
			this.setProgress(progress.intValue());
		}
		try {
			final List<MachineGroup> groupsList = new ArrayList<MachineGroup>();
			if (Configuration.createGroupPerMachine()) {
				for (final Machine m : machinesList) {
					final MachineGroup mg = new MachineGroup();
					mg.setName(new Formatter().format(Messages.getString("Importer.19"),
					    m.getName()).toString());
					groupsList.add(mg);
					m.setGroup(mg);
				}
			}
			Database.persist(groupsList);
			Database.persist(machinesList);
		} catch (final Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles the currently used CPUs for a given machine.
	 * 
	 * The point of this method is to always keep the current CPU usage for every
	 * machine, so that the database can contain the actual representation of the
	 * CPU load.
	 * 
	 * @param e
	 *          The event that is modifying the CPU usage of a machine.
	 * @return Comma-delimited list of CPUs being used on a given machine after
	 *         this event.
	 */
	private void processUsedCPUs(final ScheduleEventIO e) {
		final int jobId = e.getJob();
		String[] jobCPUs;
		try {
			jobCPUs = this.allJobs.get(jobId).getAssignedCPUs().split(",");
		} catch (final Exception ex) {
			jobCPUs = new String[] {};
		}
		String machineId = "";
		try {
			machineId = this.allJobs.get(jobId).getMachine().getName();
		} catch (final NullPointerException ex) {
			Logger.getLogger(Importer.class).warn(
			    new Formatter().format(Messages.getString("Importer.22"), jobId));
		}
		if (e.getName().equals("job-execution-start")) {
			// execution starting
			if (!this.CPUstatus.containsKey(machineId)) {
				this.CPUstatus.put(machineId, jobCPUs);
			} else {
				final Set<String> old = new HashSet<String>(Arrays
				    .asList(this.CPUstatus.get(machineId)));
				final boolean isChanged = old.addAll(Arrays.asList(jobCPUs));
				if (!isChanged) {
					Logger.getLogger(Importer.class).warn(
					    new Formatter().format(Messages.getString("Importer.24"),
					        new Object[] { jobId, machineId }));
				}
				this.CPUstatus.remove(machineId);
				this.CPUstatus.put(machineId, old.toArray(new String[] {}));
			}
		} else {
			// execution finished
			final Set<String> old = new HashSet<String>(Arrays.asList(this.CPUstatus
			    .get(machineId)));
			final boolean isChanged = old.removeAll(Arrays.asList(jobCPUs));
			if (!isChanged) {
				Logger.getLogger(Importer.class).warn(
				    new Formatter().format(Messages.getString("Importer.25"),
				        new Object[] { jobId, machineId }));
			}
			this.CPUstatus.remove(machineId);
			this.CPUstatus.put(machineId, old.toArray(new String[] {}));
		}
	}
}
