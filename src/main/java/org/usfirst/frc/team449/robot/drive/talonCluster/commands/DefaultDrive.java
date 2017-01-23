package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.Talon;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;
import org.usfirst.frc.team449.robot.oi.OISubsystem;

/**
 * Program created by noah on 1/8/17.
 */
public class DefaultDrive extends ReferencingCommand {
	public OISubsystem oi;

	double leftThrottle;
	double rightThrottle;

	public DefaultDrive(TalonClusterDrive drive, OISubsystem oi) {
		super(drive);
		this.oi = oi;
		requires(subsystem);
		System.out.println("Drive Robot bueno");
	}

	@Override
	protected void initialize() {
		((TalonClusterDrive) subsystem).leftMaster.canTalon.changeControlMode(CANTalon.TalonControlMode.Speed);
		((TalonClusterDrive) subsystem).rightMaster.canTalon.changeControlMode(CANTalon.TalonControlMode.Speed);

		((TalonClusterDrive) subsystem).setDefaultThrottle(0.0, 0.0);
	}

	@Override
	protected void execute() {
		rightThrottle = oi.getDriveAxisRight();
		leftThrottle = oi.getDriveAxisLeft();
		((TalonClusterDrive) subsystem).logData();
		((TalonClusterDrive) subsystem).setDefaultThrottle(leftThrottle, rightThrottle);
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
		System.out.println("DefaultDrive Interrupted! Stopping the robot.");
		((TalonClusterDrive) subsystem).setDefaultThrottle(0.0, 0.0);
	}
}
