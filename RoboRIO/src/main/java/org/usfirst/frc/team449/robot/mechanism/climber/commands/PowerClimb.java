package org.usfirst.frc.team449.robot.mechanism.climber.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.Robot;
import org.usfirst.frc.team449.robot.mechanism.climber.ClimberSubsystem;
import org.usfirst.frc.team449.robot.util.Logger;

/**
 * Climb the rope and stop when the power limit is exceeded.
 */
public class PowerClimb extends ReferencingCommand {

	/**
	 * The climber to execute this command on
	 */
	private ClimberSubsystem climber;

	private long startTime;

	//TODO externalize
	private long spinupTime = 250;

	/**
	 * Default constructor
	 *
	 * @param climber The climber subsystem to execute this command on
	 */
	public PowerClimb(ClimberSubsystem climber) {
		super(climber);
		requires(climber);
		this.climber = climber;
		Logger.addEvent("PowerClimb constructed", this.getClass());
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		Logger.addEvent("PowerClimb init", this.getClass());
		startTime = Robot.currentTimeMillis();
	}

	/**
	 * Climb at full speed and log the power
	 */
	@Override
	protected void execute() {
		//Climb as fast as we can
		climber.setPercentVbus(1);
		//Log power to SmartDashboard
		SmartDashboard.putNumber("Power", climber.canTalonSRX.getPower());
	}

	/**
	 * Stop when the power limit is exceeded.
	 *
	 * @return true when the power limit is exceed, false otherwise.
	 */
	@Override
	protected boolean isFinished() {
		return climber.reachedTop() && Robot.currentTimeMillis() - startTime > spinupTime;
	}

	/**
	 * Stop the motor and log that the command has ended.
	 */
	@Override
	protected void end() {
		//Stop the motor when we reach the top.
		climber.setPercentVbus(0);
		Logger.addEvent("PowerClimb end", this.getClass());
	}

	/**
	 * Stop the motor and log that the command has been interrupted.
	 */
	@Override
	protected void interrupted() {
		//Stop climbing if we're for some reason interrupted.
		climber.setPercentVbus(0);
		Logger.addEvent("PowerClimb interrupted, stopping climb.", this.getClass());
	}

}
