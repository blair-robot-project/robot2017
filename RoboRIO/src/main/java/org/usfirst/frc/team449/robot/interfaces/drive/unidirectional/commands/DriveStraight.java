package org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.UnidirectionalDrive;
import org.usfirst.frc.team449.robot.interfaces.oi.TankOI;
import org.usfirst.frc.team449.robot.util.Logger;

/**
 * Drives straight when using a tank drive.
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.StringIdGenerator.class)
public class DriveStraight extends Command {

	/**
	 * The oi that this command gets input from.
	 */
	private TankOI oi;

	/**
	 * Whether to use the left or right joystick for the forward velocity.
	 */
	private boolean useLeft;

	/**
	 * The throttle gotten from the joystick. This is a field instead of a local variable to avoid garbage collection.
	 */
	private double throttle;

	/**
	 * The drive subsystem to execute this command on.
	 */
	private UnidirectionalDrive subsystem;

	/**
	 * Drive straight without NavX stabilization.
	 *
	 * @param drive   The drive subsystem to execute this command on.
	 * @param oi      The oi to get input from.
	 * @param useLeft true to use the left stick to drive straight, false to use the right.
	 */
	@JsonCreator
	public DriveStraight(@JsonProperty(required = true) UnidirectionalDrive drive,
	                     @JsonProperty(required = true) TankOI oi,
	                     @JsonProperty(required = true) boolean useLeft) {
		subsystem = drive;
		this.oi = oi;
		this.useLeft = useLeft;
		requires((Subsystem) subsystem);
		Logger.addEvent("Drive Robot bueno", this.getClass());
	}

	/**
	 * Stop the drive for safety reasons.
	 */
	@Override
	protected void initialize() {
		subsystem.fullStop();
	}

	/**
	 * Give output to the motors based on the joystick input.
	 */
	@Override
	protected void execute() {
		if (useLeft) {
			throttle = oi.getLeftThrottle();
		} else {
			throttle = oi.getRightThrottle();
		}

		subsystem.setOutput(throttle, throttle);
	}

	/**
	 * Runs constantly because this is a drive command.
	 *
	 * @return false
	 */
	@Override
	protected boolean isFinished() {
		return false;
	}

	/**
	 * Do nothing, this never gets called because this command never finishes.
	 */
	@Override
	protected void end() {

	}

	/**
	 * Log and brake when interrupted.
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("DriveStraight Interrupted! Stopping the robot.", this.getClass());
		subsystem.fullStop();
	}
}
