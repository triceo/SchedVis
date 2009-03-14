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
package cz.muni.fi.spc.SchedVis.ui.common;

import java.awt.LayoutManager;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * Implements a JPanel that has automatically assigned a border with a title.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 * 
 */
public class JBorderedPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 6524759692406002367L;

    /**
     * @param isDoubleBuffered
     * @param title
     *            A new title for the JPanel's border.
     */
    public JBorderedPanel(final boolean isDoubleBuffered, final String title) {
	super(isDoubleBuffered);
	this.addTitle(title);
    }

    /**
     * @param layout
     * @param isDoubleBuffered
     * @param title
     *            A new title for the JPanel's border.
     */
    public JBorderedPanel(final LayoutManager layout,
	    final boolean isDoubleBuffered, final String title) {
	super(layout, isDoubleBuffered);
	this.addTitle(title);
    }

    /**
     * @param layout
     * @param title
     *            A new title for the JPanel's border.
     */
    public JBorderedPanel(final LayoutManager layout, final String title) {
	super(layout);
	this.addTitle(title);
    }

    /**
     * 
     * @param title
     *            A new title for the JPanel's border.
     */
    public JBorderedPanel(final String title) {
	this.addTitle(title);
    }

    /**
     * Perform the addition of JPanel's border and title.
     * 
     * @param title
     *            A new title for the JPanel's border.
     */
    private void addTitle(final String title) {
	this.setBorder(BorderFactory.createCompoundBorder(BorderFactory
		.createTitledBorder(title), BorderFactory.createEmptyBorder(5,
			5, 5, 5)));
    }

}
