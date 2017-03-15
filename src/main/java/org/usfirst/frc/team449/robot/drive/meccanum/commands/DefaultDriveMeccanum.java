package org.usfirst.frc.team449.robot.drive.meccanum.commands;

import com.ctre.CANTalon;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.drive.meccanum.MeccanumDrive;
import org.usfirst.frc.team449.robot.oi.OISubsystem;

/**
 * Created by sam on 1/26/17.
 */
public class DefaultDriveMeccanum extends ReferencingCommand {
	public OISubsystem oi;
	double leftThrottle, rightThrottle;

	public DefaultDriveMeccanum(MeccanumDrive drive, OISubsystem oi) {
		super(drive);
		this.oi = oi;
		requires(subsystem);
	}

	@Override
	protected void initialize() {
		((MeccanumDrive) subsystem).frontLeft.canTalon.changeControlMode(CANTalon.TalonControlMode.Speed);
		((MeccanumDrive) subsystem).frontRight.canTalon.changeControlMode(CANTalon.TalonControlMode.Speed);
		((MeccanumDrive) subsystem).backLeft.canTalon.changeControlMode(CANTalon.TalonControlMode.Speed);
		((MeccanumDrive) subsystem).backRight.canTalon.changeControlMode(CANTalon.TalonControlMode.Speed);
	}

	@Override
	protected void execute() {
		leftThrottle = oi.getDriveAxisLeft();
		rightThrottle = oi.getDriveAxisRight();
		((MeccanumDrive) subsystem).setDefaultThrottle(leftThrottle, rightThrottle);
	}

	@Override
	protected boolean isFinished() {
		return false;
	}
}
