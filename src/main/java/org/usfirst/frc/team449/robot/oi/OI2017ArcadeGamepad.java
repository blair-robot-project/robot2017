package org.usfirst.frc.team449.robot.oi;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import maps.org.usfirst.frc.team449.robot.oi.OI2017ArcadeGamepadMap;
import org.usfirst.frc.team449.robot.Robot;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.*;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.ois.ArcadeOI;
import org.usfirst.frc.team449.robot.mechanism.climber.commands.CurrentClimb;
import org.usfirst.frc.team449.robot.mechanism.climber.commands.StopClimbing;
import org.usfirst.frc.team449.robot.mechanism.intake.commands.FakeIntakeUp;
import org.usfirst.frc.team449.robot.mechanism.intake.commands.ToggleIntakeUpDown;
import org.usfirst.frc.team449.robot.mechanism.intake.commands.ToggleIntaking;
import org.usfirst.frc.team449.robot.oi.components.SmoothedThrottle;
import org.usfirst.frc.team449.robot.oi.components.Throttle;
import org.usfirst.frc.team449.robot.vision.commands.ChangeCam;

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
	private JoystickButton tt0, tt30, tt180, tt330, turnaround, switchToLowGear, switchToHighGear, climb, overrideNavX;
	private JoystickButton switchCamera, toggleIntake, toggleIntakeUpDown, tmpOverrideLow, tmpOverrideHigh, toggleOverrideHigh;

	public OI2017ArcadeGamepad(OI2017ArcadeGamepadMap.OI2017ArcadeGamepad map) {
		//This is just to give the sticks better names and allow quickly swapping which is which according to driver preference.
		gamepad = new Joystick(map.getGamepad());
		SHIFT = (map.getInvertDpad() ? -map.getDpadShift() : map.getDpadShift());
		rotThrottle = new SmoothedThrottle(gamepad, map.getGamepadLeftAxis(), map.getInvertRot());
		fwdThrottle = new SmoothedThrottle(gamepad, map.getGamepadRightAxis(), map.getInvertFwd());
		deadband = map.getDeadband();

		if (map.hasTurnTo0Button()) {
			tt0 = new JoystickButton(gamepad, map.getTurnTo0Button());
		}
		if (map.hasTurnTo30Button()) {
			tt30 = new JoystickButton(gamepad, map.getTurnTo30Button());
		}
		if (map.hasTurnTo180Button()) {
			tt180 = new JoystickButton(gamepad, map.getTurnTo180Button());
		}
		if (map.hasTurnTo330Button()) {
			tt330 = new JoystickButton(gamepad, map.getTurnTo330Button());
		}
		if(map.hasTurnaroundButton()) {
			turnaround = new JoystickButton(gamepad, map.getTurnaroundButton());
		}

		overrideNavX = new JoystickButton(gamepad, map.getOverrideNavX());

		if (map.hasSwitchToLowGear()) {
			switchToLowGear = new JoystickButton(gamepad, map.getSwitchToLowGear());
			switchToHighGear = new JoystickButton(gamepad, map.getSwitchToHighGear());
		}
		if (map.hasClimb()) {
			climb = new JoystickButton(gamepad, map.getClimb());
		}
		if (map.hasSwitchCamera()) {
			switchCamera = new JoystickButton(gamepad, map.getSwitchCamera());
		}

		if (map.hasTmpOverrideLow()){
			tmpOverrideLow = new JoystickButton(gamepad, map.getTmpOverrideLow());
		}
		if (map.hasTmpOverrideHigh()){
			tmpOverrideHigh = new JoystickButton(gamepad, map.getTmpOverrideHigh());
		}
		if (map.hasToggleOverrideHigh()){
			toggleOverrideHigh = new JoystickButton(gamepad, map.getToggleOverrideHigh());
		}
		if (map.hasToggleIntake()){
			toggleIntake = new JoystickButton(gamepad, map.getToggleIntake());
		}
		if (map.hasToggleIntakeUpDown()){
			toggleIntakeUpDown = new JoystickButton(gamepad, map.getToggleIntakeUpDown());
		}
	}

	/**
	 * The output of the throttle controlling linear velocity, smoothed and adjusted according to what type of joystick it is.
	 *
	 * @return The processed stick output, sign-adjusted so 1 is forward and -1 is backwards.
	 */
	public double getFwd() {
		if (Math.abs(fwdThrottle.getValue()) > deadband) {
			return fwdThrottle.getValue();
		} else {
			return 0;
		}
	}

	/**
	 * Get the output of the D-pad or turning joystick, whichever is in use. If both are in use, the D-pad takes preference.
	 *
	 * @return The processed stick or D-pad output, sign-adjusted so 1 is right and -1 is left.
	 */
	public double getRot() {
		if (!(gamepad.getPOV() == -1 || gamepad.getPOV() % 180 == 0)) {
			return gamepad.getPOV() < 180 ? SHIFT : -SHIFT;
		} else if (Math.abs(rotThrottle.getValue()) > deadband) {
			return rotThrottle.getValue();
		} else {
			return 0;
		}
	}

	public void mapButtons() {
		double timeout = 5.;
		if (turnaround != null) {
			turnaround.whenPressed(new NavXRelativeTTA(Robot.driveSubsystem.turnPID, 180, Robot.driveSubsystem, timeout));
		}
		if (tt0 != null) {
			tt0.whenPressed(new NavXTurnToAngle(Robot.driveSubsystem.turnPID, 0, Robot.driveSubsystem, timeout));
		}
		if (tt30 != null) {
			tt30.whenPressed(new NavXTurnToAngle(Robot.driveSubsystem.turnPID, 30, Robot.driveSubsystem, timeout));
		}
		if (tt180 != null) {
			tt180.whenPressed(new NavXTurnToAngle(Robot.driveSubsystem.turnPID, 180, Robot.driveSubsystem, timeout));
		}
		if (tt330 != null) {
			tt330.whenPressed(new NavXTurnToAngle(Robot.driveSubsystem.turnPID, -30, Robot.driveSubsystem, timeout));
		}
		if (Robot.driveSubsystem.shifter != null) {
			switchToHighGear.whenPressed(new SwitchToHighGear(Robot.driveSubsystem));
			switchToLowGear.whenPressed(new SwitchToLowGear(Robot.driveSubsystem));
		}
		if (Robot.climberSubsystem != null) {
			climb.whenPressed(new CurrentClimb(Robot.climberSubsystem));
			climb.whenReleased(new StopClimbing(Robot.climberSubsystem));
		}
		if (Robot.cameraSubsystem != null) {
			switchCamera.whenPressed(new ChangeCam(Robot.cameraSubsystem, timeout));
		}
		if (tmpOverrideHigh != null){
			tmpOverrideHigh.whenPressed(new SwitchToHighGear(Robot.driveSubsystem));
			tmpOverrideHigh.whenPressed(new OverrideAutoShift(Robot.driveSubsystem, true));
			tmpOverrideHigh.whenReleased(new OverrideAutoShift(Robot.driveSubsystem, false));
		}
		if (tmpOverrideLow != null){
			tmpOverrideLow.whenPressed(new SwitchToLowGear(Robot.driveSubsystem));
			tmpOverrideLow.whenPressed(new OverrideAutoShift(Robot.driveSubsystem, true));
			tmpOverrideLow.whenReleased(new OverrideAutoShift(Robot.driveSubsystem, false));
		}
		if (toggleOverrideHigh != null){
			toggleOverrideHigh.whenPressed(new SwitchToHighGear(Robot.driveSubsystem));
			toggleOverrideHigh.whenPressed(new OverrideAutoShift(Robot.driveSubsystem, !Robot.driveSubsystem.overrideAutoShift));
		}
		if (toggleIntake != null && Robot.intakeSubsystem != null){
			toggleIntake.whenPressed(new ToggleIntaking(Robot.intakeSubsystem));
		}
		if (toggleIntakeUpDown != null && Robot.intakeSubsystem != null){
			toggleIntakeUpDown.whenPressed(new ToggleIntakeUpDown(Robot.intakeSubsystem));
		}
		overrideNavX.whenPressed(new OverrideNavX(Robot.driveSubsystem));
	}
}
