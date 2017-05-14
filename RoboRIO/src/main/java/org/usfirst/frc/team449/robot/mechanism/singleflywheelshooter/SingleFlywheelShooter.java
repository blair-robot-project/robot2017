package org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team449.robot.MappedSubsystem;
import org.usfirst.frc.team449.robot.Robot;
import org.usfirst.frc.team449.robot.components.RotPerSecCANTalonSRX;
import org.usfirst.frc.team449.robot.util.Loggable;
import org.usfirst.frc.team449.robot.util.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class for the flywheel
 */
public class SingleFlywheelShooter extends MappedSubsystem implements Loggable{
	/**
	 * The flywheel's Talon
	 */
	public RotPerSecCANTalonSRX talon;

	/**
	 * Whether the flywheel is currently commanded to spin
	 */
	public boolean spinning;

	/**
	 * Throttle at which to run the shooter, defaults to 0.5
	 */
	public double throttle;

	/**
	 * Measured start time in milliseconds (for logging)
	 */
	private long startTime;
	/**
	 * Measured max PID error so far (for testing purposes)
	 */
	private double maxError = 0;

	private String logFilename;

	/**
	 * Construct a SingleFlywheelShooter
	 *
	 * @param map config map
	 */
	public SingleFlywheelShooter(maps.org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter
			                             .SingleFlywheelShooterMap.SingleFlywheelShooter map) {
		super(map.getMechanism());
		this.map = map;
		this.talon = new RotPerSecCANTalonSRX(map.getTalon());
		// Write the headers for the logfile.
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		logFilename = map.getLogFilename() + timeStamp + ".csv";

		this.throttle = map.getThrottle();
		Logger.addEvent("Shooter F: " + talon.canTalon.getF(), this.getClass());
	}

	/**
	 * Set the flywheel's percent voltage
	 *
	 * @param sp percent voltage setpoint [-1, 1]
	 */
	private void setVBusSpeed(double sp) {
		talon.setPercentVbus(sp);
	}

	/**
	 * Set the flywheel's percent PID velocity setpoint
	 *
	 * @param sp percent PID velocity setpoint [-1, 1]
	 */
	private void setPIDSpeed(double sp) {
		talon.setSpeed(talon.getMaxSpeed() * sp);
	}

	/**
	 * A wrapper around the speed method we're currently using/testing
	 *
	 * @param sp The speed to go at [-1, 1]
	 */
	public void setDefaultSpeed(double sp) {
		setPIDSpeed(sp);
	}

	/**
	 * Log data
	 *
	 * @param throttle velocity throttle to put in the log file
	 */
	public void logData(double throttle) {
		maxError = Math.max(talon.canTalon.getClosedLoopError(), maxError);
		SmartDashboard.putNumber("max error", maxError);
		SmartDashboard.putNumber("speed", talon.canTalon.getPulseWidthVelocity());

		try (FileWriter fw = new FileWriter(logFilename, true)) {
			StringBuilder sb = new StringBuilder();
			sb.append((Robot.currentTimeMillis() - startTime) / 1000);
			sb.append(",");
			sb.append(talon.getSpeed());
			sb.append(",");
			SmartDashboard.putNumber("talon", talon.getSpeed());
			sb.append(throttle);
			sb.append("\n");
			SmartDashboard.putNumber("Throttle", this.throttle);
			SmartDashboard.putNumber("Setpoint", talon.canTalon.getSetpoint());
			SmartDashboard.putNumber("Error", talon.canTalon.getError());
			SmartDashboard.putNumber("F", talon.canTalon.getF());
			SmartDashboard.putNumber("voltage", talon.canTalon.getOutputVoltage());
			fw.write(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Init the log file on enabling
	 */
	@Override
	protected void initDefaultCommand() {
		try (PrintWriter writer = new PrintWriter(logFilename)) {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		startTime = Robot.currentTimeMillis();
		//		setDefaultCommand(new PIDTune(this));
		Logger.addEvent("Finished init default command", this.getClass());
	}

	@Override
	public String getHeader() {
		return "speed,setpoint,error,voltage,current";
	}

	@Override
	public Object[] getData() {
		return new Object[]{talon.getSpeed(),talon.getSetpoint(),talon.getError(),talon.canTalon.getOutputVoltage(),talon.canTalon.getOutputCurrent()};
	}

	@Override
	public String getName(){
		return "shooter";
	}
}
