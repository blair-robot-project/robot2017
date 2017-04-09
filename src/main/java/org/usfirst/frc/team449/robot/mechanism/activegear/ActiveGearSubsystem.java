package org.usfirst.frc.team449.robot.mechanism.activegear;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.VictorSP;
import org.usfirst.frc.team449.robot.MappedSubsystem;

/**
 * The subsystem that carries and pushes gears.
 */
public class ActiveGearSubsystem extends MappedSubsystem {
	/**
	 * Piston for pushing gears
	 */
	private DoubleSolenoid piston;
	/**
	 * Whether piston is currently contracted
	 */
	public boolean contracted;

	/**
	 * Creates a mapped subsystem and sets its map
	 *
	 * @param map the map of constants relevant to this subsystem
	 */
	public ActiveGearSubsystem(maps.org.usfirst.frc.team449.robot.mechanism.activegear.ActiveGearMap.ActiveGear map) {
		super(map.getMechanism());
		this.map = map;
		this.piston = new DoubleSolenoid(map.getModuleNumber(), map.getPiston().getForward(), map.getPiston().getReverse());
	}

	/**
	 * Fire the piston
	 *
	 * @param value direction to fire
	 */
	public void setPiston(DoubleSolenoid.Value value) {
		piston.set(value);
		System.out.println("Set Piston");
		contracted = (value == DoubleSolenoid.Value.kReverse);
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
