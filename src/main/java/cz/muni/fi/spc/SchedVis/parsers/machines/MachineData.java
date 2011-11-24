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
public final class MachineData {

	private final char[] name;
	private final int numCPUs;
	private final int speed;
	private final char[] arch;
	private final char[] os;
	private final int memory;
	private final int space;

	public MachineData(final Token name, final Token numCPUs, final Token speed,
	    final Token arch, final Token os, final Token mem, final Token space) {
		this.name = name.toString().toCharArray();
		this.numCPUs = Integer.valueOf(numCPUs.toString()).intValue();
		this.speed = Integer.valueOf(speed.toString()).intValue();
		this.arch = arch.toString().toCharArray();
		this.os = os.toString().toCharArray();
		this.memory = Integer.valueOf(mem.toString()).intValue();
		this.space = Integer.valueOf(space.toString()).intValue();
	}

	public String getArchitecture() {
		return String.valueOf(this.arch);
	}

	public int getCPUCount() {
		return this.numCPUs;
	}

	public int getMemory() {
		return this.memory;
	}

	public String getName() {
		return String.valueOf(this.name);
	}

	public String getOperatingSystem() {
		return String.valueOf(this.os);
	}

	public int getSpace() {
		return this.space;
	}

	public int getSpeed() {
		return this.speed;
	}
}
