package org.usfirst.frc.team449.robot.oi.omnidirectional;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Created by Sam Ehrenstein on 8/30/2017.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public abstract class OIOmnidirectional {

    public abstract double getX();

    public abstract double getY();

    public abstract double getRot();

    /**
     * Whether the driver is trying to drive straight
     * @return true if the driver is trying to go straight; false otherwise
     */
    public boolean commandingStraight() {
        return getRot() == 0;
    }
}
