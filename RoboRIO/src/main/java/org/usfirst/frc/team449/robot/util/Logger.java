package org.usfirst.frc.team449.robot.util;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A logger that logs telemetry data and individual events. Should be run as a separate thread from the main robot loop.
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.StringIdGenerator.class)
public class Logger implements Runnable {

	/**
	 * A list of all events that subsystems and commands have logged that haven't yet been written to a file.
	 */
	private static List<LogEvent> events = new ArrayList<>();

	/**
	 * The filewriter for the event log.
	 */
	private FileWriter eventLogWriter;

	/**
	 * The filewriter for the telemetry data log.
	 */
	private FileWriter telemetryLogWriter;

	/**
	 * An array of all the subsystems with telemetry data to log.
	 */
	private Loggable[] subsystems;

	/**
	 * A 2d array of the names of the each datum logged by each subsystem. Organized as itemNames[subsystem][dataIndex].
	 */
	private String[][] itemNames;

	/**
	 * Construct a logger from a map and a list of subsystems to log telemetry data from.
	 *
	 * @param map        The config map.
	 * @param subsystems The subsystems to log telemetry data from.
	 * @throws IOException If the file names provided from the log can't be written to.
	 */
	public Logger(LoggerMap.Logger map, List<Loggable> subsystems) throws IOException {
		//Set up the file names, using a time stamp to avoid overwriting old log files.
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		eventLogWriter = new FileWriter(map.getEventLogFilename() + timeStamp + ".csv");
		telemetryLogWriter = new FileWriter(map.getTelemetryLogFilename() + timeStamp + ".csv");

		//Set up the list of loggable subsystems.
		this.subsystems = new Loggable[subsystems.size()];
		subsystems.toArray(this.subsystems);

		//Construct itemNames.
		itemNames = new String[this.subsystems.length][];

		//Write the file headers
		eventLogWriter.write("time,class,message");
		//We use a StringBuilder because it's better for building up a string via concatenation.
		StringBuilder telemetryHeader = new StringBuilder();
		for (int i = 0; i < this.subsystems.length; i++) {
			String[] items = this.subsystems[i].getHeader();
			//Initialize itemNames rows
			itemNames[i] = new String[items.length];
			//For each datum
			for (int j = 0; j < items.length; j++) {
				//Format name as Subsystem.dataName
				String itemName = this.subsystems[i].getName() + "." + items[j];
				itemNames[i][j] = itemName;
				telemetryHeader.append(itemName);
				telemetryHeader.append(",");
			}
		}
		telemetryHeader.append("\n");
		//Write the telemetry file header
		telemetryLogWriter.write(telemetryHeader.toString());
	}

	/**
	 * Log an event to be written to the event log file.
	 *
	 * @param message The text of the event to log.
	 * @param caller  The class causing the event. Almost always will be this.getClass().
	 */
	public static void addEvent(String message, Class caller) {
		events.add(new LogEvent(message, caller));
	}

	/**
	 * Print out all logged events to the event log and write all the telemetry data to the telemetry log.
	 */
	@Override
	public void run() {
		try {
			//Log each event to a file
			for (LogEvent event : events) {
				eventLogWriter.write(event.toString() + "\n");
			}
		} catch (IOException e) {
			System.out.println("Logging failed!");
			e.printStackTrace();
		}
		//Collect telemetry data and write it to SmartDashboard and a file.
		events = new ArrayList<>();
		//We use a StringBuilder because it's better for building up a string via concatenation.
		StringBuilder telemetryData = new StringBuilder();
		//Loop through each datum
		for (int i = 0; i < subsystems.length; i++) {
			Object[] data = subsystems[i].getData();
			for (int j = 0; j < data.length; j++) {
				Object datum = data[j];
				//We do this big thing here so we log it to SmartDashboard as the correct data type, so we make each
				//thing into a booleanBox, graph, etc.
				if (datum.getClass().equals(boolean.class) || datum.getClass().equals(Boolean.class)) {
					SmartDashboard.putBoolean(itemNames[i][j], (boolean) datum);
				} else if (datum.getClass().equals(int.class) || datum.getClass().equals(Integer.class)) {
					SmartDashboard.putNumber(itemNames[i][j], (int) datum);
				} else if (datum.getClass().equals(double.class) || datum.getClass().equals(Double.class)) {
					SmartDashboard.putNumber(itemNames[i][j], (double) datum);
				} else if (datum.getClass().equals(long.class) || datum.getClass().equals(Long.class)) {
					SmartDashboard.putNumber(itemNames[i][j], (long) datum);
				} else if (datum.getClass().equals(Sendable.class)) {
					SmartDashboard.putData(itemNames[i][j], (Sendable) datum);
				} else if (datum.getClass().equals(String.class)) {
					SmartDashboard.putString(itemNames[i][j], (String) datum);
				} else {
					SmartDashboard.putString(itemNames[i][j], datum.toString());
				}

				//Build up the line of data
				telemetryData.append(datum.toString());
				telemetryData.append(",");
			}
		}
		telemetryData.append("\n");
		//Log the data to a file.
		try {
			telemetryLogWriter.write(telemetryData.toString());
		} catch (IOException e) {
			System.out.println("Logging failed!");
			e.printStackTrace();
		}
	}

	/**
	 * Close all IO writers.
	 *
	 * @throws IOException if any of the writers throw an exception while being closed.
	 */
	public void close() throws IOException {
		eventLogWriter.close();
		telemetryLogWriter.close();
	}
}
