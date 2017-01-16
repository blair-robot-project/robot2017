package org.usfirst.frc.team449.robot.oi;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import org.usfirst.frc.team449.robot.Robot;
import org.usfirst.frc.team449.robot.mechanism.climber.commands.Climb;

/**
 * Created by Justin on 1/12/2017.
 */
public class OI2017 extends OISubsystem {

	Joystick joystick;
	JoystickButton climbButton;

	public OI2017(maps.org.usfirst.frc.team449.robot.oi.OI2017Map.OI2017 map) {
		super(map.getOi());
		this.map = map;
		joystick = new Joystick(map.getClimbController());
		climbButton = new JoystickButton(joystick, map.getClimbButton());
	}

	@Override
	protected void initDefaultCommand() {
		//Inheritance is stupid sometimes.
	}

	private void mapButtons() {
		climbButton.whileHeld(new Climb(Robot.climberSubsystem));
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
}
