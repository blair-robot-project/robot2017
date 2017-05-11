package org.usfirst.frc.team449.robot.mechanism.activegear.commands;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.activegear.ActiveGearSubsystem;

/**
 * Command for raising the intake
 */
public class FirePiston extends ReferencingCommand {
	/**
	 * The active gear subsystem to execute this command on
	 */
	private ActiveGearSubsystem activeGearSubsystem;

	private DoubleSolenoid.Value position;

	/**
	 * Construct a FirePiston command
	 *
	 * @param activeGearSubsystem active gear subsystem to execute this command on
	 * @param position The direction to set the piston to be in
	 */
	public FirePiston(ActiveGearSubsystem activeGearSubsystem, DoubleSolenoid.Value position) {
		super(activeGearSubsystem);
		this.activeGearSubsystem = activeGearSubsystem;
		this.position = position;
		System.out.println("FirePiston constructed");
	}

	@Override
	protected void initialize(){
		System.out.println("FirePiston init");
	}

	/**
	 * Set the piston to be in up position
	 */
	@Override
	protected void execute() {
		activeGearSubsystem.setPiston(position);
	}

	/**
	 * Finish immediately because this is a state-change command.
	 * @return true
	 */
	@Override
	protected boolean isFinished() {
		return true;
	}

	@Override
	protected void end(){
		System.out.println("FirePiston end");
	}
}
