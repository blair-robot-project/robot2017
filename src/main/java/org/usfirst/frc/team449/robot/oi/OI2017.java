package org.usfirst.frc.team449.robot.oi;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import org.usfirst.frc.team449.robot.Robot;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.NavXDriveStraight;
import org.usfirst.frc.team449.robot.oi.components.PolyThrottle;
import org.usfirst.frc.team449.robot.oi.components.Throttle;

/**
 * Created by blairrobot on 1/9/17.
 */
public class OI2017 extends OISubsystem{

	private double minimumOutput;

	private Throttle leftThrottle;
	private Throttle rightThrottle;
	private JoystickButton tt90;
	private JoystickButton driveStraight;

	public OI2017(maps.org.usfirst.frc.team449.robot.oi.OI2017Map.OI2017 map){
		super(map.getOi());
		this.map = map;
		this.minimumOutput = map.getMinimumOutput();
		Joystick rightStick = new Joystick(map.getRightStick());
		Joystick leftStick = new Joystick(map.getLeftStick());
		leftThrottle = new PolyThrottle(leftStick, 1, 1);
		rightThrottle = new PolyThrottle(rightStick, 1, 1);
//		leftThrottle = new SmoothedThrottle(leftStick, 1);
//		rightThrottle = new SmoothedThrottle(rightStick, 1);
//		leftThrottle = new ExpThrottle(leftStick, 1, 50);
//		rightThrottle = new ExpThrottle(rightStick, 1, 50);
		tt90 = new JoystickButton(leftStick, 1);
		driveStraight = new JoystickButton(rightStick, 1);
	}

	public void mapButtons(){
		//tt90.whenPressed(new NavXTurnToAngle(Robot.driveSubsystem.turnPID, 90, Robot.driveSubsystem, 2.5));
		driveStraight.whileHeld(new NavXDriveStraight(Robot.driveSubsystem.straightPID, Robot.driveSubsystem, this));
	}

	@Override
	public double getDriveAxisLeft() {
		if (Math.abs(leftThrottle.getValue()) > minimumOutput)
			return -leftThrottle.getValue();
		return 0;
	}

	@Override
	public double getDriveAxisRight() {
		if (Math.abs(rightThrottle.getValue()) > minimumOutput)
			return -rightThrottle.getValue();
		return 0;
	}

	@Override
	public void toggleCamera() {
		//Do Nothing!
	}

	@Override
	protected void initDefaultCommand() {
		//Inheritance is stupid sometimes.
	}
}
