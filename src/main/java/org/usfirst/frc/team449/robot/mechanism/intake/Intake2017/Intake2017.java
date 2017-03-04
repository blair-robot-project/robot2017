package org.usfirst.frc.team449.robot.mechanism.intake.Intake2017;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.VictorSP;
import org.usfirst.frc.team449.robot.MappedSubsystem;

/**
 * The subsystem that picks up balls from the ground.
 */
public class Intake2017 extends MappedSubsystem {
	/**
	 * VictorSP for the static intake
	 */
	private VictorSP fixedVictor;
	/**
	 * VictorSP for the dynamic intake
	 */
	private VictorSP actuatedVictor;
	/**
	 * Piston for raising and lowering the intake
	 */
	private DoubleSolenoid piston;
	/**
	 * Whether this is currently intaking
	 */
	public boolean isIntaking;

	//TODO make an enum
	/**
	 * Whether intake is currently up
	 */
	public boolean intakeUp;

	/**
	 * Creates a mapped subsystem and sets its map
	 *
	 * @param map the map of constants relevant to this subsystem
	 */
	public Intake2017(maps.org.usfirst.frc.team449.robot.mechanism.intake.Intake2017Map.Intake2017 map) {
		super(map.getMechanism());
		this.map = map;
		this.fixedVictor = new VictorSP(map.getFixedVictor().getPort());
		fixedVictor.setInverted(map.getFixedVictor().getInverted());
		this.actuatedVictor = new VictorSP(map.getActuatedVictor().getPort());
		actuatedVictor.setInverted(map.getActuatedVictor().getInverted());
		this.piston = new DoubleSolenoid(map.getPistonModuleNum(), map.getPiston().getForward(), map.getPiston().getReverse());
	}

	/**
	 * Set the percentage speed of the static intake
	 *
	 * @param speed PWM setpoint [-1, 1]
	 */
	public void setFixedVictor(double speed) {
		fixedVictor.set(speed);
	}

	/**
	 * Set the percentage speed of the dynamic intake
	 *
	 * @param speed PWM setpoint [-1, 1]
	 */
	public void setActuatedVictor(double speed) {
		actuatedVictor.set(speed);
	}

	/**
	 * Fire the piston
	 *
	 * @param value direction to fire
	 */
	public void setPiston(DoubleSolenoid.Value value) {
		piston.set(value);
		System.out.println("Set Piston");
		intakeUp = (value == DoubleSolenoid.Value.kReverse);
	}

	/**
	 * Set isIntaking status
	 *
	 * @param isIntaking whether currently is intaking
	 */
	public void setIntaking(boolean isIntaking) {
		this.isIntaking = isIntaking;
	}

	/**
	 * Initialize the default command for a subsystem. By default subsystems have
	 * no default command, but if they do, the default command is set with this
	 * method. It is called on all Subsystems by CommandBase in the users program
	 * after all the Subsystems are created.
	 */
	@Override
	protected void initDefaultCommand() {
		//Do nothing!
	}
}
