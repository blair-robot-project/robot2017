package org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team449.robot.MappedSubsystem;
import org.usfirst.frc.team449.robot.components.UnitlessCANTalonSRX;
import org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.commands.PIDTune;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by blairrobot on 1/10/17.
 */
public class SingleFlywheelShooter extends MappedSubsystem {

	public boolean spinning;
	private UnitlessCANTalonSRX talon;
	/**
	 * Counts per revolution
	 */

	private long startTime;
	private double maxError = 0;

	public SingleFlywheelShooter(maps.org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.SingleFlywheelShooterMap.SingleFlywheelShooter map) {
		super(map.getMechanism());
		this.map = map;
		this.talon = new UnitlessCANTalonSRX(map.getTalon());
		System.out.println("f" + talon.canTalon.getF());
	}

	/**
	 * Sets the flywheel to go at a speed between 1 and 0, where 1 is max speed.
	 *
	 * @param sp The speed to go at.
	 */
	private void setVBusSpeed(double sp) {
		talon.setPercentVbus(sp);
	}

	private void setPIDSpeed(double sp) {
		talon.setSpeed(talon.getMaxSpeed() * sp * .42);
	}

	/**
	 * A wrapper around the speed method we're currently using/testing
	 *
	 * @param sp The speed to go at, where 0 is off and 1 is max speed.
	 */
	public void setDefaultSpeed(double sp) {
		setPIDSpeed(sp);
	}

	public void logData(double throttle) {
		maxError = Math.max(talon.canTalon.getClosedLoopError(), maxError);
		SmartDashboard.putNumber("max error", maxError);
		SmartDashboard.putNumber("speed", talon.canTalon.getPulseWidthVelocity());

		try (FileWriter fw = new FileWriter("/home/lvuser/driveLog.csv", true)) {
			StringBuilder sb = new StringBuilder();
			sb.append((System.nanoTime() - startTime) / 100);
			sb.append(",");
			sb.append(talon.getSpeed());
			sb.append(",");
			SmartDashboard.putNumber("talon", talon.getSpeed());
			sb.append(throttle);
			sb.append("\n");
			SmartDashboard.putNumber("Throttle", throttle);
			SmartDashboard.putNumber("Setpoint", talon.canTalon.getSetpoint());
			SmartDashboard.putNumber("Error", talon.canTalon.getError());
			SmartDashboard.putNumber("F", talon.canTalon.getF());
			SmartDashboard.putNumber("voltage", talon.canTalon.getOutputVoltage());
			fw.write(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	protected void initDefaultCommand() {
		try (PrintWriter writer = new PrintWriter("/home/lvuser/shooterLog.csv")) {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		startTime = System.nanoTime();
		setDefaultCommand(new PIDTune(this));
		System.out.println("Finished init default command");
	}
}
