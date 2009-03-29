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
/**
 * 
 */
package cz.muni.fi.spc.SchedVis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.SwingWorker;

import cz.muni.fi.spc.SchedVis.model.BaseEntity;
import cz.muni.fi.spc.SchedVis.model.Database;
import cz.muni.fi.spc.SchedVis.model.entities.Event;
import cz.muni.fi.spc.SchedVis.model.entities.EventType;
import cz.muni.fi.spc.SchedVis.model.entities.Machine;
import cz.muni.fi.spc.SchedVis.parsers.machines.MachineData;
import cz.muni.fi.spc.SchedVis.parsers.machines.MachinesParser;
import cz.muni.fi.spc.SchedVis.parsers.schedule.EventHasData;
import cz.muni.fi.spc.SchedVis.parsers.schedule.EventIsJobRelated;
import cz.muni.fi.spc.SchedVis.parsers.schedule.EventIsMachineRelated;
import cz.muni.fi.spc.SchedVis.parsers.schedule.ScheduleEvent;
import cz.muni.fi.spc.SchedVis.parsers.schedule.ScheduleEventMove;
import cz.muni.fi.spc.SchedVis.parsers.schedule.ScheduleJobData;
import cz.muni.fi.spc.SchedVis.parsers.schedule.ScheduleMachineData;
import cz.muni.fi.spc.SchedVis.parsers.schedule.ScheduleParser;

