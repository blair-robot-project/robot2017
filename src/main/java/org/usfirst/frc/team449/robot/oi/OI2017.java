package org.usfirst.frc.team449.robot.oi;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import org.usfirst.frc.team449.robot.Robot;
import org.usfirst.frc.team449.robot.mechanism.doubleflywheelshooter.commands.ToggleFlywheel;

/**
 * Created by blairrobot on 1/10/17.
 */
public class OI2017 extends OISubsystem {

	private Joystick buttonPad;
	private Button toggleFlywheel;

	public OI2017(maps.org.usfirst.frc.team449.robot.oi.OI2017Map.OI2017 map) {
		super(map.getOi());
		buttonPad = new Joystick(map.getButtonPad());
		toggleFlywheel = new JoystickButton(buttonPad, map.getToggleFlywheel());
	}

	public void mapButtons() {
		toggleFlywheel.whenPressed(new ToggleFlywheel(Robot.shooterSubsystem));
	}

	@Override
	public double getDriveAxisLeft() {
		return 0; //Do Nothing!
	}

	@Override
	public double getDriveAxisRight() {
		return 0; //Do Nothing!
	}

	@Override
	public void toggleCamera() {
		//Do Nothing!
	}

	@Override
	protected void initDefaultCommand() {
		//Do Nothing!
	}
}
