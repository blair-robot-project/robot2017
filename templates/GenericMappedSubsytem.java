package org.usfirst.frc.team449.template;

import com.google.protobuf.Message;
import org.usfirst.frc.team449.robot.MappedSubsystem;

/**
 * A broad template for a mapped subsystem.
 */
public class GenericMappedSubsytem extends MappedSubsystem{

	/**
	 * Construct a GenericMappedSubsystem
	 * @param map The config map.
	 */
	public GenericMappedSubsytem(Message map) {
		super(map);
		this.map = map;
	}

	/**
	 * Initialize the default command for a subsystem By default subsystems have
	 * no default command, but if they do, the default command is set with this
	 * method. It is called on all Subsystems by CommandBase in the users program
	 * after all the Subsystems are created.
	 */
	@Override
	protected void initDefaultCommand() {

	}
}
