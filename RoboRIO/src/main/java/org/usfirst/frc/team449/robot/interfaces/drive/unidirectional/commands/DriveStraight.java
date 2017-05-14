package org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.UnidirectionalDrive;
import org.usfirst.frc.team449.robot.interfaces.oi.TankOI;

/**
 * Drives straight when using a tank drive. Not updated for new OI organization.
 */
public class DriveStraight extends Command {
	private TankOI oi;

	private boolean useLeft;

	private double throttle;

	private UnidirectionalDrive subsystem;

	public DriveStraight(UnidirectionalDrive drive, TankOI oi, boolean useLeft) {
		subsystem = drive;
		this.oi = oi;
		this.useLeft = useLeft;
		requires((Subsystem) subsystem);
		System.out.println("Drive Robot bueno");
	}

	@Override
	protected void initialize() {
		subsystem.fullStop();
	}

	@Override
	protected void execute() {
		if(useLeft){
			throttle = oi.getLeftThrottle();
		} else {
			throttle = oi.getRightThrottle();
		}
		subsystem.setOutput(throttle, throttle);
	}

	@Override
	protected boolean isFinished() {
		return false;
	}

	@Override
	protected void end() {
	}

	@Override
	protected void interrupted() {
		System.out.println("DriveStraight Interrupted! Stopping the robot.");
		subsystem.fullStop();
	}
}
