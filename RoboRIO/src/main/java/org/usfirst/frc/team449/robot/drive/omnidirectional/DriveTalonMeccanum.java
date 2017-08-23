package org.usfirst.frc.team449.robot.drive.omnidirectional;

import com.fasterxml.jackson.annotation.*;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.command.Command;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedAHRS;
import org.usfirst.frc.team449.robot.jacksonWrappers.RotPerSecCANTalon;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;
import org.usfirst.frc.team449.robot.logger.Loggable;
import org.usfirst.frc.team449.robot.subsystem.interfaces.navX.SubsystemNavX;

/**
 * A meccanum drive. Meccanum drives have an independently-driven meccanum wheel at each corner, allowing
 * omnidirectional movement.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class DriveTalonMeccanum extends YamlSubsystem implements SubsystemNavX, DriveMeccanum, Loggable {

    protected final double PID_SCALE;

    @NotNull
    protected final RotPerSecCANTalon frontLeft;

    @NotNull
    protected final RotPerSecCANTalon frontRight;

    @NotNull
    protected final RotPerSecCANTalon backLeft;

    @NotNull
    protected final RotPerSecCANTalon backRight;

    @NotNull
    private final AHRS navX;

    private boolean overrideNavX;

    /**
     * Default constructor.
     * @param frontLeft the Talon for the front-left wheel
     * @param frontRight the Talon for the front-right wheel
     * @param backLeft the Talon for the back-left wheel
     * @param backRight the Talon for the back-right wheel
     * @param navX the NavX
     * @param PIDScale PID scaling constant for joystick values
     */
    @JsonCreator
    public DriveTalonMeccanum(@NotNull @JsonProperty(required = true) RotPerSecCANTalon frontLeft,
                              @NotNull @JsonProperty(required = true) RotPerSecCANTalon frontRight,
                              @NotNull @JsonProperty(required = true) RotPerSecCANTalon backLeft,
                              @NotNull @JsonProperty(required = true) RotPerSecCANTalon backRight,
                              @NotNull @JsonProperty(required = true) MappedAHRS navX,
                              @Nullable Double PIDScale){
        super();
        //Initialization
        this.PID_SCALE = PIDScale;
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.backLeft = backLeft;
        this.backRight = backRight;
        this.navX = navX;
    }

    /**
     * Simple helper function for clipping output to the -1 to 1 scale.
     *
     * @param in The number to be processed.
     * @return That number, clipped to 1 if it's greater than 1 or clipped to -1 if it's less than -1.
     */
    private static double clipToOne(double in) {
        return Math.min(Math.max(in, -1), 1);
    }

    /**
     * Set the velocity of each wheel as a percentage of max voltage
     * @param fl the front-left wheel velocity
     * @param fr the front-right wheel velocity
     * @param bl the back-left wheel velocity
     * @param br the back-right wheel velocity
     */
    protected void setVBusThrottle(double fl, double fr, double bl, double br){
        frontLeft.setPercentVbus(fl);
        frontRight.setPercentVbus(fr);
        backLeft.setPercentVbus(bl);
        backRight.setPercentVbus(br);
    }

    /**
     * Set the PID velocity setpoint of each wheel as a percentage of max velocity
     * @param fl the front-left wheel velocity
     * @param fr the front-right wheel velocity
     * @param bl the back-left wheel velocity
     * @param br the back-right wheel velocity
     */
    protected void setPIDThrottle(double fl, double fr, double bl, double br){
        frontLeft.setPercentVbus(fl);
        frontRight.setPercentVbus(fr);
        backLeft.setPercentVbus(bl);
        backRight.setPercentVbus(br);
    }

    public void enableMotors(){
        frontLeft.getCanTalon().enable();
        frontRight.getCanTalon().enable();
        backLeft.getCanTalon().enable();
        backRight.getCanTalon().enable();
    }

    public void setOutput(double fl, double fr, double bl, double br){
        setPIDThrottle(clipToOne(fl), clipToOne(fr), clipToOne(bl), clipToOne(br));
    }

    @Override
    @Nullable
    public Double getFrontLeftVel() {
        return frontLeft.getSpeed();
    }

    @Nullable
    @Override
    public Double getFrontRightVel() {
        return frontRight.getSpeed();
    }

    @Nullable
    @Override
    public Double getBackLeftVel() {
        return backLeft.getSpeed();
    }

    @Nullable
    @Override
    public Double getBackRightVel() {
        return backRight.getSpeed();
    }

    public void fullStop(){
        setVBusThrottle(0,0,0,0);
    }

    /**
     * Stuff run on first enable.
     */
    @Override
    protected void initDefaultCommand() {
        //Do nothing, the default command gets set with setDefaultCommandManual
    }

    /**
     * Set the default command. Done here instead of in initDefaultCommand so we don't have a defaultCommand during
     * auto.
     *
     * @param defaultCommand The command to have run by default. Must require this subsystem.
     */
    public void setDefaultCommandManual(Command defaultCommand) {
        setDefaultCommand(defaultCommand);
    }

    /**
     * Get the robot's heading using the navX
     *
     * @return robot heading, in degrees, on [-180, 180]
     */
    @Override
    public double getGyroOutput() {
        return navX.pidGet();
    }

    /**
     * @return true if the NavX is currently overriden, false otherwise.
     */
    @Override
    public boolean getOverrideNavX() {
        return overrideNavX;
    }

    /**
     * @param override true to override the NavX, false to un-override it.
     */
    @Override
    public void setOverrideNavX(boolean override) {
        overrideNavX = override;
    }

    /**
     * @return An AHRS object representing this subsystem's NavX.
     */
    @Override
    @NotNull
    public AHRS getNavX() {
        return navX;
    }

    /**
     * Get the headers for the data this subsystem logs every loop.
     *
     * @return An N-length array of String labels for data, where N is the length of the Object[] returned by getData().
     */
    @Override
    @NotNull
    @Contract(pure = true)
    public String[] getHeader() {
        return new String[]{"fl_vel",
                "fr_vel",
                "bl_vel",
                "br_vel",
                "fl_setpoint",
                "fr_setpoint",
                "bl_setpoint",
                "br_setpoint",
                "fl_current",
                "fr_current",
                "bl_current",
                "br_current",
                "fl_voltage",
                "fr_voltage",
                "bl_voltage",
                "br_voltage"};
    }

    /**
     * Get the data this subsystem logs every loop.
     *
     * @return An N-length array of Objects, where N is the number of labels given by getHeader.
     */
    @Override
    @NotNull
    public Object[] getData() {
        return new Object[]{frontLeft.getSpeed(),
                frontRight.getSpeed(),
                backLeft.getSpeed(),
                backRight.getSpeed(),
                frontLeft.getSetpoint(),
                frontRight.getSetpoint(),
                backLeft.getSetpoint(),
                backRight.getSetpoint(),
                frontLeft.getCanTalon().getOutputCurrent(),
                frontRight.getCanTalon().getOutputCurrent(),
                backLeft.getCanTalon().getOutputCurrent(),
                backRight.getCanTalon().getOutputCurrent(),
                frontLeft.getCanTalon().getOutputVoltage(),
                frontRight.getCanTalon().getOutputVoltage(),
                backLeft.getCanTalon().getOutputVoltage(),
                backRight.getCanTalon().getOutputVoltage()};
    }

    /**
     * Get the name of this object.
     *
     * @return A string that will identify this object in the log file.
     */
    @Override
    @NotNull
    @Contract(pure = true)
    public String getName() {
        return "Drive";
    }
}
