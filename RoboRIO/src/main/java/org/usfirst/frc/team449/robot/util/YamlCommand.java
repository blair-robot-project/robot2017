package org.usfirst.frc.team449.robot.util;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import edu.wpi.first.wpilibj.command.Command;

/**
 * A command that's constructable from YAML.
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.WRAPPER_OBJECT, property="@class")
public interface YamlCommand {

	Command getCommand();
}
