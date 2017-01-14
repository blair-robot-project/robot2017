package org.usfirst.frc.team449.robot.mechanism.climber.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.climber.ClimberSubsystem;

/**
 * Created by Justin on 1/12/2017.
 */
public class Climb extends ReferencingCommand {
    /**
     * Instantiate a new <code>Climb</code>, taking control of the climber subsystem.
     */
    public Climb(ClimberSubsystem climber) {
        super(climber);
        requires(climber);
    }

    @Override
    protected void initialize() { System.out.println("Climb init"); }

    @Override
    protected void execute() { System.out.println("Climb execute"); }

    @Override
    protected boolean isFinished() { return true; }

    @Override
    protected void end() { System.out.println("Climb end"); }

    @Override
    protected void interrupted() { System.out.println("Climb interrupted"); }


}
