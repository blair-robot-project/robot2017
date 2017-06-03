package org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team449.robot.Robot;
import org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.UnidirectionalDrive;
import org.usfirst.frc.team449.robot.util.Logger;

/**
 * Go at a certain speed for a set number of seconds
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.StringIdGenerator.class)
public class DriveAtSpeed extends Command {

	/**
	 * Speed to go at
	 */
	private double speed;

	/**
	 * How long to run for
	 */
	private double seconds;

	/**
	 * When this command was initialized.
	 */
	private long startTime;

	/**
	 * The drive subsystem to execute this command on.
	 */
	private UnidirectionalDrive subsystem;

	/**
	 * Default constructor
	 *
	 * @param drive   The drive to execute this command on
	 * @param speed   How fast to go, in RPS
	 * @param seconds How long to drive for.
	 */
	@JsonCreator
	public DriveAtSpeed(@JsonProperty(required = true) UnidirectionalDrive drive,
	                    @JsonProperty(required = true) double speed,
	                    @JsonProperty(required = true) double seconds) {
		//Initialize stuff
		this.subsystem = drive;
		this.speed = speed;
		this.seconds = seconds;
		Logger.addEvent("Drive Robot bueno", this.getClass());
	}

	/**
	 * Set up start time.
	 */
	@Override
	protected void initialize() {
		//Set up start time
		startTime = Robot.currentTimeMillis();
		//Reset drive speed (for safety reasons)
		subsystem.fullStop();
		Logger.addEvent("DriveAtSpeed init", this.getClass());
	}

	/**
	 * Send output to motors and log data
	 */
	@Override
	protected void execute() {
		//Set the speed
		subsystem.setOutput(speed, speed);
	}

	/**
	 * Exit after the command's been running for long enough
	 *
	 * @return True if timeout has been reached, false otherwise
	 */
	@Override
	protected boolean isFinished() {
		return (Robot.currentTimeMillis() - startTime) * 1e-3 > seconds;
	}

	/**
	 * Stop the drive when the command ends.
	 */
	@Override
	protected void end() {
		//Brake on exit. Yes this should be setOutput because often we'll be testing how well the PID loop handles a full stop.
		subsystem.setOutput(0, 0);
		Logger.addEvent("DriveAtSpeed end.", this.getClass());
	}

	/**
	 * Log and stop the drive when the command is interrupted.
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("DriveAtSpeed Interrupted! Stopping the robot.", this.getClass());
		//Brake if we're interrupted
		subsystem.fullStop();
	}
}
