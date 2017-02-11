package org.usfirst.frc.team449.robot.oi;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import maps.org.usfirst.frc.team449.robot.oi.OI2017ArcadeGamepadMap;
import org.usfirst.frc.team449.robot.Robot;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.NavXRelativeTTA;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.NavXTurnToAngle;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.SwitchToHighGear;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.SwitchToLowGear;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.ois.ArcadeOI;
import org.usfirst.frc.team449.robot.mechanism.climber.commands.CurrentClimb;
import org.usfirst.frc.team449.robot.mechanism.climber.commands.StopClimbing;
import org.usfirst.frc.team449.robot.oi.components.SmoothedThrottle;
import org.usfirst.frc.team449.robot.oi.components.Throttle;

/**
 * An OI for using an Xbox-style controller for an arcade drive, where one stick controls forward velocity and the other
 * controls turning velocity.
 */
public class OI2017ArcadeGamepad extends BaseOI implements ArcadeOI {

	//How much the D-pad moves the robot rotationally on a 0 to 1 scale, equivalent to pushing the turning stick that much of the way.
	private static double SHIFT;
	//The throttle wrapper for the stick controlling turning velocity.
	private Throttle rotThrottle;
	//The throttle wrapper for the stick controlling linear velocity.
	private Throttle fwdThrottle;
	private Joystick gamepad;
	private double deadband;
	private JoystickButton tt0, tt30, tt180, tt330, turnaround, switchToLowGear, switchToHighGear, climb;

	public OI2017ArcadeGamepad(OI2017ArcadeGamepadMap.OI2017ArcadeGamepad map) {
		//This is just to give the sticks better names and allow quickly swapping which is which according to driver preference.
		gamepad = new Joystick(map.getGamepad());
		SHIFT = map.getDpadShift();
		rotThrottle = new SmoothedThrottle(gamepad, map.getGamepadLeftAxis(), true);
		fwdThrottle = new SmoothedThrottle(gamepad, map.getGamepadRightAxis(), true);
		deadband = map.getDeadband();
		tt0 = new JoystickButton(gamepad, map.getTurnTo0Button());
		tt30 = new JoystickButton(gamepad, map.getTurnTo30Button());
		tt180 = new JoystickButton(gamepad, map.getTurnTo180Button());
		tt330 = new JoystickButton(gamepad, map.getTurnTo330Button());
		turnaround = new JoystickButton(gamepad, map.getTurnaroundButton());
		switchToLowGear = new JoystickButton(gamepad, map.getSwitchToLowGear());
		switchToHighGear = new JoystickButton(gamepad, map.getSwitchToHighGear());
		climb = new JoystickButton(gamepad, map.getClimb());
	}

	/**
	 * The output of the throttle controlling linear velocity, smoothed and adjusted according to what type of joystick it is.
	 * @return The processed stick output, sign-adjusted so 1 is forward and -1 is backwards.
	 */
	public double getFwd(){
		if (Math.abs(fwdThrottle.getValue()) > deadband) {
			return fwdThrottle.getValue();
		} else {
			return 0;
		}
	}

	/**
	 * Get the output of the D-pad or turning joystick, whichever is in use. If both are in use, the D-pad takes preference.
	 * @return The processed stick or D-pad output, sign-adjusted so 1 is right and -1 is left.
	 */
	public double getRot(){
		if (!(gamepad.getPOV() == -1 || gamepad.getPOV()%180 == 0)) {
			return gamepad.getPOV() < 180 ? -SHIFT : SHIFT;
		} else if (Math.abs(rotThrottle.getValue()) > deadband) {
			return rotThrottle.getValue();
		} else {
			return 0;
		}
	}

	public void mapButtons(){
		double timeout = 5.;
		turnaround.whenPressed(new NavXRelativeTTA(Robot.driveSubsystem.turnPID, 180, Robot.driveSubsystem, timeout));
		tt0.whenPressed(new NavXTurnToAngle(Robot.driveSubsystem.turnPID, 0, Robot.driveSubsystem, timeout));
		tt30.whenPressed(new NavXTurnToAngle(Robot.driveSubsystem.turnPID, 30, Robot.driveSubsystem, timeout));
		tt180.whenPressed(new NavXTurnToAngle(Robot.driveSubsystem.turnPID, 180, Robot.driveSubsystem, timeout));
		tt330.whenPressed(new NavXTurnToAngle(Robot.driveSubsystem.turnPID, -30, Robot.driveSubsystem, timeout));
		switchToHighGear.whenPressed(new SwitchToHighGear(Robot.driveSubsystem));
		switchToLowGear.whenPressed(new SwitchToLowGear(Robot.driveSubsystem));
		climb.whenPressed(new CurrentClimb(Robot.climberSubsystem));
		climb.whenReleased(new StopClimbing(Robot.climberSubsystem));
	}
}
