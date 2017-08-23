package org.usfirst.frc.team449.robot.drive.omnidirectional;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.drive.DriveSubsystem;

/**
 * A meccanum drive.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public interface DriveMeccanum extends DriveSubsystem {

    /**
     * Set the output of each wheel. All velocities are in the range [-1, 1].
     * @param fl the front-left wheel velocity
     * @param fr the front-right wheel velocity
     * @param bl the back-left wheel velocity
     * @param br the back-right wheel velocity
     */
    void setOutput(double fl, double fr, double bl, double br);

    /**
     * @return velocity of the front-left wheel
     */
    @Nullable
    Double getFrontLeftVel();

    /**
     * @return velocity of the front-right wheel
     */
    @Nullable
    Double getFrontRightVel();

    /**
     * @return velocity of the back-left wheel
     */
    @Nullable
    Double getBackLeftVel();

    /**
     * @return velocity of the back-right wheel
     */
    @Nullable
    Double getBackRightVel();
}
