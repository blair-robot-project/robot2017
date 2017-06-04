package org.usfirst.frc.team449.robot.util;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import edu.wpi.first.wpilibj.command.Command;

/**
 * A wrapper on {@link Command} with @JsonTypeInfo so we can use it in maps.
 */
public abstract class YamlCommandWrapper extends Command implements YamlCommand{

	/**
	 * Return the Command this is a wrapper on.
	 * @return this.
	 */
	@Override
	public Command getCommand() {
		return this;
	}
}
