package cz.muni.fi.spc.SchedVis;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.filechooser.FileFilter;

/**
 * A simple FileFilter class that works by filename extension, like the one in
 * the JDK demo called ExampleFileFilter, which has been announced to be
 * supported in a future Swing release.
 */
public class DataFileFilter extends FileFilter {
	protected String				description;

	protected List<String>	exts	= new ArrayList<String>();

	/** Return true if the given file is accepted by this filter. */
	@Override
	public boolean accept(final File f) {
		// If we don't do this, only directory names ending in one of the
		// extensions appear in the window.
		if (f.isDirectory()) {
			return true;

		} else if (f.isFile()) {
			final Iterator<String> it = this.exts.iterator();
			while (it.hasNext()) {
				if (f.getName().endsWith(it.next())) {
					return true;
				}
			}
		}

		// A file that didn't match, or a weirdo (e.g. UNIX device file?).
		return false;
	}

	public void addType(final String s) {
		this.exts.add(s);
	}

	/** Return the printable description of this filter. */
	@Override
	public String getDescription() {
		return this.description;
	}

	/** Set the printable description of this filter. */
	public void setDescription(final String s) {
		this.description = s;
	}
}