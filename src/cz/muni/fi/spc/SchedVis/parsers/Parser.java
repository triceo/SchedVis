/**
 * 
 */
package cz.muni.fi.spc.SchedVis.parsers;

import cz.muni.fi.spc.SchedVis.Importer;

/**
 * Base class for SchedVis auto-generated parsers, which should provide some
 * basic common methods.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * @todo Implement reporting progress of parsing.
 */
public class Parser {

	private Importer	importer;

	public Importer getImporter() {
		return this.importer;
	}

	public void setImporter(final Importer importer) {
		this.importer = importer;
	}

}
