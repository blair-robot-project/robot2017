package org.usfirst.frc.team449.robot.interfaces.subsystem.solenoid;

import edu.wpi.first.wpilibj.DoubleSolenoid;

/**
 * Created by noah on 5/13/17.
 */
public interface SolenoidSubsystem {
	void setSolenoid(DoubleSolenoid.Value value);
	DoubleSolenoid.Value getSolenoidPosition();
}
