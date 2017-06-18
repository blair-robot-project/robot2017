package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.usfirst.frc.team449.robot.util.YamlSubsystem;
import org.usfirst.frc.team449.robot.components.PID;
import org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.UnidirectionalDrive;
import org.usfirst.frc.team449.robot.interfaces.subsystem.NavX.NavxSubsystem;
import org.usfirst.frc.team449.robot.util.YamlCommandGroupWrapper;

/**
 * Rotates the robot back and forth in order to dislodge any stuck balls.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class JiggleRobot extends YamlCommandGroupWrapper {
	/**
	 * Instantiate the CommandGroup
	 *
	 * @param PID                      The PID gains for this loop.
	 * @param toleranceBuffer          How many consecutive loops have to be run while within tolerance to be considered
	 *                                 on target. Multiply by loop period of ~20 milliseconds for time. Defaults to 0.
	 * @param absoluteTolerance        The maximum number of degrees off from the target at which we can be considered
	 *                                 within tolerance.
	 * @param minimumOutput            The minimum output of the loop. Defaults to zero.
	 * @param maximumOutput            The maximum output of the loop. Can be null, and if it is, no maximum output is
	 *                                 used.
	 * @param deadband                 The deadband around the setpoint, in degrees, within which no output is given to
	 *                                 the motors. Defaults to zero.
	 * @param maxAngularVelToEnterLoop The maximum angular velocity, in degrees/sec, at which the loop will be entered.
	 *                                 Defaults to 180.
	 * @param inverted                 Whether the loop is inverted. Defaults to false.
	 * @param loopEntryDelay           The delay to enter the loop after conditions for entry are met. Defaults to
	 *                                 zero.
	 * @param subsystem The drive to execute this command on.
	 */
	@JsonCreator
	public <T extends YamlSubsystem & UnidirectionalDrive & NavxSubsystem> JiggleRobot(@JsonProperty(required = true) PID PID,
	                                                                               @JsonProperty(required = true) double absoluteTolerance,
	                                                                               int toleranceBuffer,
	                                                                               double minimumOutput, Double maximumOutput,
	                                                                               double deadband,
	                                                                               Double maxAngularVelToEnterLoop,
	                                                                               boolean inverted,
	                                                                               double loopEntryDelay,
	                                                                               @JsonProperty(required = true) T subsystem) {
		addSequential(new NavXRelativeTTA(PID, absoluteTolerance, toleranceBuffer, minimumOutput, maximumOutput, deadband, inverted, 10, subsystem, 3));
		addSequential(new NavXRelativeTTA(PID, absoluteTolerance, toleranceBuffer, minimumOutput, maximumOutput, deadband, inverted, -10, subsystem, 3));
	}
}
