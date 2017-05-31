package org.usfirst.frc.team449.robot.oi;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import maps.org.usfirst.frc.team449.robot.oi.JoystickButtonMap;
import maps.org.usfirst.frc.team449.robot.oi.OI2017ArcadeGamepadMap;
import org.usfirst.frc.team449.robot.Robot;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.JiggleRobot;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.NavXRelativeTTA;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.NavXTurnToAngle;
import org.usfirst.frc.team449.robot.interfaces.drive.shifting.ShiftingDrive;
import org.usfirst.frc.team449.robot.interfaces.drive.shifting.commands.*;
import org.usfirst.frc.team449.robot.interfaces.oi.ArcadeOI;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Intake.IntakeSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Intake.commands.ToggleIntaking;
import org.usfirst.frc.team449.robot.interfaces.subsystem.NavX.commands.OverrideNavX;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter.commands.ToggleShooting;
import org.usfirst.frc.team449.robot.interfaces.subsystem.binaryMotor.commands.TurnMotorOff;
import org.usfirst.frc.team449.robot.interfaces.subsystem.binaryMotor.commands.TurnMotorOffWithRequires;
import org.usfirst.frc.team449.robot.interfaces.subsystem.binaryMotor.commands.TurnMotorOn;
import org.usfirst.frc.team449.robot.interfaces.subsystem.solenoid.commands.SolenoidForward;
import org.usfirst.frc.team449.robot.interfaces.subsystem.solenoid.commands.SolenoidReverse;
import org.usfirst.frc.team449.robot.interfaces.subsystem.solenoid.commands.ToggleSolenoid;
import org.usfirst.frc.team449.robot.mechanism.climber.commands.RunMotorWhileConditonMet;
import org.usfirst.frc.team449.robot.mechanism.topcommands.shooter.FireShooter;
import org.usfirst.frc.team449.robot.mechanism.topcommands.shooter.LoadShooter;
import org.usfirst.frc.team449.robot.mechanism.topcommands.shooter.RackShooter;
import org.usfirst.frc.team449.robot.mechanism.topcommands.shooter.ResetShooter;
import org.usfirst.frc.team449.robot.oi.buttons.MappedJoystickButton;
import org.usfirst.frc.team449.robot.oi.components.SmoothedThrottle;
import org.usfirst.frc.team449.robot.oi.components.Throttle;
import org.usfirst.frc.team449.robot.util.Logger;
import org.usfirst.frc.team449.robot.vision.commands.ChangeCam;

import java.util.ArrayList;
import java.util.List;

/**
 * An OI for using an Xbox-style controller for an arcade drive, where one stick controls forward velocity and the other
 * controls turning velocity.
 */
public class OI2017ArcadeGamepad extends ArcadeOI {

	/**
	 * How much the D-pad moves the robot rotationally on a 0 to 1 scale, equivalent to pushing the turning stick that
	 * much of the way
	 */
	private double shift;

	/**
	 * The throttle wrapper for the stick controlling turning velocity
	 */
	private Throttle rotThrottle;

	/**
	 * The throttle wrapper for the stick controlling linear velocity
	 */
	private Throttle fwdThrottle;

	/**
	 * The controller with the drive sticks
	 */
	private Joystick gamepad;

	/**
	 * The joystick output under which any input is considered noise
	 */
	private double deadband;

	/**
	 * Button for turning to 0 degrees absolute heading (where robot was powered on)
	 */
	private Button turnTo0;

	/**
	 * Button for turning to 30 degrees absolute heading (where robot was powered on)
	 */
	private Button turnTo30;

	/**
	 * Button for turning to 180 degrees absolute heading (where robot was powered on)
	 */
	private Button turnTo180;

	/**
	 * Button for turning to 330 degrees absolute heading (where robot was powered on)
	 */
	private Button turnTo330;

	/**
	 * Button for a 180 degree relative turn
	 */
	private Button turnaround;

	/**
	 * Button for switching to low gear
	 */
	private Button switchToLowGear;

	/**
	 * Button for switching to high gear
	 */
	private Button switchToHighGear;

	/**
	 * Button for climbing
	 */
	private Button climb;

	/**
	 * Button for toggling whether to use NavX DriveStraight
	 */
	private Button toggleOverrideNavX;

	/**
	 * Button for toggling cameras
	 */
	private Button switchCamera;

	/**
	 * Button for toggling intake on/off
	 */
	private Button toggleIntake;

	/**
	 * Button for toggling intake up/down
	 */
	private Button toggleIntakeUpDown;

