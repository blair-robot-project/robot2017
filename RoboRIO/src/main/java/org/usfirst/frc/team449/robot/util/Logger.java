package org.usfirst.frc.team449.robot.util;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import maps.org.usfirst.frc.team449.robot.util.LoggerMap;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by noah on 5/14/17.
 */
public class Logger implements Runnable{

	private static List<LogEvent> events = new ArrayList<>();
	private FileWriter eventLogWriter;
	private FileWriter telemetryLogWriter;
	private Loggable[] subsystems;
	private String[][] itemNames;

	public Logger(LoggerMap.Logger map, List<Loggable> subsystems) throws IOException {
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		eventLogWriter = new FileWriter(map.getEventLogFilename() + timeStamp + ".csv");
		telemetryLogWriter = new FileWriter(map.getTelemetryLogFilename() + timeStamp + ".csv");
		this.subsystems = (Loggable[]) subsystems.toArray();
		itemNames = new String[this.subsystems.length][];
		eventLogWriter.write("time,class,message");
		StringBuilder telemetryHeader = new StringBuilder();
		for (int i = 0; i < this.subsystems.length; i++){
			String[] items = this.subsystems[i].getHeader().split(",");
			for (int j = 0; j < items.length; j++){
				String itemName = this.subsystems[i].getName()+"."+items[j];
				itemNames[i][j] = itemName;
				telemetryHeader.append(itemName);
				telemetryHeader.append(",");
			}
		}
		telemetryHeader.append("\n");
		telemetryLogWriter.write(telemetryHeader.toString());
	}

	@Override
	public void run() {
		for (LogEvent event : events){
			try {
				eventLogWriter.write(event.toString());
			} catch (IOException e) {
				System.out.println("Logging failed!");
				e.printStackTrace();
			}
		}
		events = new ArrayList<>();
		StringBuilder telemetryData = new StringBuilder();
		for (int i = 0; i < subsystems.length; i++) {
			Object[] data = subsystems[i].getData();
			for (int j = 0; j < data.length; j++) {
				Object datum = data[j];
				if (datum.getClass().equals(boolean.class) || datum.getClass().equals(Boolean.class)){
					SmartDashboard.putBoolean(itemNames[i][j], (boolean) datum);
				} else if (datum.getClass().equals(int.class) || datum.getClass().equals(Integer.class)){
					SmartDashboard.putNumber(itemNames[i][j], (int) datum);
				} else if (datum.getClass().equals(double.class) || datum.getClass().equals(Double.class)){
					SmartDashboard.putNumber(itemNames[i][j], (double) datum);
				} else if (datum.getClass().equals(long.class) || datum.getClass().equals(Long.class)){
					SmartDashboard.putNumber(itemNames[i][j], (long) datum);
				} else if (datum.getClass().equals(Sendable.class)){
					SmartDashboard.putData(itemNames[i][j], (Sendable) datum);
				} else if (datum.getClass().equals(String.class)){
					SmartDashboard.putString(itemNames[i][j], (String) datum);
				} else {
					SmartDashboard.putString(itemNames[i][j], datum.toString());
				}

				telemetryData.append(datum.toString());
				telemetryData.append(",");
			}
		}
		telemetryData.append("\n");
		try {
			telemetryLogWriter.write(telemetryData.toString());
		} catch (IOException e) {
			System.out.println("Logging failed!");
			e.printStackTrace();
		}
	}

	public static void addEvent(String message, Class caller){
		events.add(new LogEvent(message, caller));
	}

	public void close() throws IOException {
		eventLogWriter.close();
		telemetryLogWriter.close();
	}
}
