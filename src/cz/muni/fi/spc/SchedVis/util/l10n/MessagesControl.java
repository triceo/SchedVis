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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
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

	private Reader getResourceReader(final URL resourceURL) throws IOException {
		try {
			/*
			 * This permission has already been checked by
			 * ClassLoader.getResource(String), which will return null in case the
			 * code has not enough privileges.
			 */
			return AccessController
			    .doPrivileged(new PrivilegedExceptionAction<Reader>() {
				    public Reader run() throws IOException {
					    final URLConnection connection = resourceURL.openConnection();
					    connection.setUseCaches(false);
					    return new BufferedReader(new InputStreamReader(connection
					        .getInputStream(), "UTF-8"));
				    }
			    });
		} catch (final PrivilegedActionException x) {
			throw (IOException) x.getCause();
		}
	}

	@Override
	public ResourceBundle newBundle(final String baseName, final Locale locale,
	    final String format, final ClassLoader loader, final boolean reload)
	    throws IllegalAccessException, InstantiationException, IOException {
		final String bundleName = this.toBundleName(baseName, locale);
		final String resourceName = this.toResourceName(bundleName, "properties");
		final URL resourceURL = loader.getResource(resourceName);
		if (resourceURL == null) {
			return null;
		}
		return new PropertyResourceBundle(this.getResourceReader(resourceURL));
	}
}