	/**
	 * Button held to stay in low gear
	 */
	private Button tmpOverrideLow;

	/**
	 * Button held to stay in high gear
	 */
	private Button tmpOverrideHigh;

	/**
	 * Button for toggling autoshifting
	 */
	private Button toggleOverrideAutoshift;

	/**
	 * Button for toggling shooter
	 */
	private Button toggleShooter;

	/**
	 * Button for running the LoadShooter command group (intake balls)
	 */
	private Button loadShooter;

	/**
	 * Button for running the RackShooter command group (set up to shoot)
	 */
	private Button rackShooter;

	/**
	 * Button for running the FireShooter command group (fire the shooter)
	 */
	private Button fireShooter;

	/**
	 * Button for running the ResetShooter command group (turn everything off)
	 */
	private Button resetShooter;

	/**
	 * Button for toggling whether the gear handler is open or closed.
	 */
	private Button toggleGear;

	/**
	 * Buttons that open the gear handler when pressed and close it when released.
	 */
	private List<Button> pushGear;

	/**
	 * Button that, while held, climbs without a current limit.
	 */
	private Button manualClimb;

	/**
	 * A button that logs an error to the log file.
	 */
	private Button logError;

	/**
	 * A button that turns the robot back and forth 10 degrees to un-jam the feeder.
	 */
	private Button jiggleRobot;

	/**
	 * Whether to override the NavX while the button is held or while it is not held.
	 */
	private boolean overrideNavXWhileHeld;

	/**
	 * Scaling, from [0, 1], that the rotational throttle decreases the forwards throttle by. Used so that turning while
	 * at high speed still has an impact.
	 */
	private double rotScale;

	/**
	 * Construct the OI2017ArcadeGamepad
	 *
	 * @param map config map
	 */
	public OI2017ArcadeGamepad(OI2017ArcadeGamepadMap.OI2017ArcadeGamepad map) {
		//Instantiate stick and joysticks
		gamepad = new Joystick(map.getGamepad());
		rotThrottle = new SmoothedThrottle(gamepad, map.getGamepadLeftAxis(), map.getInvertRot());
		fwdThrottle = new SmoothedThrottle(gamepad, map.getGamepadRightAxis(), map.getInvertFwd());

		//Set up other map constants
		shift = (map.getInvertDpad() ? -map.getDpadShift() : map.getDpadShift());
		deadband = map.getDeadband();
		rotScale = map.getScaleFwdByRotCoefficient();

		//Instantiate mandatory buttons.
		toggleOverrideNavX = MappedJoystickButton.constructButton(map.getOverrideNavX());
		overrideNavXWhileHeld = map.getOverrideNavXWhileHeld();

		//Instantiate optional buttons.
		if (map.hasTurnTo0()) {
			turnTo0 = MappedJoystickButton.constructButton(map.getTurnTo0());
		}
		if (map.hasTurnTo30()) {
			turnTo30 = MappedJoystickButton.constructButton(map.getTurnTo30());
		}
		if (map.hasTurnTo180()) {
			turnTo180 = MappedJoystickButton.constructButton(map.getTurnTo180());
		}
		if (map.hasTurnTo330()) {
			turnTo330 = MappedJoystickButton.constructButton(map.getTurnTo330());
		}
		if (map.hasTurnaround()) {
			turnaround = MappedJoystickButton.constructButton(map.getTurnaround());
		}
		if (map.hasSwitchToLowGear()) {
			switchToLowGear = MappedJoystickButton.constructButton(map.getSwitchToLowGear());
			switchToHighGear = MappedJoystickButton.constructButton(map.getSwitchToHighGear());
		}
		if (map.hasClimb()) {
			climb = MappedJoystickButton.constructButton(map.getClimb());
		}

		if (map.hasTmpOverrideLow()) {
			tmpOverrideLow = MappedJoystickButton.constructButton(map.getTmpOverrideLow());
		}
		if (map.hasTmpOverrideHigh()) {
			tmpOverrideHigh = MappedJoystickButton.constructButton(map.getTmpOverrideHigh());
		}
		if (map.hasToggleOverrideAutoshift()) {
			toggleOverrideAutoshift = MappedJoystickButton.constructButton(map.getToggleOverrideAutoshift());
		}
		if (map.hasToggleIntake()) {
			toggleIntake = MappedJoystickButton.constructButton(map.getToggleIntake());
		}
		if (map.hasToggleIntakeUpDown()) {
			toggleIntakeUpDown = MappedJoystickButton.constructButton(map.getToggleIntakeUpDown());
		}
		if (map.hasShoot()) {
			toggleShooter = MappedJoystickButton.constructButton(map.getShoot());
		}
		if (map.hasLoadShooter()) {
			loadShooter = MappedJoystickButton.constructButton(map.getLoadShooter());
		}
		if (map.hasRackShooter()) {
			rackShooter = MappedJoystickButton.constructButton(map.getRackShooter());
		}
		if (map.hasFireShooter()) {
			fireShooter = MappedJoystickButton.constructButton(map.getFireShooter());
		}
		if (map.hasResetShooter()) {
			resetShooter = MappedJoystickButton.constructButton(map.getResetShooter());
		}
		if (map.hasSwitchCamera()) {
			switchCamera = MappedJoystickButton.constructButton(map.getSwitchCamera());
		}

		if (map.hasToggleGear()) {
			toggleGear = MappedJoystickButton.constructButton(map.getToggleGear());
		}
		if (map.hasManualClimb()) {
			manualClimb = MappedJoystickButton.constructButton(map.getManualClimb());
		}
		if (map.hasLogError()) {
			logError = MappedJoystickButton.constructButton(map.getLogError());
		}
		if (map.hasJiggleRobot()) {
			jiggleRobot = MappedJoystickButton.constructButton(map.getJiggleRobot());
		}

		//We may have multiple push gear buttons
		pushGear = new ArrayList<>();
		for (JoystickButtonMap.JoystickButton button : map.getPushGearList()) {
			Button tmp = MappedJoystickButton.constructButton(button);
			pushGear.add(tmp);
		}
	}

