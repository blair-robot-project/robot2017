package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import maps.org.usfirst.frc.team449.robot.util.ToleranceBufferAnglePIDMap;
import org.usfirst.frc.team449.robot.interfaces.drive.shifting.ShiftingDrive;
import org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.UnidirectionalDrive;
import org.usfirst.frc.team449.robot.interfaces.oi.ArcadeOI;

/**
 * Drive with arcade drive setup, autoshift, and when the driver isn't turning, use a NavX to stabilize the robot's
 * alignment.
 */
public class ShiftingUnidirectionalNavXArcadeDrive extends UnidirectionalNavXArcadeDrive {
	/**
	 * Default constructor
	 *
	 * @param map   The angle PID map containing PID and other tuning constants.
	 * @param drive The drive to execute this command on. Must also be a NavXSubsystem and a ShiftingDrive.
	 * @param oi    The OI controlling the robot.
	 */
	public ShiftingUnidirectionalNavXArcadeDrive(ToleranceBufferAnglePIDMap.ToleranceBufferAnglePID map, UnidirectionalDrive drive, ArcadeOI oi) {
		super(map, drive, oi);
	}

	/**
	 * Autoshift, decide whether or not we should be in free drive or straight drive, and log data.
	 */
	@Override
	public void execute() {
		//Auto-shifting
		((ShiftingDrive) driveSubsystem).autoshift();
		super.execute();
	}
}
