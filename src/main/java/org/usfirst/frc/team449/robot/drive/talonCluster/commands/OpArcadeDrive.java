package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import com.ctre.CANTalon;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.ois.ArcadeOI;

/**
 * Program created by noah on 1/8/17.
 */
public class OpArcadeDrive extends ReferencingCommand {
	public ArcadeOI oi;

	double leftThrottle;
	double rightThrottle;

	public OpArcadeDrive(TalonClusterDrive drive, ArcadeOI oi) {
		super(drive);
		this.oi = oi;
		requires(subsystem);
	}

	@Override
	protected void initialize() {
		((TalonClusterDrive) subsystem).leftMaster.canTalon.changeControlMode(CANTalon.TalonControlMode.Speed);
		((TalonClusterDrive) subsystem).rightMaster.canTalon.changeControlMode(CANTalon.TalonControlMode.Speed);

		((TalonClusterDrive) subsystem).setDefaultThrottle(0.0, 0.0);
	}

	@Override
	protected void execute() {


		rightThrottle = oi.getFwd() + oi.getRot();
		leftThrottle = oi.getFwd() - oi.getRot();

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
		System.out.println("OpTankDrive Interrupted! Stopping the robot.");
		((TalonClusterDrive) subsystem).setDefaultThrottle(0.0, 0.0);
	}
}
