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

	//How much the D-pad moves the robot rotationally on a 0 to 1 scale, equivalent to pushing the turning stick that
	// much of the way.
	private static double SHIFT;

	//The throttle wrapper for the stick controlling turning velocity.
	private Throttle rotThrottle;

	//The throttle wrapper for the stick controlling linear velocity.
	private Throttle fwdThrottle;

	//The controller with the drive sticks.
	private Joystick gamepad;

	//The joystick output under which any input is considered noise.
	private double deadband;

	//Turn to 0, 30, 180, or 330 degrees relative to the position the robot was turned on at.
	private JoystickButton turnTo0;
	private JoystickButton turnTo30;
	private JoystickButton turnTo180;
	private JoystickButton turnTo330;

	//Turn 180 degrees relative to the robot's current position.
	private JoystickButton turnaround;

	//Gear switching for drive.
	private JoystickButton switchToLowGear;
	private JoystickButton switchToHighGear;

	//Run the climber while the button is held AND the current limit hasn't been reached.
	private JoystickButton climb;

	//Toggle whether we're overriding the NavX DriveStraight for slam alignment.
	private JoystickButton toggleOverrideNavX;

	//Switch which camera we're displaying on SmartDashboard.
	private JoystickButton switchCamera;

	//Switch whether or not the intake is sucking in balls.
	private JoystickButton toggleIntake;

	//Switch whether the intake is up or down.
	private JoystickButton toggleIntakeUpDown;

	//While this button is held, stay in low gear and don't autoshift.
	private JoystickButton tmpOverrideLow;

	//While this button is held, stay in high gear and don't autoshift.
	private JoystickButton tmpOverrideHigh;

	//Toggle overriding autoshifting. When autoshifting is turned off, switch to high gear.
	private JoystickButton toggleOverrideHigh;

	//Switch whether or not the feeder is feeding balls into the shooter.
	private JoystickButton toggleFeeder;

	//Toggle whether the flywheel is on or off.
	private JoystickButton toggleShooter;

	//Lower and run the intake while turning off the flywheel and feeder.
	private JoystickButton loadShooter;
	//Raise the intake and run the static one to agitate the ball bin. Run the flywheel to get it up to speed, but
	// don't run the feeder.
	private JoystickButton rackShooter;
	//Same as rack, but run the feeder in order to fire balls.
	private JoystickButton fireShooter;

	public OI2017ArcadeGamepad(OI2017ArcadeGamepadMap.OI2017ArcadeGamepad map) {
		//Instantiate stick and joysticks
		gamepad = new Joystick(map.getGamepad());
		rotThrottle = new SmoothedThrottle(gamepad, map.getGamepadLeftAxis(), map.getInvertRot());
		fwdThrottle = new SmoothedThrottle(gamepad, map.getGamepadRightAxis(), map.getInvertFwd());

		//Set up other map constants
		SHIFT = (map.getInvertDpad() ? -map.getDpadShift() : map.getDpadShift());
		deadband = map.getDeadband();

		//Instantiate mandatory buttons.
		toggleOverrideNavX = new MappedJoystickButton(map.getOverrideNavX());

		//Instantiate optional buttons.
		if (map.hasTurnTo0()) {
			turnTo0 = new MappedJoystickButton(map.getTurnTo0());
		}
		if (map.hasTurnTo30()) {
			turnTo30 = new MappedJoystickButton(map.getTurnTo30());
		}
		if (map.hasTurnTo180()) {
			turnTo180 = new MappedJoystickButton(map.getTurnTo180());
		}
		if (map.hasTurnTo330()) {
			turnTo330 = new MappedJoystickButton(map.getTurnTo330());
		}
		if (map.hasTurnaround()) {
			turnaround = new MappedJoystickButton(map.getTurnaround());
		}
		if (map.hasSwitchToLowGear()) {
			switchToLowGear = new MappedJoystickButton(map.getSwitchToLowGear());
			switchToHighGear = new MappedJoystickButton(map.getSwitchToHighGear());
		}
		if (map.hasClimb()) {
			climb = new MappedJoystickButton(map.getClimb());
		}
		if (map.hasTmpOverrideLow()) {
			tmpOverrideLow = new MappedJoystickButton(map.getTmpOverrideLow());
		}
		if (map.hasTmpOverrideHigh()) {
			tmpOverrideHigh = new MappedJoystickButton(map.getTmpOverrideHigh());
		}
		if (map.hasToggleOverrideHigh()) {
			toggleOverrideHigh = new MappedJoystickButton(map.getToggleOverrideHigh());
		}
		if (map.hasToggleFeeder()) {
			toggleFeeder = new MappedJoystickButton(map.getToggleFeeder());
		}
		if (map.hasToggleIntake()) {
			toggleIntake = new MappedJoystickButton(map.getToggleIntake());
		}
		if (map.hasToggleIntakeUpDown()) {
			toggleIntakeUpDown = new MappedJoystickButton(map.getToggleIntakeUpDown());
		}
		if (map.hasShoot()) {
			toggleShooter = new MappedJoystickButton(map.getShoot());
		}
		if (map.hasLoadShooter()) {
			loadShooter = new MappedJoystickButton(map.getLoadShooter());
		}
		if (map.hasRackShooter()) {
			rackShooter = new MappedJoystickButton(map.getRackShooter());
		}
		if (map.hasFireShooter()) {
			fireShooter = new MappedJoystickButton(map.getFireShooter());
		}
		if (map.hasSwitchCamera()) {
			switchCamera = new MappedJoystickButton(map.getSwitchCamera());
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
			//TODO put this number in the map
			final double ROT_SCALE = 0.2;
			//Scale based on rotational throttle for more responsive turning at high speed
			return fwdThrottle.getValue() * (1 - ROT_SCALE * rotThrottle.getValue());
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
			return gamepad.getPOV() < 180 ? SHIFT : -SHIFT;
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
		toggleOverrideNavX.whenPressed(new OverrideNavX(Robot.driveSubsystem));

		//Map drive commands
		if (turnaround != null) {
			turnaround.whenPressed(new NavXRelativeTTA(Robot.driveSubsystem.turnPID, 180, Robot.driveSubsystem,
					TIMEOUT));
		}
		if (turnTo0 != null) {
			turnTo0.whenPressed(new NavXTurnToAngle(Robot.driveSubsystem.turnPID, 0, Robot.driveSubsystem, TIMEOUT));
		}
		if (turnTo30 != null) {
			turnTo30.whenPressed(new NavXTurnToAngle(Robot.driveSubsystem.turnPID, 30, Robot.driveSubsystem, TIMEOUT));
		}
		if (turnTo180 != null) {
			turnTo180.whenPressed(new NavXTurnToAngle(Robot.driveSubsystem.turnPID, 180, Robot.driveSubsystem,
					TIMEOUT));
		}
		if (turnTo330 != null) {
			turnTo330.whenPressed(new NavXTurnToAngle(Robot.driveSubsystem.turnPID, -30, Robot.driveSubsystem,
					TIMEOUT));
		}
		if (Robot.driveSubsystem.shifter != null && switchToHighGear != null && switchToLowGear != null) {
			switchToHighGear.whenPressed(new SwitchToHighGear(Robot.driveSubsystem));
			switchToLowGear.whenPressed(new SwitchToLowGear(Robot.driveSubsystem));
		}
		if (tmpOverrideHigh != null) {
			tmpOverrideHigh.whenPressed(new OverrideAutoShift(Robot.driveSubsystem, true, false));
			tmpOverrideHigh.whenReleased(new OverrideAutoShift(Robot.driveSubsystem, false, false));
		}
		if (tmpOverrideLow != null) {
			tmpOverrideLow.whenPressed(new OverrideAutoShift(Robot.driveSubsystem, true, true));
			tmpOverrideLow.whenReleased(new OverrideAutoShift(Robot.driveSubsystem, false, true));
		}
		if (toggleOverrideHigh != null) {
			toggleOverrideHigh.whenPressed(new OverrideAutoShift(Robot.driveSubsystem, !Robot.driveSubsystem
					.overrideAutoShift, false));
		}

		//Map climber commands
		if (Robot.climberSubsystem != null && climb != null) {
			climb.whenPressed(new CurrentClimb(Robot.climberSubsystem));
			climb.whenReleased(new StopClimbing(Robot.climberSubsystem));
		}

		//Map camera commands
		if (Robot.cameraSubsystem != null && switchCamera != null) {
			switchCamera.whenPressed(new ChangeCam(Robot.cameraSubsystem, TIMEOUT));
		}

		//Map intake commands
		if (Robot.intakeSubsystem != null) {
			if (toggleIntakeUpDown != null) {
				toggleIntakeUpDown.whenPressed(new ToggleIntakeUpDown(Robot.intakeSubsystem));
			}
			if (toggleIntake != null) {
				toggleIntake.whenPressed(new ToggleIntaking(Robot.intakeSubsystem));
			}
		}

		//Map feeder commands
		if (toggleFeeder != null && Robot.feederSubsystem != null) {
			toggleFeeder.whenPressed(new ToggleFeeder(Robot.feederSubsystem));
		}

		//Map shooter commands
		if (toggleShooter != null && Robot.singleFlywheelShooterSubsystem != null) {
			toggleShooter.whenPressed(new ToggleShooter(Robot.singleFlywheelShooterSubsystem));
		}

		//Map group commands
		if (loadShooter != null) {
			loadShooter.whenPressed(new LoadShooter(Robot.singleFlywheelShooterSubsystem, Robot.intakeSubsystem, Robot
					.feederSubsystem));
		}
		if (rackShooter != null) {
			rackShooter.whenPressed(new RackShooter(Robot.singleFlywheelShooterSubsystem, Robot.intakeSubsystem, Robot
					.feederSubsystem));
		}
		if (fireShooter != null) {
			fireShooter.whenPressed(new FireShooter(Robot.singleFlywheelShooterSubsystem, Robot.intakeSubsystem, Robot
					.feederSubsystem));
		}
	}
}
