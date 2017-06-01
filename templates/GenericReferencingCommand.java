package org.usfirst.frc.team449.template;

import org.usfirst.frc.team449.robot.MappedSubsystem;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.util.Logger;

/**
 * A broad template for all referencingCommands.
 */
public class GenericReferencingCommand extends ReferencingCommand{

	/**
	 * The subsystem to execute this command on.
	 */
	private GenericMappedSubsytem specificSubsystem;

	/**
	 * Default constructor
	 * @param subsystem The subsystem to execute this command on
	 */
	public GenericReferencingCommand(MappedSubsystem subsystem) {
		super(subsystem);
		specificSubsystem = (GenericMappedSubsytem) subsystem;
	}

	/**
	 *
	 */
	@Override
	protected void initialize() {
		Logger.addEvent("GenericReferencingCommand init.", this.getClass());
	}

	/**
	 *
	 */
	@Override
	protected void execute() {
	}

	/**
	 *
	 * @return
	 */
	@Override
	protected boolean isFinished() {
		//This does NOT have to be true.
		return true;
	}

	/**
	 *
	 */
	@Override
	protected void end() {
		Logger.addEvent("GenericReferencingCommand end.", this.getClass());
	}

	/**
	 *
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("GenericReferencingCommand Interrupted!", this.getClass());
	}
}