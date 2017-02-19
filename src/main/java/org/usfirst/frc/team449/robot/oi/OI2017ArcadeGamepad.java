package org.usfirst.frc.team449.robot.oi;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import maps.org.usfirst.frc.team449.robot.oi.OI2017ArcadeGamepadMap;
import org.usfirst.frc.team449.robot.Robot;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.NavXRelativeTTA;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.NavXTurnToAngle;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.OverrideAutoShift;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.OverrideNavX;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.SwitchToHighGear;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.SwitchToLowGear;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.ois.ArcadeOI;
import org.usfirst.frc.team449.robot.mechanism.climber.commands.CurrentClimb;
import org.usfirst.frc.team449.robot.mechanism.climber.commands.StopClimbing;
import org.usfirst.frc.team449.robot.mechanism.feeder.commands.ToggleFeeder;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.commands.ToggleIntakeUpDown;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.commands.ToggleIntaking;
import org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.commands.ToggleFlywheel;
import org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.commands.ToggleShooter;
import org.usfirst.frc.team449.robot.mechanism.topcommands.shooter.FireShooter;
import org.usfirst.frc.team449.robot.mechanism.topcommands.shooter.LoadShooter;
import org.usfirst.frc.team449.robot.mechanism.topcommands.shooter.RackShooter;
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
	private JoystickButton switchCamera, toggleIntake, toggleIntakeUpDown, tmpOverrideLow, tmpOverrideHigh, toggleOverrideHigh, toggleFeeder, shoot;
	private JoystickButton loadShooter, rackShooter, fireShooter;

	public OI2017ArcadeGamepad(OI2017ArcadeGamepadMap.OI2017ArcadeGamepad map) {
		//This is just to give the sticks better names and allow quickly swapping which is which according to driver preference.
		gamepad = new Joystick(map.getGamepad());
		SHIFT = (map.getInvertDpad() ? -map.getDpadShift() : map.getDpadShift());
		rotThrottle = new SmoothedThrottle(gamepad, map.getGamepadLeftAxis(), map.getInvertRot());
		fwdThrottle = new SmoothedThrottle(gamepad, map.getGamepadRightAxis(), map.getInvertFwd());
		deadband = map.getDeadband();

//		if (map.hasTurnTo0Button()) {
//			tt0 = new JoystickButton(gamepad, map.getTurnTo0Button());
//		}
//		if (map.hasTurnTo30Button()) {
//			tt30 = new JoystickButton(gamepad, map.getTurnTo30Button());
//		}
//		if (map.hasTurnTo180Button()) {
//			tt180 = new JoystickButton(gamepad, map.getTurnTo180Button());
//		}
//		if (map.hasTurnTo330Button()) {
//			tt330 = new JoystickButton(gamepad, map.getTurnTo330Button());
//		}
//		if(map.hasTurnaroundButton()) {
//			turnaround = new JoystickButton(gamepad, map.getTurnaroundButton());
//		}

		overrideNavX = new MJButton(map.getOverrideNavX());

		if (map.hasSwitchToLowGear()) {
			switchToLowGear = new MJButton(map.getSwitchToLowGear());
			switchToHighGear = new MJButton(map.getSwitchToHighGear());
		}
		if (map.hasClimb()) {
			climb = new MJButton(map.getClimb());
		}
//		if (map.hasSwitchCamera()) {
//			switchCamera = new JoystickButton(gamepad, map.getSwitchCamera());
//		}

		if (map.hasToggleOverrideLow()){
			tmpOverrideLow = new MJButton(map.getToggleOverrideLow());
		}
		if (map.hasToggleOverrideHigh()){
			tmpOverrideHigh = new MJButton(map.getToggleOverrideHigh());
		}
		if (map.hasToggleOverrideHigh()) {     // TODO check if tmp == toggle
			toggleOverrideHigh = new MJButton(map.getToggleOverrideHigh());
		}
		if (map.hasToggleFeeder()) {
			toggleFeeder = new MJButton(map.getToggleFeeder());
		}
		if (map.hasToggleIntake()) {
			toggleIntake = new MJButton(map.getToggleIntake());
		}
		if (map.hasToggleIntakeUpDown()){
			toggleIntakeUpDown = new MJButton(map.getToggleIntakeUpDown());
		}
		if (map.hasShoot()) {
			shoot = new MJButton(map.getShoot());
		}
		if (map.hasLoadShooter()) {
			loadShooter = new MJButton(map.getLoadShooter());
		}
		if (map.hasRackShooter()) {
			rackShooter = new MJButton(map.getRackShooter());
		}
		if (map.hasFireShooter()) {
			fireShooter = new MJButton(map.getFireShooter());
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
			tmpOverrideHigh.whenPressed(new OverrideAutoShift(Robot.driveSubsystem, true, false));
			tmpOverrideHigh.whenReleased(new OverrideAutoShift(Robot.driveSubsystem, false, false));
		}
		if (tmpOverrideLow != null){
			tmpOverrideLow.whenPressed(new OverrideAutoShift(Robot.driveSubsystem, true, true));
			tmpOverrideLow.whenReleased(new OverrideAutoShift(Robot.driveSubsystem, false, true));
		}
		if (toggleOverrideHigh != null){
			toggleOverrideHigh.whenPressed(new OverrideAutoShift(Robot.driveSubsystem, !Robot.driveSubsystem.overrideAutoShift, false));
		}
//		if (toggleIntake != null && Robot.intakeSubsystem != null){
//			toggleIntake.whenPressed(new ToggleIntaking(Robot.intakeSubsystem));
//		}
		if (toggleIntakeUpDown != null && Robot.intakeSubsystem != null){
			toggleIntakeUpDown.whenPressed(new ToggleIntakeUpDown(Robot.intakeSubsystem));
		}
		if (toggleFeeder != null && Robot.feederSubsystem != null){
			toggleFeeder.whenPressed(new ToggleFeeder(Robot.feederSubsystem));
		}
		if (shoot != null && Robot.singleFlywheelShooterSubsystem != null) {
			shoot.whenPressed(new ToggleShooter(Robot.singleFlywheelShooterSubsystem));
		}
		if (loadShooter != null) {
			loadShooter.whenPressed(new LoadShooter(Robot.singleFlywheelShooterSubsystem, Robot.intakeSubsystem, Robot.feederSubsystem));
		}
		if (rackShooter != null) {
			rackShooter.whenPressed(new RackShooter(Robot.singleFlywheelShooterSubsystem, Robot.intakeSubsystem, Robot.feederSubsystem));
		}
		if (fireShooter != null) {
			fireShooter.whenPressed(new FireShooter(Robot.singleFlywheelShooterSubsystem, Robot.intakeSubsystem, Robot.feederSubsystem));
		}
		overrideNavX.whenPressed(new OverrideNavX(Robot.driveSubsystem));
	}
}
