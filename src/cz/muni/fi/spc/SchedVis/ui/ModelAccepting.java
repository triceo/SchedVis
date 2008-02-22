/**
 * 
 */
package cz.muni.fi.spc.SchedVis.ui;

/**
 * Interface for all UI elements that accept a data model for their operation.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 *
 */
public interface ModelAccepting {
	
	/**
	 * Re-render the element.
	 */
	public void refresh();
	
	/**
	 * Set the model for the element.
	 */
	public void setModel(cz.muni.fi.spc.SchedVis.model.Model model);

}
