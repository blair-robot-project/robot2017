package org.usfirst.frc.team449.robot.interfaces.subsystem.Intake;

/**
 * Created by noah on 5/20/17.
 */
public interface IntakeSubsystem {
	/**
	 * Set the speed of the intake to one of 5 IntakeModes.
	 * @param mode off, in slow, in fast, out slow, out fast.
	 */
	void setMode(IntakeMode mode);

	/**
	 * Get the mode of the intake
	 * @return off, in slow, in fast, out slow, out fast.
	 */
	IntakeMode getMode();

	enum IntakeMode {
		OFF,IN_SLOW,IN_FAST,OUT_SLOW,OUT_FAST
	}
}
