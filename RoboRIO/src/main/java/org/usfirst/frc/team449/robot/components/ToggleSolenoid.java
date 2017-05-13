package org.usfirst.frc.team449.robot.components;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Command;

/**
 * A command that toggles the position of a piston.
 */
public class ToggleSolenoid extends Command{

	private SolenoidSubsystem subsystem;

	/**
	 * Default constructor
	 * @param subsystem The solenoid subsystem to execute this command on.
	 */
	public ToggleSolenoid(SolenoidSubsystem subsystem) {
		this.subsystem = subsystem;
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		System.out.println("SolenoidForward init.");
	}

	/**
	 * Do the state change.
	 */
	@Override
	protected void execute() {
		if (subsystem.getSolenoidPosition().equals(DoubleSolenoid.Value.kForward)){
			subsystem.setSolenoid(DoubleSolenoid.Value.kReverse);
		} else {
			subsystem.setSolenoid(DoubleSolenoid.Value.kForward);
		}
	}

	/**
	 * Finish immediately because this is a state-change command.
	 * @return true
	 */
	@Override
	protected boolean isFinished() {
		return true;
	}

	/**
	 * Log when this command ends
	 */
	@Override
	protected void end() {
		System.out.println("SolenoidForward end.");
	}

	/**
	 * Log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		System.out.println("SolenoidForward Interrupted!");
	}
}