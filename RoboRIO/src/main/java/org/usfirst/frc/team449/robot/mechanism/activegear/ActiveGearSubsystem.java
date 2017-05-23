package org.usfirst.frc.team449.robot.mechanism.activegear;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import org.usfirst.frc.team449.robot.MappedSubsystem;
import org.usfirst.frc.team449.robot.components.MappedDoubleSolenoid;
import org.usfirst.frc.team449.robot.interfaces.subsystem.solenoid.SolenoidSubsystem;

/**
 * The subsystem that carries and pushes gears.
 */
public class ActiveGearSubsystem extends MappedSubsystem implements SolenoidSubsystem {
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
	 * @param map the map of constants relevant to this subsystem
	 */
	public ActiveGearSubsystem(maps.org.usfirst.frc.team449.robot.mechanism.activegear.ActiveGearMap.ActiveGear map) {
		super(map.getMechanism());
		this.map = map;
		this.piston = new MappedDoubleSolenoid(map.getPiston());
	}

	/**
	 * Set the solenoid to a certain position.
	 * @param value Forward to extend the Solenoid, Reverse to contract it.
	 */
	public void setSolenoid(DoubleSolenoid.Value value) {
		piston.set(value);
		contracted = (value == DoubleSolenoid.Value.kReverse);
	}

	/**
	 * Get the position of the solenoid.
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
