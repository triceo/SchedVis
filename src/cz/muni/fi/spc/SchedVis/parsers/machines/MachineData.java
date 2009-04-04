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
/**
 * 
 */
package cz.muni.fi.spc.SchedVis.parsers.machines;

import cz.muni.fi.spc.SchedVis.parsers.Token;

/**
 * Holds information about a machine loaded from the data set. Written into by
 * parsers, read by importer.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class MachineData {

	private final String name;
	private final Integer numCPUs;
	private final Integer speed;
	private final String arch;
	private final String os;
	private final Integer memory;
	private final Integer space;

	public MachineData(final Token name, final Token numCPUs, final Token speed,
	    final Token arch, final Token os, final Token mem, final Token space) {
		this.name = name.toString();
		this.numCPUs = new Integer(numCPUs.toString());
		this.speed = new Integer(speed.toString());
		this.arch = arch.toString();
		this.os = os.toString();
		this.memory = new Integer(mem.toString());
		this.space = new Integer(speed.toString());
	}

	public String getArchitecture() {
		return this.arch;
	}

	public Integer getCPUCount() {
		return this.numCPUs;
	}

	public Integer getMemory() {
		return this.memory;
	}

	public String getName() {
		return this.name;
	}

	public String getOperatingSystem() {
		return this.os;
	}

	public Integer getSpace() {
		return this.space;
	}

	public Integer getSpeed() {
		return this.speed;
	}
}
