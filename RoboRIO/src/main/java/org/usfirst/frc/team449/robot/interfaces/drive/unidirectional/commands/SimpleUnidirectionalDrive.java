package org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.usfirst.frc.team449.robot.util.YamlCommandWrapper;
import org.usfirst.frc.team449.robot.util.YamlSubsystem;
import org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.UnidirectionalDrive;
import org.usfirst.frc.team449.robot.interfaces.oi.UnidirectionalOI;
import org.usfirst.frc.team449.robot.util.Logger;

/**
 * Very simple unidirectional drive control.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SimpleUnidirectionalDrive <T extends YamlSubsystem & UnidirectionalDrive> extends YamlCommandWrapper {

	/**
	 * The OI used for input.
	 */
	public UnidirectionalOI oi;

	/**
	 * The drive subsystem to execute this command on.
	 */
	private UnidirectionalDrive subsystem;

	/**
	 * Default constructor
	 *
	 * @param drive The drive to execute this command on
	 * @param oi    The OI that gives the input to this command.
	 */
	@JsonCreator
	public SimpleUnidirectionalDrive(@JsonProperty(required = true) T drive,
	                                                                             @JsonProperty(required = true) UnidirectionalOI oi) {
		this.oi = oi;
		this.subsystem = drive;
		//Default commands need to require their subsystems.
		requires(drive);
	}

	/**
	 * Stop the drive for safety reasons.
	 */
	@Override
	protected void initialize() {
		subsystem.fullStop();
	}

	/**
	 * Give output to the motors based on the stick inputs.
	 */
	@Override
	protected void execute() {
		subsystem.setOutput(oi.getLeftOutput(), oi.getRightOutput());
	}

	/**
	 * Run constantly because this is a default drive
	 *
	 * @return false
	 */
	@Override
	protected boolean isFinished() {
		return false;
	}

	/**
	 * Do nothing, never gets called because this command never finishes.
	 */
	@Override
	protected void end() {

	}

	/**
	 * Log and brake when interrupted.
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("SimpleUnidirectionalDrive Interrupted! Stopping the robot.", this.getClass());
		//Brake for safety!
		subsystem.fullStop();
	}
}
