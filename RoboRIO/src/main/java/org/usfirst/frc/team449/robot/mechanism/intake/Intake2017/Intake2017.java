package org.usfirst.frc.team449.robot.mechanism.intake.Intake2017;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.VictorSP;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.components.MappedDoubleSolenoid;
import org.usfirst.frc.team449.robot.components.MappedVictor;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Intake.IntakeSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.solenoid.SolenoidSubsystem;
import org.usfirst.frc.team449.robot.util.Logger;
import org.usfirst.frc.team449.robot.util.YamlSubsystem;

/**
 * A subsystem that picks up balls from the ground.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class Intake2017 extends YamlSubsystem implements SolenoidSubsystem, IntakeSubsystem {

	/**
	 * VictorSP for the fixed intake
	 */
	@NotNull
	private final VictorSP fixedVictor;

	/**
	 * VictorSP for the actuated intake
	 */
	@Nullable
	private final VictorSP actuatedVictor;

	/**
	 * Piston for raising and lowering the intake
	 */
	@Nullable
	private final DoubleSolenoid piston;

	/**
	 * How fast the fixed victor should go to pick up balls, on [-1, 1]
	 */
	private final double fixedIntakeSpeed;

	/**
	 * How fast the fixed victor should go to agitate balls while they're being fed into the shooter, on [-1, 1]
	 */
	private final double fixedAgitateSpeed;

	/**
	 * How fast the actuated victor should go to pick up balls, on [-1, 1]
	 */
	private final double actuatedSpeed;

	/**
	 * Whether intake is currently up
	 */
	private boolean intakeUp;

	/**
	 * The mode the intake's currently in.
	 */
	@NotNull
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
	public Intake2017(@NotNull @JsonProperty(required = true) MappedVictor fixedVictor,
	                  @JsonProperty(required = true) double fixedAgitateSpeed,
	                  @JsonProperty(required = true) double fixedIntakeSpeed,
	                  @Nullable MappedVictor actuatedVictor,
	                  double actuatedSpeed,
	                  @Nullable MappedDoubleSolenoid piston) {
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
	 * @param value The position to set the solenoid to.
	 */
	public void setSolenoid(@NotNull DoubleSolenoid.Value value) {
		if (piston != null) {
			piston.set(value);
			intakeUp = (value == DoubleSolenoid.Value.kReverse);
		}
	}

	/**
	 * @return the current position of the solenoid.
	 */
	@NotNull
	public DoubleSolenoid.Value getSolenoidPosition() {
		return intakeUp ? DoubleSolenoid.Value.kReverse : DoubleSolenoid.Value.kForward;
	}

	/**
	 * Initialize the default command for a subsystem. By default subsystems have no default command, but if they do,
	 * the default command is set with this method. It is called on all Subsystems by CommandBase in the users program
	 * after all the Subsystems are created.
	 */
	@Override
	protected void initDefaultCommand() {
		//Do nothing!
	}

	/**
	 * @return the current mode of the intake.
	 */
	@NotNull
	@Override
	public IntakeMode getMode() {
		return mode;
	}

	/**
	 * @param mode The mode to switch the intake to.
	 */
	@Override
	public void setMode(@NotNull IntakeMode mode) {
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
