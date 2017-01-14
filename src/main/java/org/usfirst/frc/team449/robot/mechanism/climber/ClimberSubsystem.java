package org.usfirst.frc.team449.robot.mechanism.climber;

import org.usfirst.frc.team449.robot.mechanism.MechanismSubsystem;
import org.usfirst.frc.team449.robot.mechanism.climber.commands.Climb;

/**
 * Created by Justin on 1/12/2017.
 */
public class ClimberSubsystem extends MechanismSubsystem {

    public ClimberSubsystem(maps.org.usfirst.frc.team449.robot.mechanism.climber.ClimberMap.Climber map) {
        super(map.getMechanism());
    }

    /**
     * Initialize the default command for a subsystem By default subsystems have
     * no default command, but if they do, the default command is set with this
     * method. It is called on all Subsystems by CommandBase in the users program
     * after all the Subsystems are created.
     */
    @Override
    protected void initDefaultCommand() {}
}
