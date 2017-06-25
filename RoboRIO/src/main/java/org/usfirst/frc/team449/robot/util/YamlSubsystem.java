package org.usfirst.frc.team449.robot.util;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Created by noahg on 17-Jun-17.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public abstract class YamlSubsystem extends Subsystem {
}
