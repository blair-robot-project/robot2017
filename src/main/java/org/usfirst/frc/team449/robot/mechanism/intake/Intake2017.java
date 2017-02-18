package org.usfirst.frc.team449.robot.mechanism.intake;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.VictorSP;
import org.usfirst.frc.team449.robot.MappedSubsystem;

/**
 * Created by Justin on 1/28/2017.
 */
public class Intake2017 extends MappedSubsystem {

	private VictorSP fixedVictor;
	private VictorSP actuatedVictor;
	private DoubleSolenoid piston;
	public boolean isIntaking;
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
		this.piston = new DoubleSolenoid(map.getPiston().getForward(), map.getPiston().getReverse());
	}

	public void setFixedVictor(double speed) {
		fixedVictor.set(speed);
	}

	public void setActuatedVictor(double speed){
		actuatedVictor.set(speed);
	}

	public void setPiston(DoubleSolenoid.Value value) {
		piston.set(value);
	}

	public void setIntaking(boolean isIntaking){
		this.isIntaking = isIntaking;
	}

	public void setIntakeUp(boolean intakeUp) {
		this.intakeUp = intakeUp;
	}

	/**
	 * Initialize the default command for a subsystem By default subsystems have
	 * no default command, but if they do, the default command is set with this
	 * method. It is called on all Subsystems by CommandBase in the users program
	 * after all the Subsystems are created.
	 */
	@Override
	protected void initDefaultCommand() {
		//Do nothing!
	}
}
