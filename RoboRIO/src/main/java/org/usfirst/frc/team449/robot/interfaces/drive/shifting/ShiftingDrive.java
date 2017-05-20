package org.usfirst.frc.team449.robot.interfaces.drive.shifting;

/**
 * Created by noah on 5/13/17.
 */
public interface ShiftingDrive {

	void autoshift();

	gear getGear();

	void setGear(gear gear);

	boolean getOverrideAutoshift();

	void setOverrideAutoshift(boolean override);

	enum gear {
		HIGH, LOW
	}
}
