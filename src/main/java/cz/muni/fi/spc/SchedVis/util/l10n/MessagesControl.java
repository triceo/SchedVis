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
package cz.muni.fi.spc.SchedVis.util.l10n;

import java.io.IOException;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * JDK 6's ResourceBundle.Control subclass that allows loading of bundles
 * encoded in UTF-8.
 * 
 * @author Lukáš Petrovický <petrovicky@mail.muni.cz>
 */
public class MessagesControl extends ResourceBundle.Control {

	@Override
	public ResourceBundle newBundle(final String baseName, final Locale locale,
	    final String format, final ClassLoader loader, final boolean reload)
	    throws IllegalAccessException, InstantiationException, IOException {
		final String bundleName = this.toBundleName(baseName, locale);
		final String resourceName = this.toResourceName(bundleName, "properties");
		return new PropertyResourceBundle(this.getClass().getResourceAsStream(
		    "/l10n/" + resourceName));
	}
}