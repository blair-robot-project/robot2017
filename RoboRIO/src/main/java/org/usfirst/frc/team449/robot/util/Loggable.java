package org.usfirst.frc.team449.robot.util;

/**
 * Created by noah on 5/14/17.
 */
public interface Loggable {

	/**
	 * Get the headers for the data this subsystem logs every loop.
	 * @return A string consisting of N comma-separated labels for data, where N is the length of the Object[] returned by getData().
	 */
	String getHeader();

	/**
	 * Get the data this subsystem logs every loop.
	 * @return An N-length array of Objects, where N is the number of labels given by getHeader.
	 */
	Object[] getData();

	/**
	 * Get the name of this object.
	 * @return A string that will identify this object in the log file.
	 */
	String getName();
}
