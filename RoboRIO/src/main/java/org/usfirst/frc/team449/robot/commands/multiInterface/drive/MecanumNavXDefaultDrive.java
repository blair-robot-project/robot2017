package org.usfirst.frc.team449.robot.commands.multiInterface.drive;

import com.fasterxml.jackson.annotation.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.drive.omnidirectional.DriveMecanum;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;
import org.usfirst.frc.team449.robot.logger.Logger;
import org.usfirst.frc.team449.robot.oi.omnidirectional.OIMecanum;
import org.usfirst.frc.team449.robot.other.BufferTimer;
import org.usfirst.frc.team449.robot.subsystem.interfaces.navX.SubsystemNavX;
import org.usfirst.frc.team449.robot.subsystem.interfaces.navX.commands.PIDAngleCommand;

/**
 * Control a mecanum drive using joysticks. Uses a NavX to implement field-centric control.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class MecanumNavXDefaultDrive<T extends YamlSubsystem & DriveMecanum & SubsystemNavX> extends PIDAngleCommand{

    /**
     * The subsystem controlled by this command.
     */
    @NotNull
    protected final T subsystem;

    /**
     * The OI giving the drive input values.
     */
    @NotNull
    protected final OIMecanum oi;

    /**
     * The maximum velocity for the robot to be at in order to switch to driveStraight, in degrees/sec
     */
    private final double maxAngularVelToEnterLoop;

    /**
     * A bufferTimer so we only switch to driving straight when the conditions are met for a certain period of time.
     */
    @NotNull
    private final BufferTimer driveStraightLoopEntryTimer;

    /**
     * Whether or not we should be using the NavX to drive straight stably.
     */
    private boolean drivingStraight;

    /**
     * Default constructor
     *
     * @param absoluteTolerance           The maximum number of degrees off from the target at which we can be
     *                                    considered within tolerance.
     * @param toleranceBuffer             How many consecutive loops have to be run while within tolerance to be
     *                                    considered on target. Multiply by loop period of ~20 milliseconds for time.
     *                                    Defaults to 0.
     * @param minimumOutput               The minimum output of the loop. Defaults to zero.
     * @param maximumOutput               The maximum output of the loop. Can be null, and if it is, no maximum output
     *                                    is used.
     * @param deadband                    The deadband around the setpoint, in degrees, within which no output is given
     *                                    to the motors. Defaults to zero.
     * @param maxAngularVelToEnterLoop    The maximum angular velocity, in degrees/sec, at which the loop will be
     *                                    entered. Defaults to 180.
     * @param inverted                    Whether the loop is inverted. Defaults to false.
     * @param kP                          Proportional gain. Defaults to zero.
     * @param kI                          Integral gain. Defaults to zero.
     * @param kD                          Derivative gain. Defaults to zero.
     * @param driveStraightLoopEntryTimer The buffer timer for starting to drive straight.
     * @param subsystem                   The drive to execute this command on.
     * @param oi                          The OI controlling the robot.
     */
    @JsonCreator
    public MecanumNavXDefaultDrive(@JsonProperty(required = true) double absoluteTolerance,
                                   int toleranceBuffer,
                                   double minimumOutput, @Nullable Double maximumOutput,
                                   double deadband,
                                   @Nullable Double maxAngularVelToEnterLoop,
                                   boolean inverted,
                                   int kP,
                                   int kI,
                                   int kD,
                                   @NotNull @JsonProperty(required = true) BufferTimer driveStraightLoopEntryTimer,
                                   @NotNull @JsonProperty(required = true)T subsystem,
                                   @NotNull@JsonProperty(required = true)OIMecanum oi){
        super(absoluteTolerance, toleranceBuffer, minimumOutput, maximumOutput, deadband, inverted, subsystem, kP, kI, kD);
        this.subsystem = subsystem;
        this.oi = oi;

        this.driveStraightLoopEntryTimer = driveStraightLoopEntryTimer;
        this.maxAngularVelToEnterLoop = maxAngularVelToEnterLoop != null ? maxAngularVelToEnterLoop : 180;

        requires(this.subsystem);
    }

    @Override
    protected void initialize() {
        //Reset all values of the PIDController and enable it.
        this.getPIDController().reset();
        this.getPIDController().enable();
        Logger.addEvent("MecanumNavXDefaultDrive init.", this.getClass());

        //Initial assignment
        drivingStraight = false;
    }

    @Override
    protected void execute() {
        //Whether the driver wants to be driving straight
        boolean commandingStraight = oi.commandingStraight();

        //If we are driving straight and want to turn or override the NavX
        if(drivingStraight && (!commandingStraight || subsystem.getOverrideNavX())){
            drivingStraight = false;
            Logger.addEvent("Switching to free drive.",this.getClass());
        }
        //If we are free driving and want to drive straight
        else if(driveStraightLoopEntryTimer.get(!subsystem.getOverrideNavX()) && !drivingStraight &&
                commandingStraight && Math.abs(subsystem.getNavX().getRate()) <= maxAngularVelToEnterLoop){
            //Switch to drive straight
            drivingStraight = true;
            //Start the angle PID
            this.getPIDController().reset();
            this.getPIDController().setSetpoint(subsystem.getGyroOutput());
            this.getPIDController().enable();
            Logger.addEvent("Switching to drive straight.",this.getClass());
        }
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

    @Override
    protected void usePIDOutput(double output) {
        output = processPIDOutput(output);

        //If driving straight, the only rotation should be to correct the heading
        //If not, the rotational velocity is controlled by the driver
        //TODO make sure that output has the correct sign
        double vR = drivingStraight ? -output : oi.getRot();
        /*
        A bunch of math here. What this block does is calculate the magnitude and direction of the desired linear
        velocity in the robot's coordinate system and rotate it by the gyro angle to be in the field's coordinate
        system. The field's coordinate system will be aligned with the gyro's zero angle, which is set at the start of
        the match.
         */
        double x = oi.getX();
        double y = oi.getY();
        double vD = Math.sqrt(oi.getX()*oi.getX()+oi.getY()*oi.getY()); //magnitude of the linear velocity vector
        double angleRad = subsystem.getGyroOutput()*Math.PI/180.0;  //gyro angle in radians
        double thetaRad = angleRad + Math.atan(y / x);  //the angle to move at in the field coordinates; in [0,2pi)
        if(x < 0){
            thetaRad += Math.PI;    //make thetaRad be in [0,2pi)
        }
        if(thetaRad < 0){
            thetaRad += 2*Math.PI;  //same physical angle, but the value will be in [0,2pi)
        }

        //calculate the speed for each wheel based on vD and vR
        double fl = vD*Math.sin(thetaRad+Math.PI/4.0)+vR;
        double fr = vD*Math.cos(thetaRad+Math.PI/4.0)-vR;
        double bl = vD*Math.cos(thetaRad+Math.PI/4.0)+vR;
        double br = vD*Math.sin(thetaRad+Math.PI/4.0)-vR;

        subsystem.setOutput(fl, fr, bl, br);
    }
}