package org.usfirst.frc.team449.robot.commands.multiInterface.drive;

import com.fasterxml.jackson.annotation.*;
import org.usfirst.frc.team449.robot.drive.omnidirectional.DriveMecanum;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlCommandWrapper;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;
import org.usfirst.frc.team449.robot.logger.Logger;
import org.usfirst.frc.team449.robot.oi.omnidirectional.OIMecanum;
import org.usfirst.frc.team449.robot.subsystem.interfaces.navX.SubsystemNavX;

/**
 *  Control a mecanum drive from an OI. I think this would also work for 4-kiwi and X-drive, but I can't confirm this.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class MecanumDefaultDrive <T extends YamlSubsystem & DriveMecanum & SubsystemNavX> extends YamlCommandWrapper{

    protected final T subsystem;

    protected final OIMecanum oi;

    @JsonCreator
    public MecanumDefaultDrive(@JsonProperty(required = true) T subsystem,
                               @JsonProperty(required = true) OIMecanum oi){
        this.subsystem = subsystem;
        this.oi = oi;
        requires(this.subsystem);
    }

    @Override
    protected void initialize() {
        Logger.addEvent("MecanumDefaultDrive init.", this.getClass());
    }

    @Override
    protected void execute() {
        //Set the wheels to the velocities calculated by the OI
        subsystem.setOutput(oi.getFrontLeftOutput(), oi.getFrontRightOutput(), oi.getBackLeftOutput(), oi.getBackRightOutput());
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}
