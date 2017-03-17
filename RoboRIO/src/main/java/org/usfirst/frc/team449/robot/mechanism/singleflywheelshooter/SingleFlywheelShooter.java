package org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team449.robot.MappedSubsystem;
import org.usfirst.frc.team449.robot.components.RotPerSecCANTalonSRX;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Class for the flywheel
 */
public class SingleFlywheelShooter extends MappedSubsystem {
	/**
	 * The flywheel's Talon
	 */
	private RotPerSecCANTalonSRX talon;

	/**
	 * Whether the flywheel is currently commanded to spin
	 */
	public boolean spinning;

	// TODO externalize
	/**
	 * Throttle at which to run the shooter, defaults to 0.5
	 */
	public double throttle = 0.5;

	/**
	 * Measured start time in nanoseconds (for logging)
	 */
	private long startTime;
	/**
	 * Measured max PID error so far (for testing purposes)
	 */
	private double maxError = 0;

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

		//If we have a throttle in the map, use that instead of 0.5
		if (map.hasThrottle()) {
			this.throttle = map.getThrottle();
		}
		System.out.println("Shooter F: " + talon.canTalon.getF());
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

		try (FileWriter fw = new FileWriter("/home/lvuser/logs/shooterLog.csv", true)) {
			StringBuilder sb = new StringBuilder();
			sb.append((System.nanoTime() - startTime) / 100);
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
		//TODO Externalize filepath.
		try (PrintWriter writer = new PrintWriter("/home/lvuser/logs/shooterLog.csv")) {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		startTime = System.nanoTime();
		//		setDefaultCommand(new PIDTune(this));
		System.out.println("Finished init default command");
	}
}
