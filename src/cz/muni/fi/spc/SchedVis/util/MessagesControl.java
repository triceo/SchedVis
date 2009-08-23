package cz.muni.fi.spc.SchedVis.util;

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
 * JDK 6's ResourceBundle.Control subclass that allows loading of bundles in
 * UTF-8.
 */
public class MessagesControl extends ResourceBundle.Control {

	private Reader getResourceReader(final URL resourceURL) throws IOException {
		try {
			// This permission has already been checked by
			// ClassLoader.getResource(String), which will return null
			// in case the code has not enough privileges.
			return AccessController
			    .doPrivileged(new PrivilegedExceptionAction<Reader>() {
				    public Reader run() throws IOException {
					    URLConnection connection = resourceURL.openConnection();
					    connection.setUseCaches(false);
					    return new BufferedReader(new InputStreamReader(connection
					        .getInputStream()));
				    }
			    });
		} catch (PrivilegedActionException x) {
			throw (IOException) x.getCause();
		}
	}

	@Override
	public ResourceBundle newBundle(final String baseName, final Locale locale,
	    final String format, final ClassLoader loader, final boolean reload)
	    throws IllegalAccessException, InstantiationException, IOException {
		String bundleName = this.toBundleName(baseName, locale);
		String resourceName = this.toResourceName(bundleName, "properties");
		final URL resourceURL = loader.getResource(resourceName);
		if (resourceURL == null) {
			return null;
		}
		return new PropertyResourceBundle(this.getResourceReader(resourceURL));
	}
}