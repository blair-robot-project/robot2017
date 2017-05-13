package org.usfirst.frc.team449.robot.mechanism.intake.Intake2017;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.VictorSP;
import org.usfirst.frc.team449.robot.MappedSubsystem;
import org.usfirst.frc.team449.robot.components.MappedVictor;
import org.usfirst.frc.team449.robot.interfaces.subsystem.solenoid.SolenoidSubsystem;

/**
 * The subsystem that picks up balls from the ground.
 */
public class Intake2017 extends MappedSubsystem implements SolenoidSubsystem{
	/**
	 * Whether this is currently intaking
	 */
	public boolean isIntaking;
	/**
	 * Whether intake is currently up
	 */
	public boolean intakeUp;
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

	private double fixedIntakeSpeed;

	private double fixedAgitateSpeed;

	private double actuatedSpeed;

	/**
	 * Creates a mapped subsystem and sets its map
	 *
	 * @param map the map of constants relevant to this subsystem
	 */
	public Intake2017(maps.org.usfirst.frc.team449.robot.mechanism.intake.Intake2017Map.Intake2017 map) {
		super(map.getMechanism());
		this.map = map;
		this.fixedVictor = new MappedVictor(map.getFixedVictor());
		if (map.hasActuatedVictor()) {
			this.actuatedVictor = new MappedVictor(map.getActuatedVictor());
		}
		if (map.hasPiston()) {
			this.piston = new DoubleSolenoid(map.getPistonModuleNum(), map.getPiston().getForward(), map.getPiston().getReverse());
		}
		fixedAgitateSpeed = map.getFixedAgitateSpeed();
		fixedIntakeSpeed = map.getFixedIntakeSpeed();
		actuatedSpeed = map.getActuatedSpeed();
	}

	/**
	 * Set the speed of the static intake
	 *
	 * @param mode the mode to set the victor to.
	 */
	public void setFixedVictor(FixedIntakeMode mode) {
		if (mode == FixedIntakeMode.OFF){
			fixedVictor.set(0);
		} else if (mode == FixedIntakeMode.AGITATING){
			fixedVictor.set(fixedAgitateSpeed);
		} else {
			fixedVictor.set(fixedIntakeSpeed);
		}
	}

	/**
	 * Set whether the actuated intake is on
	 *
	 * @param on set the motor to the speed in the config if true, 0 otherwise.
	 */
	public void setActuatedVictor(boolean on) {
		if (actuatedVictor != null) {
			if (on){
				actuatedVictor.set(actuatedSpeed);
			} else {
				actuatedVictor.set(0);
			}
		}
	}

	/**
	 * Fire the piston
	 *
	 * @param value direction to fire
	 */
	public void setSolenoid(DoubleSolenoid.Value value) {
		if (piston != null) {
			piston.set(value);
			System.out.println("Set Piston");
			intakeUp = (value == DoubleSolenoid.Value.kReverse);
		}
	}

	public DoubleSolenoid.Value getSolenoidPosition(){
		return intakeUp ? DoubleSolenoid.Value.kReverse : DoubleSolenoid.Value.kForward;
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

	public enum FixedIntakeMode{
		INTAKING, AGITATING, OFF
	}
}
