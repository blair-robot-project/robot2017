package org.usfirst.frc.team449.robot.vision.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.vision.CameraSubsystem;

/**
 * Toggles camera on button press.
 */
public class ChangeCam extends ReferencingCommand {

	/**
	 * The cameraSubsystem to execute this command on
	 */
	private CameraSubsystem cameraSubsystem;

	//TODO get rid of timeout.
	/**
	 * Default constructor.
	 * @param cameraSubsystem The cameraSubsystem to execute this command on.
	 * @param timeout The timeout for this command (does nothing)
	 */
	public ChangeCam(CameraSubsystem cameraSubsystem, double timeout) {
		super(cameraSubsystem, timeout);
		requires(cameraSubsystem);
		this.cameraSubsystem = cameraSubsystem;
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		System.out.println("ChangeCam init");
	}

	/**
	 * Switch the MjpegServer to use the next camera in the list
	 */
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

	/**
	 * Finish immediately because this is a state-change command.
	 * @return true
	 */
	@Override
	protected boolean isFinished() {
		return true;
	}

	/**
	 * Log when this command ends
	 */
	@Override
	protected void end() {
		System.out.println("ChangeCam end");
	}

	/**
	 * Log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		System.out.println("ChangeCam interrupted!");
	}
}
