package org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.UnidirectionalDrive;
import org.usfirst.frc.team449.robot.util.Logger;
import org.usfirst.frc.team449.robot.util.YamlCommandWrapper;
import org.usfirst.frc.team449.robot.util.YamlSubsystem;

/**
 * Created by noah on 6/24/17.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class DetermineNominalVoltage<T extends YamlSubsystem & UnidirectionalDrive> extends YamlCommandWrapper {

	/**
	 * The drive subsystem to execute this command on.
	 */
	@NotNull
	private final T subsystem;

	private double percentVoltage;

	/**
	 * Default constructor
	 *
	 * @param drive The drive to execute this command on
	 */
	@JsonCreator
	public DetermineNominalVoltage(@NotNull @JsonProperty(required = true) T drive) {
		//Initialize stuff
		this.subsystem = drive;
		requires(drive);
		Logger.addEvent("Drive Robot bueno", this.getClass());
	}

	/**
	 * Set up start time.
	 */
	@Override
	protected void initialize() {
		percentVoltage = 0;
		//Reset drive velocity (for safety reasons)
		subsystem.fullStop();
		Logger.addEvent("DetermineNominalVoltage init", this.getClass());
	}

	/**
	 * Send output to motors and log data
	 */
	@Override
	protected void execute() {
		//Adjust it by 0.01 per second, so 0.01 * 20/1000, which is 0.0002
		percentVoltage += 0.0002;
		//Set the velocity
		subsystem.setOutput(percentVoltage, percentVoltage);
	}

	/**
	 * Exit after the command's been running for long enough
	 *
	 * @return True if timeout has been reached, false otherwise
	 */
	@Override
	protected boolean isFinished() {
		return Math.max(subsystem.getLeftVel(), subsystem.getRightVel()) >= 0.5;
	}

	/**
	 * Stop the drive when the command ends.
	 */
	@Override
	protected void end() {
		//Brake on exit. Yes this should be setOutput because often we'll be testing how well the PID loop handles a full stop.
		subsystem.fullStop();
		System.out.println("it moved!");
		Logger.addEvent("DetermineNominalVoltage end.", this.getClass());
	}

	/**
	 * Log and stop the drive when the command is interrupted.
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("DetermineNominalVoltage Interrupted! Stopping the robot.", this.getClass());
		//Brake if we're interrupted
		subsystem.fullStop();
	}
}
