package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.usfirst.frc.team449.robot.components.ToleranceBufferAnglePID;
import org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.UnidirectionalDrive;
import org.usfirst.frc.team449.robot.interfaces.subsystem.NavX.NavxSubsystem;

/**
 * Rotates the robot back and forth in order to dislodge any stuck balls.
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.StringIdGenerator.class)
public class JiggleRobot extends CommandGroup {
	/**
	 * Instantiate the CommandGroup
	 *
	 * @param subsystem The drive to execute this command on.
	 * @param turnPID The angular PID loop to turn with.
	 */
	@JsonCreator
	public <T extends Subsystem & UnidirectionalDrive & NavxSubsystem> JiggleRobot(@JsonProperty(required = true) T subsystem,
	                                                                              @JsonProperty(required = true) ToleranceBufferAnglePID turnPID) {
		addSequential(new NavXRelativeTTA(turnPID, 10, subsystem, 3));
		addSequential(new NavXRelativeTTA(turnPID, -10, subsystem, 3));
	}
}
