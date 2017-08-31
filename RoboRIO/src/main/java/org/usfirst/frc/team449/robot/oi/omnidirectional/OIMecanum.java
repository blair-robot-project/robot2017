package org.usfirst.frc.team449.robot.oi.omnidirectional;

import com.fasterxml.jackson.annotation.*;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.oi.throttles.Throttle;

/**
 * An OI for controlling a mecanum drive.
 * Note: This OI can be used for either robot-centric or field-centric drive. For robot-centric drive, Y refers to the
 * front-back motion of the robot, and X refers to strafing motion. For field-centric drive, Y refers to motion
 * parallel to the gyro's zero, and X refers to motion perpendicular to that.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class OIMecanum extends OIOmnidirectional implements OIFourCorner {

    /**
     * The input controlling front-back velocity.
     */
    @NotNull
    private final Throttle yThrottle;

    /**
     * The input controlling strafing velocity.
     */
    @NotNull
    private final Throttle xThrottle;

    /**
     * The input controlling rotation.
     */
    @NotNull
    private final Throttle rotThrottle;

    /**
     * Default constructor.
     * @param yThrottle the throttle controlling Y velocity
     * @param xThrottle the throttle controlling X velocity
     * @param rotThrottle the throttle controlling rotational velocity
     */
    @JsonCreator
    public OIMecanum(@NotNull @JsonProperty(required = true) Throttle yThrottle,
                     @NotNull @JsonProperty(required = true) Throttle xThrottle,
                     @NotNull @JsonProperty(required = true) Throttle rotThrottle){
        this.yThrottle = yThrottle;
        this.xThrottle = xThrottle;
        this.rotThrottle = rotThrottle;
    }

    @Override
    public double getY(){
        return yThrottle.getValue();
    }

    @Override
    public double getX(){
        return xThrottle.getValue();
    }

    @Override
    public double getRot(){
        return rotThrottle.getValue();
    }

    @Override
    public double getFrontLeftOutput() {
        return getY() + getRot() + getX();
    }

    @Override
    public double getFrontRightOutput() {
        return getY() - getRot() - getX();
    }

    @Override
    public double getBackLeftOutput() {
        return getY() + getRot() - getX();
    }

    @Override
    public double getBackRightOutput() {
        return getY() - getRot() + getX();
    }
}
