package org.usfirst.frc.team449.robot.oi;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team449.robot.Robot;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.NavXDriveStraight;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.NavXRelativeTTA;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.NavXTurnToAngle;
import org.usfirst.frc.team449.robot.mechanism.intake.commands.ToggleIntakeUpDown;
import org.usfirst.frc.team449.robot.mechanism.intake.commands.ToggleIntaking;
import org.usfirst.frc.team449.robot.oi.components.PolyThrottle;
import org.usfirst.frc.team449.robot.oi.components.Throttle;

/**
 * Created by blairrobot on 1/9/17.
 */
public class OI2017 extends OISubsystem {

	public Joystick gamepad;
	public Throttle gRight;
	public Throttle gLeft;
	protected double joystickDeadband;
	protected Throttle leftThrottle;
	protected Throttle rightThrottle;
	private Joystick buttonPad;
	private JoystickButton tt0;
	private JoystickButton tt30;
	private JoystickButton tt180;
	private JoystickButton tt330;
	private JoystickButton turnaround;
	private JoystickButton driveStraight;
	private JoystickButton climbButton;
	private JoystickButton toggleFlywheel;
	private JoystickButton toggleIntake;
	private JoystickButton toggleIntakeUpDown;

	public OI2017(maps.org.usfirst.frc.team449.robot.oi.OI2017Map.OI2017 map) {
		super(map.getOi());
		this.map = map;
		this.joystickDeadband = map.getJoystickDeadband();
		gamepad = new Joystick(5);
		gRight = new PolyThrottle(gamepad, 3, 1);
		gLeft = new PolyThrottle(gamepad, 0, 1);
		Joystick rightStick = new Joystick(map.getRightStick());
		Joystick leftStick = new Joystick(map.getLeftStick());
		buttonPad = new Joystick(map.getButtonPad());
		leftThrottle = new PolyThrottle(leftStick, 1, 1);
		rightThrottle = new PolyThrottle(rightStick, 1, 1);
//		leftThrottle = new SmoothedThrottle(leftStick, 1);
//		rightThrottle = new SmoothedThrottle(rightStick, 1);
//		leftThrottle = new ExpThrottle(leftStick, 1, 50);
//		rightThrottle = new ExpThrottle(rightStick, 1, 50);
		turnaround = new JoystickButton(leftStick, map.getTurnaroundButton());
		tt0 = new JoystickButton(leftStick, map.getTurnTo0Button());
		tt30 = new JoystickButton(leftStick, map.getTurnTo30Button());
		tt180 = new JoystickButton(leftStick, map.getTurnTo180Button());
		tt330 = new JoystickButton(leftStick, map.getTurnTo330Button());
		driveStraight = new JoystickButton(rightStick, 1);

		climbButton = new JoystickButton(buttonPad, map.getClimbButton());
		//toggleFlywheel = new JoystickButton(buttonPad, map.getToggleFlywheel());
		toggleIntake = new JoystickButton(buttonPad, map.getToggleIntake());
		toggleIntakeUpDown = new JoystickButton(buttonPad, map.getToggleIntakeUpdown());
	}

	public void mapButtons() {
		turnaround.whenPressed(new NavXRelativeTTA(Robot.driveSubsystem.turnPID, 180, Robot.driveSubsystem, 2.5));
		tt0.whenPressed(new NavXTurnToAngle(Robot.driveSubsystem.turnPID, 0, Robot.driveSubsystem, 2.5));
		tt30.whenPressed(new NavXTurnToAngle(Robot.driveSubsystem.turnPID, 30, Robot.driveSubsystem, 2.5));
		tt180.whenPressed(new NavXTurnToAngle(Robot.driveSubsystem.turnPID, 180, Robot.driveSubsystem, 2.5));
		tt330.whenPressed(new NavXTurnToAngle(Robot.driveSubsystem.turnPID, -30, Robot.driveSubsystem, 2.5));
		driveStraight.whileHeld(new NavXDriveStraight(Robot.driveSubsystem.straightPID, Robot.driveSubsystem, this));
//		climbButton.whileHeld(new Climb(Robot.climberSubsystem));
//		toggleFlywheel.whenPressed(new org.usfirst.frc.team449.robot.mechanism.doubleflywheelshooter.commands.ToggleFlywheel(Robot.doubleFlywheelShooterSubsystem));
//		toggleFlywheel.whenPressed(new org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.commands.ToggleFlywheel(Robot.singleFlywheelShooterSubsystem));
		toggleIntake.whenPressed(new ToggleIntaking(Robot.intakeSubsystem));
		toggleIntakeUpDown.whenPressed(new ToggleIntakeUpDown(Robot.intakeSubsystem));
	}

	@Override
	public double getDriveAxisLeft() {
		if (Math.abs(leftThrottle.getValue()) > joystickDeadband)
			return -leftThrottle.getValue();
		return 0;
	}

	@Override
	public double getDriveAxisRight() {
		if (Math.abs(rightThrottle.getValue()) > joystickDeadband)
			return -rightThrottle.getValue();
		return 0;
	}

	public void checkDPad() {
		SmartDashboard.putNumber("D Pad", gamepad.getPOV());
		SmartDashboard.putNumber("D Pad 0", gamepad.getPOV(0));
		SmartDashboard.putNumber("D Pad 1", gamepad.getPOV(1));
		SmartDashboard.putNumber("D Pad 2", gamepad.getPOV(2));
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
