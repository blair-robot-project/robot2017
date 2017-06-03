package org.usfirst.frc.team449.robot.mechanism.intake.Intake2017;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.usfirst.frc.team449.robot.components.MappedDoubleSolenoid;
import org.usfirst.frc.team449.robot.components.MappedVictor;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Intake.IntakeSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.solenoid.SolenoidSubsystem;
import org.usfirst.frc.team449.robot.util.Logger;

/**
 * A subsystem that picks up balls from the ground.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class Intake2017 extends Subsystem implements SolenoidSubsystem, IntakeSubsystem {
	/**
	 * Whether intake is currently up
	 */
	private boolean intakeUp;

	/**
	 * VictorSP for the fixed intake
	 */
	private VictorSP fixedVictor;

	/**
	 * VictorSP for the actuated intake
	 */
	private VictorSP actuatedVictor;

	/**
	 * Piston for raising and lowering the intake
	 */
	private DoubleSolenoid piston;

	/**
	 * How fast the fixed victor should go to pick up balls, on [-1, 1]
	 */
	private double fixedIntakeSpeed;

	/**
	 * How fast the fixed victor should go to agitate balls while they're being fed into the shooter, on [-1, 1]
	 */
	private double fixedAgitateSpeed;

	/**
	 * How fast the actuated victor should go to pick up balls, on [-1, 1]
	 */
	private double actuatedSpeed;

	/**
	 * The mode the intake's currently in.
	 */
	private IntakeMode mode;

	/**
	 * Default constructor.
	 *
	 * @param fixedVictor       The VictorSP powering the fixed intake.
	 * @param fixedAgitateSpeed The speed to run the fixed victor at to agitate balls, on [-1, 1]
	 * @param fixedIntakeSpeed  The speed to run the fixed victor to intake balls, on [-1, 1]
	 * @param actuatedVictor    The VictorSP powering the actuated intake. Can be null.
	 * @param actuatedSpeed     The speed to run the actuated victor to intake balls, on [-1, 1]. Defaults to 0.
	 * @param piston            The piston for raising and lowering the actuated intake. Can be null.
	 */
	@JsonCreator
	public Intake2017(@JsonProperty(required = true) MappedVictor fixedVictor,
	                  @JsonProperty(required = true) double fixedAgitateSpeed,
	                  @JsonProperty(required = true) double fixedIntakeSpeed,
	                  MappedVictor actuatedVictor,
	                  double actuatedSpeed,
	                  MappedDoubleSolenoid piston) {
		super();
		//Instantiate stuff.
		this.fixedVictor = fixedVictor;
		this.fixedIntakeSpeed = fixedIntakeSpeed;
		this.fixedAgitateSpeed = fixedAgitateSpeed;
		this.actuatedVictor = actuatedVictor;
		this.actuatedSpeed = actuatedSpeed;
		this.piston = piston;
		mode = IntakeMode.OFF;
	}

	/**
	 * Set the speed of the actuated victor if it exists.
	 *
	 * @param sp speed to set it to, from [-1, 1]
	 */
	private void setActuatedVictor(double sp) {
		if (actuatedVictor != null) {
			actuatedVictor.set(sp);
		}
	}

	/**
	 * Set the speed of the fixed victor.
	 *
	 * @param sp speed to set it to, from [-1, 1]
	 */
	private void setFixedVictor(double sp) {
		fixedVictor.set(sp);
	}

	/**
	 * Set the solenoid to a certain position.
	 *
	 * @param value Forward to extend the Solenoid, Reverse to contract it.
	 */
	public void setSolenoid(DoubleSolenoid.Value value) {
		if (piston != null) {
			piston.set(value);
			intakeUp = (value == DoubleSolenoid.Value.kReverse);
		}
	}

	/**
	 * Get the position of the solenoid.
	 *
	 * @return Forward if extended, Reverse if contracted.
	 */
	public DoubleSolenoid.Value getSolenoidPosition() {
		return intakeUp ? DoubleSolenoid.Value.kReverse : DoubleSolenoid.Value.kForward;
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

	/**
	 * Get the mode of the intake
	 *
	 * @return off, in slow, in fast, out slow, out fast.
	 */
	@Override
	public IntakeMode getMode() {
		return mode;
	}

	/**
	 * Set the speed of the intake to one of 5 IntakeModes.
	 *
	 * @param mode off, in slow, in fast, out slow, out fast.
	 */
	@Override
	public void setMode(IntakeMode mode) {
		this.mode = mode;
		switch (mode) {
			case OFF:
				setActuatedVictor(0);
				setFixedVictor(0);
				break;
			case IN_FAST:
				//In fast is used for picking up balls.
				setFixedVictor(fixedIntakeSpeed);
				setActuatedVictor(actuatedSpeed);
				break;
			case IN_SLOW:
				//In slow is used for agitation.
				setActuatedVictor(0);
				setFixedVictor(fixedAgitateSpeed);
				break;
			default:
				Logger.addEvent("Unsupported mode!", this.getClass());
		}
	}
}
