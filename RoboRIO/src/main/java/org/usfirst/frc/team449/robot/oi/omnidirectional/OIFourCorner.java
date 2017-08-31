package org.usfirst.frc.team449.robot.oi.omnidirectional;

/**
 * An OI to control a drivetrain where each corner has a fixed, independently-controlled wheel (e.g. kiwi or meccanum).
 */
public interface OIFourCorner {

    /**
     * The output to be given to the front-left wheel.
     * @return the front-left wheel output in the range [-1, 1]
     */
    double getFrontLeftOutput();

    double getFrontRightOutput();

    double getBackLeftOutput();

    double getBackRightOutput();

}
