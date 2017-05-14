package org.usfirst.frc.team449.robot.interfaces.drive.shifting;

/**
 * Created by noah on 5/13/17.
 */
public interface ShiftingDrive {

	void autoshift();
	void setGear(gear gear);
	gear getGear();
	void setOverrideAutoshift(boolean override);
	boolean getOverrideAutoshift();

	enum gear{
		HIGH, LOW
	}
}
