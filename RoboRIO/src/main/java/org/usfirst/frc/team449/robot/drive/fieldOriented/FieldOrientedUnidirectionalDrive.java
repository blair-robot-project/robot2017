package org.usfirst.frc.team449.robot.drive.fieldOriented;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.drive.unidirectional.DriveUnidirectional;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlCommandWrapper;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.subsystem.interfaces.navX.SubsystemNavX;

/**
 * A default command for driving a unidirectional drive using a field-oriented OI.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class FieldOrientedUnidirectionalDrive <T extends YamlSubsystem & DriveUnidirectional & SubsystemNavX> extends YamlCommandWrapper {

	/**
	 * The subsystem to execute this command on.
	 */
	@NotNull
	private final T subsystem;

	/**
	 * Default constructor
	 *
	 * @param subsystem The subsystem to execute this command on
	 */
	@JsonCreator
	public FieldOrientedUnidirectionalDrive(@NotNull @JsonProperty(required = true) T subsystem) {
		this.subsystem = subsystem;
	}

	/**
	 *
	 */
	@Override
	protected void initialize() {
		Logger.addEvent("FieldOrientedUnidirectionalDrive init.", this.getClass());
	}

	/**
	 *
	 */
	@Override
	protected void execute() {
	}

	/**
	 * @return
	 */
	@Override
	protected boolean isFinished() {
		//This does NOT have to be true.
		return true;
	}

	/**
	 *
	 */
	@Override
	protected void end() {
		Logger.addEvent("FieldOrientedUnidirectionalDrive end.", this.getClass());
	}

	/**
	 *
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("FieldOrientedUnidirectionalDrive Interrupted!", this.getClass());
	}
}