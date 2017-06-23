package org.usfirst.frc.team449.robot.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.List;

/**
 * A command group that takes a list of commands and runs them in the order given.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class CommandSequence extends YamlCommandGroupWrapper{

	/**
	 * Default constructor
	 * @param commandList The commands to run, in order.
	 */
	@JsonCreator
	public CommandSequence(@JsonProperty(required = true) List<YamlCommand> commandList){
		for (YamlCommand command : commandList){
			addSequential(command.getCommand());
		}
	}
}
