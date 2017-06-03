package org.usfirst.frc.team449.robot.mechanism.activegear;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.usfirst.frc.team449.robot.components.MappedDoubleSolenoid;
import org.usfirst.frc.team449.robot.interfaces.subsystem.solenoid.SolenoidSubsystem;

/**
 * The subsystem that carries and pushes gears.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ActiveGearSubsystem extends Subsystem implements SolenoidSubsystem {
	/**
	 * Whether piston is currently contracted
	 */
	private boolean contracted;

	/**
	 * Piston for pushing gears
	 */
	private DoubleSolenoid piston;

	/**
	 * Creates a mapped subsystem and sets its map
	 *
	 * @param piston The piston that comprises this subsystem.
	 */
	@JsonCreator
	public ActiveGearSubsystem(@JsonProperty(required = true) MappedDoubleSolenoid piston) {
		super();
		this.piston = piston;
	}

	/**
	 * Set the solenoid to a certain position.
	 *
	 * @param value Forward to extend the Solenoid, Reverse to contract it.
	 */
	public void setSolenoid(DoubleSolenoid.Value value) {
		piston.set(value);
		contracted = (value == DoubleSolenoid.Value.kReverse);
	}

	/**
	 * Get the position of the solenoid.
	 *
	 * @return Forward if extended, Reverse if contracted.
	 */
	@Override
	public DoubleSolenoid.Value getSolenoidPosition() {
		return contracted ? DoubleSolenoid.Value.kReverse : DoubleSolenoid.Value.kForward;
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
