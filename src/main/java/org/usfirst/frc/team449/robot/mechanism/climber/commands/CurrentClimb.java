package org.usfirst.frc.team449.robot.mechanism.climber.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.climber.ClimberSubsystem;

/**
 * Created by Justin on 1/12/2017.
 */
public class CurrentClimb extends ReferencingCommand {

	ClimberSubsystem climber;

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
		//if (climber.reachedTop()){
		//	climber.setPercentVbus(0);
		//} else {
			climber.setPercentVbus(1);
		//}
		SmartDashboard.putNumber("Current", climber.canTalonSRX.canTalon.getOutputCurrent());
	}

	@Override
	protected boolean isFinished() {
		return climber.reachedTop();
	}

	@Override
	protected void end() {
		climber.setPercentVbus(0);
		System.out.println("CurrentClimb end");
	}

	@Override
	protected void interrupted() {
		climber.setPercentVbus(0);
		System.out.println("CurrentClimb interrupted, stopping climb.");
	}

}
