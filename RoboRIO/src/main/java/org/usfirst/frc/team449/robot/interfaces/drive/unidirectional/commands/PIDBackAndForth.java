package org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.UnidirectionalDrive;

/**
 * Drive back and forth to tune PID.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class PIDBackAndForth extends CommandGroup {
	/**
	 * Instantiate the CommandGroup
	 *
	 * @param subsystem  the unidirectional drive to execute this command on.
	 * @param speed      The speed to drive forwards and backwards at, from [0, 1].
	 * @param timeInSecs How long to drive in each direction for, in seconds.
	 */
	@JsonCreator
	public <T extends Subsystem & UnidirectionalDrive> PIDBackAndForth(@JsonProperty(required = true) T subsystem,
	                                                                   @JsonProperty(required = true) double speed,
	                                                                   @JsonProperty(required = true) double timeInSecs) {
		//Drive forwards
		addSequential(new DriveAtSpeed(subsystem, speed, timeInSecs));
		//Drive backwards
		addSequential(new DriveAtSpeed(subsystem, -speed, timeInSecs));
	}
}
