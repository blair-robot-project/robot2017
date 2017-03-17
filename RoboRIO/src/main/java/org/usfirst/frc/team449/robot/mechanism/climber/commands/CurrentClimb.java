package org.usfirst.frc.team449.robot.mechanism.climber.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.climber.ClimberSubsystem;

/**
 * Climb the rope and stop when the current limit is exceeded.
 */
public class CurrentClimb extends ReferencingCommand {

	/**
	 * The climber to execute this command on
	 */
	private ClimberSubsystem climber;

	/**
	 * Default constructor
	 * @param climber The climber subsystem to execute this command on
	 */
	public CurrentClimb(ClimberSubsystem climber) {
		super(climber);
		requires(climber);
		this.climber = climber;
		System.out.println("CurrentClimb constructed");
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		System.out.println("CurrentClimb init");
	}

	/**
	 * Climb at full speed and log the current
	 */
	@Override
	protected void execute() {
		//Climb as fast as we can
		climber.setPercentVbus(1);
		//Log current to SmartDashboard
		SmartDashboard.putNumber("Current", climber.canTalonSRX.canTalon.getOutputCurrent());
	}

	/**
	 * Stop when the current limit is exceeded.
	 * @return true when the current limit is exceed, false otherwise.
	 */
	@Override
	protected boolean isFinished() {
		return climber.reachedTop();
	}

	/**
	 * Stop the motor and log that the command has ended.
	 */
	@Override
	protected void end() {
		//Stop the motor when we reach the top.
		climber.setPercentVbus(0);
		System.out.println("CurrentClimb end");
	}

	/**
	 * Stop the motor and log that the command has been interrupted.
	 */
	@Override
	protected void interrupted() {
		//Stop climbing if we're for some reason interrupted.
		climber.setPercentVbus(0);
		System.out.println("CurrentClimb interrupted, stopping climb.");
	}

}
