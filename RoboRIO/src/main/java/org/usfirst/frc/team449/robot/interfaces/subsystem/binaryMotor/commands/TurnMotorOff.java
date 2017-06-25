package org.usfirst.frc.team449.robot.interfaces.subsystem.binaryMotor.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.interfaces.subsystem.binaryMotor.BinaryMotorSubsystem;
import org.usfirst.frc.team449.robot.util.Logger;
import org.usfirst.frc.team449.robot.util.YamlCommandWrapper;

/**
 * Turns off the motor of the specified subsystem.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class TurnMotorOff extends YamlCommandWrapper {

	/**
	 * The subsystem to execute this command on.
	 */
	@NotNull
	protected final BinaryMotorSubsystem subsystem;

	/**
	 * Default constructor
	 *
	 * @param subsystem The subsystem to execute this command on.
	 */
	@JsonCreator
	public TurnMotorOff(@NotNull @JsonProperty(required = true) BinaryMotorSubsystem subsystem) {
		this.subsystem = subsystem;
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		Logger.addEvent("TurnMotorOff init.", this.getClass());
	}

	/**
	 * Turn the motor off.
	 */
	@Override
	protected void execute() {
		subsystem.turnMotorOff();
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
		Logger.addEvent("TurnMotorOff end.", this.getClass());
	}

	/**
	 * Log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("TurnMotorOff Interrupted!", this.getClass());
	}
}