	/**
	 * The output of the throttle controlling linear velocity, smoothed and adjusted according to what type of
	 * joystick it is.
	 *
	 * @return The processed stick output, sign-adjusted so 1 is forward and -1 is backwards.
	 */
	public double getFwd() {
		//If the value is outside of the deadband
		if (Math.abs(fwdThrottle.getValue()) > deadband) {
			//Scale based on rotational throttle for more responsive turning at high speed
			return fwdThrottle.getValue() * (1 - rotScale * rotThrottle.getValue());
		} else {
			return 0;
		}
	}

	/**
	 * Get the output of the D-pad or turning joystick, whichever is in use. If both are in use, the D-pad takes
	 * preference.
	 *
	 * @return The processed stick or D-pad output, sign-adjusted so 1 is right and -1 is left.
	 */
	public double getRot() {
		//If the gamepad is being pushed to the left or right
		if (!(gamepad.getPOV() == -1 || gamepad.getPOV() % 180 == 0)) {
			//Output the shift value
			return gamepad.getPOV() < 180 ? shift : -shift;
		} else if (Math.abs(rotThrottle.getValue()) > deadband) {
			//Return the throttle value if it's outside of the deadband.
			return rotThrottle.getValue();
		} else {
			return 0;
		}
	}

	/**
	 * Map all buttons to commands. Should only be run after all subsystems have been instantiated.
	 */
	public void mapButtons() {
		//The timeout for turning commands
		final double TIMEOUT = 5.;

		//Map mandatory commands
		toggleOverrideNavX.whenPressed(new OverrideNavX(Robot.instance.driveSubsystem, overrideNavXWhileHeld));
		toggleOverrideNavX.whenReleased(new OverrideNavX(Robot.instance.driveSubsystem, !overrideNavXWhileHeld));

		//Map drive commands
		if (turnaround != null) {
			turnaround.whenPressed(new NavXRelativeTTA(Robot.instance.driveSubsystem.turnPID, 180, Robot.instance.driveSubsystem,
					TIMEOUT));
		}
		if (turnTo0 != null) {
			turnTo0.whenPressed(new NavXTurnToAngle(Robot.instance.driveSubsystem.turnPID, 0, Robot.instance.driveSubsystem, TIMEOUT));
		}
		if (turnTo30 != null) {
			turnTo30.whenPressed(new NavXTurnToAngle(Robot.instance.driveSubsystem.turnPID, 30, Robot.instance.driveSubsystem, TIMEOUT));
		}
		if (turnTo180 != null) {
			turnTo180.whenPressed(new NavXTurnToAngle(Robot.instance.driveSubsystem.turnPID, 180, Robot.instance.driveSubsystem,
					TIMEOUT));
		}
		if (turnTo330 != null) {
			turnTo330.whenPressed(new NavXTurnToAngle(Robot.instance.driveSubsystem.turnPID, -30, Robot.instance.driveSubsystem,
					TIMEOUT));
		}
		if (switchToHighGear != null && switchToLowGear != null) {
			switchToHighGear.whenPressed(new SwitchToHighGear(Robot.instance.driveSubsystem));
			switchToLowGear.whenPressed(new SwitchToLowGear(Robot.instance.driveSubsystem));
		}
		if (tmpOverrideHigh != null) {
			tmpOverrideHigh.whenPressed(new OverrideAutoShift(Robot.instance.driveSubsystem, true));
			tmpOverrideHigh.whenPressed(new SwitchToGear(Robot.instance.driveSubsystem, ShiftingDrive.gear.HIGH));
			tmpOverrideHigh.whenReleased(new OverrideAutoShift(Robot.instance.driveSubsystem, false));
		}
		if (tmpOverrideLow != null) {
			tmpOverrideLow.whenPressed(new OverrideAutoShift(Robot.instance.driveSubsystem, true));
			tmpOverrideLow.whenPressed(new SwitchToGear(Robot.instance.driveSubsystem, ShiftingDrive.gear.LOW));
			tmpOverrideLow.whenReleased(new OverrideAutoShift(Robot.instance.driveSubsystem, false));
		}
		if (toggleOverrideAutoshift != null) {
			toggleOverrideAutoshift.whenPressed(new ToggleOverrideAutoShift(Robot.instance.driveSubsystem));
		}
		if (jiggleRobot != null) {
			jiggleRobot.whenPressed(new JiggleRobot(Robot.instance.driveSubsystem, Robot.instance.driveSubsystem.turnPID));
		}

		//Map climber commands
		if (Robot.instance.climberSubsystem != null) {
			if (climb != null) {
				climb.whenPressed(new RunMotorWhileConditonMet(Robot.instance.climberSubsystem));
				climb.whenReleased(new TurnMotorOffWithRequires(Robot.instance.climberSubsystem));
			}
			if (manualClimb != null) {
				manualClimb.whenPressed(new TurnMotorOn(Robot.instance.climberSubsystem));
				manualClimb.whenReleased(new TurnMotorOff(Robot.instance.climberSubsystem));
			}
		}

		//Map camera commands
		if (Robot.instance.cameraSubsystem != null && switchCamera != null) {
			switchCamera.whenPressed(new ChangeCam(Robot.instance.cameraSubsystem));
		}

		//Map intake commands
		if (Robot.instance.intakeSubsystem != null) {
			if (toggleIntakeUpDown != null) {
				toggleIntakeUpDown.whenPressed(new ToggleSolenoid(Robot.instance.intakeSubsystem));
			}
			if (toggleIntake != null) {
				toggleIntake.whenPressed(new ToggleIntaking(Robot.instance.intakeSubsystem, IntakeSubsystem.IntakeMode.IN_SLOW));
			}
		}

		//Map shooter commands
		if (toggleShooter != null && Robot.instance.singleFlywheelShooterSubsystem != null) {
			toggleShooter.whenPressed(new ToggleShooting(Robot.instance.singleFlywheelShooterSubsystem));
		}

		//Map group commands
		if (loadShooter != null) {
			loadShooter.whenPressed(new LoadShooter(Robot.instance.singleFlywheelShooterSubsystem, Robot.instance.intakeSubsystem));
		}
		if (rackShooter != null) {
			rackShooter.whenPressed(new RackShooter(Robot.instance.singleFlywheelShooterSubsystem, Robot.instance.intakeSubsystem));
		}
		if (fireShooter != null) {
			fireShooter.whenPressed(new FireShooter(Robot.instance.singleFlywheelShooterSubsystem, Robot.instance.intakeSubsystem));
		}
		if (resetShooter != null) {
			resetShooter.whenPressed(new ResetShooter(Robot.instance.singleFlywheelShooterSubsystem, Robot.instance.intakeSubsystem));
		}

		//Map gear handler commands
		if (Robot.instance.gearSubsystem != null) {
			if (toggleGear != null) {
				toggleGear.whenPressed(new ToggleSolenoid(Robot.instance.gearSubsystem));
			}
			for (Button button : pushGear) {
				button.whenPressed(new SolenoidReverse(Robot.instance.gearSubsystem));
				button.whenReleased(new SolenoidForward(Robot.instance.gearSubsystem));
			}
		}

		//Map miscellaneous commands
		if (logError != null) {
			Logger.addEvent("User pressed the error button!", this.getClass());
		}
	}
}
