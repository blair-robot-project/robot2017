package org.usfirst.frc.team449.robot.mechanism.activegear;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.components.MappedDoubleSolenoid;
import org.usfirst.frc.team449.robot.interfaces.subsystem.solenoid.SolenoidSubsystem;
import org.usfirst.frc.team449.robot.util.YamlSubsystem;

/**
 * The subsystem that carries and pushes gears.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ActiveGearSubsystem extends YamlSubsystem implements SolenoidSubsystem {

	/**
	 * Piston for pushing gears
	 */
	@NotNull
	private final DoubleSolenoid piston;

	/**
	 * Whether piston is currently contracted
	 */
	private boolean contracted;

	/**
	 * Default constructor
	 *
	 * @param piston The piston that comprises this subsystem.
	 */
	@JsonCreator
	public ActiveGearSubsystem(@NotNull @JsonProperty(required = true) MappedDoubleSolenoid piston) {
		this.piston = piston;
	}

	/**
	 * @param value The position to set the solenoid to.
	 */
	public void setSolenoid(@NotNull DoubleSolenoid.Value value) {
		piston.set(value);
		contracted = (value == DoubleSolenoid.Value.kReverse);
	}

	/**
	 * @return the current position of the solenoid.
	 */
	@NotNull
	@Override
	public DoubleSolenoid.Value getSolenoidPosition() {
		return contracted ? DoubleSolenoid.Value.kReverse : DoubleSolenoid.Value.kForward;
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
}
