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
package cz.muni.fi.spc.SchedVis.rendering;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

/**
 * A class whose only purpose is to write an image to a file. It is used for
 * delayed writing of just-rendered schedule images.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class MachineFileWriter extends SwingWorker<Void, Void> {

	private final BufferedImage img;
	private final File f;
	private final static Logger logger = Logger
	    .getLogger(MachineFileWriter.class);

	/**
	 * Class constructor.
	 * 
	 * @param img
	 *          Image to write.
	 * @param f
	 *          File to write the image in.
	 */
	public MachineFileWriter(final BufferedImage img, final File f) {
		this.img = img;
		this.f = f;
	}

	/**
	 * This is where the writing happens.
	 */
	@Override
	protected Void doInBackground() throws Exception {
		try {
			ImageIO.write(this.img, "gif", this.f);
		} catch (IOException e) {
			MachineFileWriter.logger.warn("Failed to write into a file "
			    + this.f.getAbsolutePath() + ". Won't cache machine.");
		}
		return null;
	}

}
