package org.usfirst.frc.team449.robot.oi.omnidirectional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.oi.throttles.Throttle;

/**
 * Created by Sam Ehrenstein on 8/23/2017.
 */
public class OIMecanum implements OIFourCorner {

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

    @JsonCreator
    public OIMecanum(@NotNull @JsonProperty(required = true) Throttle yThrottle,
                     @NotNull @JsonProperty(required = true) Throttle xThrottle,
                     @NotNull @JsonProperty(required = true) Throttle rotThrottle){
        this.yThrottle = yThrottle;
        this.xThrottle = xThrottle;
        this.rotThrottle = rotThrottle;
    }

    public double getY(){
        return yThrottle.getValue();
    }

    public double getX(){
        return xThrottle.getValue();
    }

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
