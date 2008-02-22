/**
 * 
 */
package cz.muni.fi.spc.SchedVis.ui;

import java.awt.Container;

/**
 * An interface specifying necessary methods for each UI element to implement.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 *
 */
public interface UIElement {
	
	/**
	 * Get the object representing the interface.
	 * 
	 * @return Some UI component.
	 */
	public Container get();

}
