/*
 * Created on Aug 29, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.util;

/**
 * @author marius
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Utilities {
	public static void pause(long millis) {
		try {
			Thread.sleep(millis);
		} catch(InterruptedException ignored) {
		}
	}
}
