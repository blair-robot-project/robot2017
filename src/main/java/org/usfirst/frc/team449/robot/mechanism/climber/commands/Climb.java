package org.usfirst.frc.team449.robot.mechanism.climber.commands;

import edu.wpi.first.wpilibj.CANTalon;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.climber.ClimberSubsystem;
import org.usfirst.frc.team449.robot.oi.OI2017;

/**
 * Created by Justin on 1/12/2017.
 */
public class Climb extends ReferencingCommand {
    /**
     * Instantiate a new <code>Climb</code>, taking control of the climber subsystem.
     */
    ClimberSubsystem climber;
    OI2017 oi;

    public Climb(ClimberSubsystem climber, OI2017 oi) {
        super(climber);
        requires(climber);
        this.climber=climber;
        this.oi=oi;
        climber.setControlMode(CANTalon.TalonControlMode.PercentVbus);
        System.out.println("Climb constructed");
    }

    @Override
    protected void initialize() { System.out.println("Climb init"); }

    @Override
    protected void execute() {
        //System.out.println("Climb execute");
        //System.out.println(oi.getClimberThrottle());
        climber.setPercentVbus(deadband(oi.getClimberThrottle()));
    }

    @Override
    protected boolean isFinished() { return false; }

    @Override
    protected void end() { System.out.println("Climb end"); }

    @Override
    protected void interrupted() { System.out.println("Climb interrupted"); }

    private double deadband(double v){
        if(Math.abs(v)<0.09){
            return 0;
        }
        return v;
    }

}
