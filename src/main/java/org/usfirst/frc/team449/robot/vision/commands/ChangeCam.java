package org.usfirst.frc.team449.robot.vision.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.vision.CameraSubsystem;

/**
 * Toggles camera on button press.
 */
public class ChangeCam extends ReferencingCommand {

	//Initializes CameraSubsystem
	private CameraSubsystem cameraSubsystem;

	//Default constructor
	public ChangeCam(CameraSubsystem cameraSubsystem, double timeout) {
		super(cameraSubsystem, timeout);
		requires(cameraSubsystem);
		this.cameraSubsystem = cameraSubsystem;
	}

	@Override
	protected void initialize() {
		System.out.println("ChangeCam init");
		//Does nothing - Logging to console
	}

	@Override
	protected void execute() {
		//Logging to console
		System.out.println("ChangeCam exec start");

		//Switches camNum to next camera, if applicable
		if (cameraSubsystem.cameras.size() == 1) {
			System.out.println("You're trying to switch cameras, but your robot only has one camera!");
		} else {
			cameraSubsystem.camNum = (cameraSubsystem.camNum + 1) % cameraSubsystem.cameras.size();
		}

		//Switches to set camera
		cameraSubsystem.server.setSource(cameraSubsystem.cameras.get(cameraSubsystem.camNum));

		//Logging to camera
		System.out.println("ChangeCam exec end");
	}

	@Override
	protected boolean isFinished() {
		//Finishes instantaneously.
		return true;
	}

	@Override
	protected void end() {
		System.out.println("ChangeCam end");
	}

	@Override
	protected void interrupted() {
		System.out.println("ChangeCam interrupted!");
	}
}
