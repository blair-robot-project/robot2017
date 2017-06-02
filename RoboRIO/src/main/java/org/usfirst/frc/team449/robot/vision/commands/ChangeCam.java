package org.usfirst.frc.team449.robot.vision.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.util.Logger;
import org.usfirst.frc.team449.robot.vision.CameraSubsystem;

/**
 * Toggles camera on button press.
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.StringIdGenerator.class)
public class ChangeCam extends ReferencingCommand {

	/**
	 * The cameraSubsystem to execute this command on
	 */
	private CameraSubsystem cameraSubsystem;

	/**
	 * Default constructor.
	 *
	 * @param cameraSubsystem The cameraSubsystem to execute this command on.
	 */
	public ChangeCam(CameraSubsystem cameraSubsystem) {
		super(cameraSubsystem);
		requires(cameraSubsystem);
		this.cameraSubsystem = cameraSubsystem;
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		Logger.addEvent("ChangeCam init", this.getClass());
	}

	/**
	 * Switch the MjpegServer to use the next camera in the list
	 */
	@Override
	protected void execute() {
		//Switches camNum to next camera, if applicable
		if (cameraSubsystem.cameras.size() == 1) {
			Logger.addEvent("You're trying to switch cameras, but your robot only has one camera!", this.getClass());
		} else {
			cameraSubsystem.camNum = (cameraSubsystem.camNum + 1) % cameraSubsystem.cameras.size();
		}

		//Switches to set camera
		cameraSubsystem.server.setSource(cameraSubsystem.cameras.get(cameraSubsystem.camNum));
	}

	/**
	 * Finish immediately because this is a state-change command.
	 *
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
		Logger.addEvent("ChangeCam end", this.getClass());
	}

	/**
	 * Log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("ChangeCam interrupted!", this.getClass());
	}
}
