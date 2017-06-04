package org.usfirst.frc.team449.robot.oi.buttons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.usfirst.frc.team449.robot.util.YamlCommand;
import org.usfirst.frc.team449.robot.util.YamlCommandWrapper;

/**
 * A button mapped to a command.
 */
public class CommandButton {

	/**
	 * Default constructor.
	 *
	 * @param button  The button that triggers the command.
	 * @param command The command to run or cancel.
	 * @param action  The action to do to the command.
	 */
	@JsonCreator
	public CommandButton(@JsonProperty(required = true) FactoryButton button,
	                     @JsonProperty(required = true) YamlCommand command,
	                     @JsonProperty(required = true) Action action) {
		switch (action) {
			case WHILE_HELD:
				button.whileHeld(command.getCommand());
				break;
			case WHEN_PRESSED:
				button.whenPressed(command.getCommand());
				break;
			case WHEN_RELEASED:
				button.whenReleased(command.getCommand());
				break;
			case CANCEL_WHEN_PRESSED:
				button.cancelWhenPressed(command.getCommand());
				break;
			case TOGGLE_WHEN_PRESSED:
				button.toggleWhenPressed(command.getCommand());
				break;
		}
	}

	/**
	 * The possible actions for the button to do to the command.
	 */
	enum Action {
		WHEN_PRESSED, WHILE_HELD, WHEN_RELEASED, TOGGLE_WHEN_PRESSED, CANCEL_WHEN_PRESSED
	}
}