/**
 * A tool to import data from specific files into the SQLite database used by
 * this application.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class Importer extends SwingWorker<Void, Void> {

	private final File machinesFile;
	private final Integer machinesLineCount;
	private final File dataFile;
	private final Integer dataLineCount;

	private Integer parsedLines = 0;
	private Integer totalLines = 0;

	private boolean result = false;

	private final Map<String, Machine> machines = new HashMap<String, Machine>();

	public Importer(final File machinesFile, final File dataFile) {
		this.machinesFile = machinesFile;
		this.machinesLineCount = this.countLines(machinesFile);
		this.dataFile = dataFile;
		this.dataLineCount = this.countLines(dataFile);
	}

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
		} catch (cz.muni.fi.spc.SchedVis.parsers.ParseException e) {
			return null;
		}
		this.result = true;
		return null;
	}

	/**
	 * @todo Remove this once second-level caching works fine.
	 * 
	 * @param name
	 * @return
	 */
	private Machine getMachine(final String name) {
		if (!this.machines.containsKey(name)) {
			this.machines.put(name, Machine.getWithName(name));
		}
		return this.machines.get(name);
	}

	public boolean isSuccess() {
		return this.result;
	}

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
	 * Parse the data set and insert its data into database.
	 * 
	 * @param reader
	 * @throws ParseException
	 * @todo Somehow make jobs a table of its own.
	 * @todo Somehow make assigned-CPUs a table if its own.
	 */
	private void parseDataSet(final BufferedReader reader)
	    throws cz.muni.fi.spc.SchedVis.parsers.ParseException {
		this.setProgress(0);
		final Map<String, Integer> eventTypes = new HashMap<String, Integer>();
		eventTypes.put("job-arrival", EventType.EVENT_JOB_ARRIVAL);
		eventTypes.put("job-execution-start", EventType.EVENT_JOB_EXECUTION_START);
		eventTypes.put("job-cancel", EventType.EVENT_JOB_CANCEL);
		eventTypes.put("good-move", EventType.EVENT_JOB_MOVE_GOOD);
		eventTypes.put("bad-move", EventType.EVENT_JOB_MOVE_BAD);
		eventTypes.put("machine-failure", EventType.EVENT_MACHINE_FAILURE);
		eventTypes.put("machine-failure-move-good",
		    EventType.EVENT_MACHINE_FAILURE_JOB_MOVE_GOOD);
		eventTypes.put("machine-failure-move-bad",
		    EventType.EVENT_MACHINE_FAILURE_JOB_MOVE_BAD);
		eventTypes.put("machine-restart", EventType.EVENT_MACHINE_RESTART);
		eventTypes.put("machine-restart-move-good",
		    EventType.EVENT_MACHINE_RESTART_JOB_MOVE_GOOD);
		eventTypes.put("machine-restart-move-bad",
		    EventType.EVENT_MACHINE_RESTART_JOB_MOVE_BAD);
		final Iterator<Map.Entry<String, Integer>> eventTypeIterator = eventTypes
		    .entrySet().iterator();
		final List<EventType> etl = new Vector<EventType>();
		while (eventTypeIterator.hasNext()) {
			final EventType et = new EventType();
			final Map.Entry<String, Integer> item = eventTypeIterator.next();
			et.setId(item.getValue());
			et.setName(item.getKey());
			etl.add(et);
		}
		Database.persist(etl);
		// parse data set
		this.parsedLines = 0;
		this.totalLines = this.dataLineCount;
		final ScheduleParser parser = new ScheduleParser(reader);
		parser.setImporter(this);
		final List<ScheduleEvent> events = parser.read();
		// fill the event's table
		final Integer totalEvents = events.size();
		Integer lineId = 0;
		Integer eventId = 0;
		final List<BaseEntity> bel = new Vector<BaseEntity>();
		for (final ScheduleEvent event : events) {
			lineId++;
			eventId++;
			final Event evt = new Event();
			evt.setType(Database.getEntityManager().find(EventType.class,
			    eventTypes.get(event.getName())));
			evt.setClock(event.getClock());
			if (event instanceof EventIsJobRelated) {
				evt.setJob(((EventIsJobRelated) event).getJob());
			}
			if (event instanceof EventIsMachineRelated) {
				evt.setSourceMachine(this.getMachine(((EventIsMachineRelated) event)
				    .getMachine()));
				if (event instanceof ScheduleEventMove) {
					evt.setTargetMachine(this.getMachine(((ScheduleEventMove) event)
					    .getTargetMachine()));
				}
			}
			bel.add(evt);
			if (event instanceof EventHasData) {
				final List<ScheduleMachineData> data = ((EventHasData) event).getData();
				for (final ScheduleMachineData machine : data) {
					eventId++;
					for (final ScheduleJobData job : machine.getJobs()) {
						final Event evt2 = new Event();
						evt2.setClock(event.getClock());
						evt2.setSourceMachine(this.getMachine(machine.getMachineId()));
						evt2.setNeededCPUs(job.getNeededCPUs());
						evt2.setAssignedCPUs(job.getAssignedCPUs());
						evt2.setNeededPlatform(job.getArch());
						evt2.setNeededRAM(job.getNeededMemory());
						evt2.setNeededHDD(job.getNeededSpace());
						evt2.setDeadline(job.getDeadline());
						evt2.setExpectedStart(job.starts());
						evt2.setExpectedEnd(job.ends());
						evt2.setJob(job.getId());
						evt2.setParent(evt);
						bel.add(evt2);
					}
				}
			}
			// update progress
			final Double progress = (((lineId * 100) / (double) totalEvents) / 2) + 50;
			this.setProgress(progress.intValue());
			if (bel.size() > 2000) { // persist some items
				Database.persist(bel);
				bel.clear();
			}
		}
		Database.persist(bel);
	}

	/**
	 * Parse the machines' input file, each line of which looks like this:
	 * "${NAME};${CPUs};${SPEED};${PLATFORM};${OS};${RAM};${HDD}" Where: ${NAME}
	 * is the name of the machine, ${CPUs} is the number of processors inside,
	 * ${SPEED} is the total cumulative speed of those CPUs (in MIPS), ${PLATFORM}
	 * is the architecture that the machine uses, ${OS} is the operating system
	 * the machine runs, ${RAM} is the amount of RAM available in the machine (in
	 * MBs) and ${HDD} is the amount of hard drive space available (in MBs)
	 * 
	 * @param reader
	 * @throws ParseException
	 */
	private void parseMachines(final BufferedReader reader)
	    throws cz.muni.fi.spc.SchedVis.parsers.ParseException {
		// ready the parser
		this.parsedLines = 0;
		this.totalLines = this.machinesLineCount;
		final MachinesParser parser = new MachinesParser(reader);
		parser.setImporter(this);
		final List<MachineData> machines = parser.read();
		// fill the machines' table
		final Integer totalMachines = machines.size();
		Integer machineId = 0;
		final List<Machine> machinesList = new Vector<Machine>();
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
		Database.persist(machinesList);
	}

}
