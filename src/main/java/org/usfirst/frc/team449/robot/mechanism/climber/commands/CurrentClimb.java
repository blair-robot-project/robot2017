package org.usfirst.frc.team449.robot.mechanism.climber.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.climber.ClimberSubsystem;

/**
 * Climb the rope and stop when the current limit is exceeded.
 */
public class CurrentClimb extends ReferencingCommand {

	/**
	 * The climber this is controlling
	 */
	private ClimberSubsystem climber;

	public CurrentClimb(ClimberSubsystem climber) {
		super(climber);
		requires(climber);
		this.climber = climber;
		System.out.println("CurrentClimb constructed");
	}

	@Override
	protected void initialize() {
		System.out.println("CurrentClimb init");
	}

	@Override
	protected void execute() {
		//Climb as fast as we can
		climber.setPercentVbus(1);
		SmartDashboard.putNumber("Current", climber.canTalonSRX.canTalon.getOutputCurrent());
	}

	@Override
	protected boolean isFinished() {
		//Stop when the current limit is exceeded.
		return climber.reachedTop();
	}

	@Override
	protected void end() {
		//Stop the motor when we reach the top.
		climber.setPercentVbus(0);
		System.out.println("CurrentClimb end");
	}

	@Override
	protected void interrupted() {
		//Stop climbing if we're for some reason interrupted.
		climber.setPercentVbus(0);
		System.out.println("CurrentClimb interrupted, stopping climb.");
	}

}
