package org.usfirst.frc.team449.robot;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.wpi.first.wpilibj.command.Command;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.components.MappedDigitalInput;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;
import org.usfirst.frc.team449.robot.interfaces.oi.UnidirectionalOI;
import org.usfirst.frc.team449.robot.mechanism.activegear.ActiveGearSubsystem;
import org.usfirst.frc.team449.robot.mechanism.climber.ClimberSubsystem;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.Intake2017;
import org.usfirst.frc.team449.robot.mechanism.pneumatics.PneumaticsSubsystem;
import org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.SingleFlywheelShooter;
import org.usfirst.frc.team449.robot.oi.buttons.CommandButton;
import org.usfirst.frc.team449.robot.util.Logger;
import org.usfirst.frc.team449.robot.util.MotionProfileData;
import org.usfirst.frc.team449.robot.util.YamlCommand;
import org.usfirst.frc.team449.robot.vision.CameraSubsystem;

import java.util.List;
import java.util.Map;

/**
 * The Jackson-compatible object representing the entire robot.
 */
public class RobotMap {

	/**
	 * The buttons for controlling this robot.
	 */
	@NotNull
	private final List<CommandButton> buttons;

	/**
	 * The OI for controlling this robot's drive.
	 */
	@NotNull
	private final UnidirectionalOI oi;

	/**
	 * The logger for recording events and telemetry data.
	 */
	@NotNull
	private final Logger logger;

	/**
	 * The drive.
	 */
	@NotNull
	private final TalonClusterDrive drive;

	/**
	 * The command for the drive to run during the teleoperated period.
	 */
	@NotNull
	private final Command defaultDriveCommand;

	/**
	 * The climber for boarding the airship. Can be null.
	 */
	@Nullable
	private final ClimberSubsystem climber;

	/**
	 * The shooter for shooting fuel. Can be null.
	 */
	@Nullable
	private final SingleFlywheelShooter shooter;

	/**
	 * The cameras on this robot. Can be null.
	 */
	@Nullable
	private final CameraSubsystem camera;

	/**
	 * The intake for picking up and agitating balls. Can be null.
	 */
	@Nullable
	private final Intake2017 intake;

	/**
	 * The pneumatics on this robot. Can be null.
	 */
	@Nullable
	private final PneumaticsSubsystem pneumatics;

	/**
	 * The gear handler on this robot. Can be null.
	 */
	@Nullable
	private final ActiveGearSubsystem gearHandler;

	/**
	 * The I2C port of the RIOduino plugged into this robot. Can be null.
	 */
	@Nullable
	private final Integer RIOduinoPort;

	/**
	 * The switch for selecting which alliance we're on. Can be null if doMP is false or testMP is true, but otherwise
	 * must have a value.
	 */
	@Nullable
	private final MappedDigitalInput allianceSwitch;

	/**
	 * The switch for deciding whether or not to drop the gear. Can be null if doMP is false or testMP is true, but
	 * otherwise must have a value.
	 */
	@Nullable
	private final MappedDigitalInput dropGearSwitch;

	/**
	 * The dial for selecting which side of the field the robot is on. Can be null if doMP is false or testMP is true,
	 * but otherwise must have a value.
	 */
	@Nullable
	private final MappedDigitalInput locationDial;

	/**
	 * The command to run in autonomous on the boiler side of the field. Can be null if doMP is false or testMP is true,
	 * but otherwise must have a value.
	 */
	@Nullable
	private final Command boilerAuto;

	/**
	 * The command to run in autonomous on the center of the field. Can be null if doMP is false or testMP is true, but
	 * otherwise must have a value.
	 */
	@Nullable
	private final Command centerAuto;

	/**
	 * The command to run in autonomous on the feeding station side of the field. Can be null if doMP is false or testMP
	 * is true, but otherwise must have a value.
	 */
	@Nullable
	private final Command feederAuto;

	/**
	 * The profile for the left side of the drive to run in test mode. Can be null if either testMP or doMP are false,
	 * but otherwise must have a value.
	 */
	@Nullable
	private final MotionProfileData leftTestProfile;

	/**
	 * The profile for the right side of the drive to run in test mode. Can be null if either testMP or doMP are false,
	 * but otherwise must have a value.
	 */
	@Nullable
	private final MotionProfileData rightTestProfile;

	/**
	 * The starting position to peg profiles for the left side. Should have options for "red_right", "red_center",
	 * "red_left", "blue_right", "blue_center", and "blue_left". Can be null if doMP is false or testMP is true, but
	 * otherwise must have a value.
	 */
	@Nullable
	private final Map<String, MotionProfileData> leftProfiles;

	/**
	 * The starting position to peg profiles for the right side. Should have options for "red_right", "red_center",
	 * "red_left", "blue_right", "blue_center", and "blue_left". Can be null if doMP is false or testMP is true, but
	 * otherwise must have a value.
	 */
	@Nullable
	private final Map<String, MotionProfileData> rightProfiles;

	/**
	 * The command to run during autonomous if doMP is false. Can be null, and if it is, no command is run during
	 * autonomous.
	 */
	@Nullable
	private final Command nonMPAutoCommand;

	/**
	 * Whether to run the test or real motion profile during autonomous.
	 */
	private final boolean testMP;

	/**
	 * Whether to run a motion profile during autonomous.
	 */
	private final boolean doMP;

	/**
	 * Default constructor.
	 *
	 * @param buttons             The buttons for controlling this robot.
	 * @param oi                  The OI for controlling this robot's drive.
	 * @param logger              The logger for recording events and telemetry data.
	 * @param drive               The drive.
	 * @param defaultDriveCommand The command for the drive to run during the teleoperated period.
	 * @param climber             The climber for boarding the airship. Can be null.
	 * @param shooter             The shooter for shooting fuel. Can be null.
	 * @param camera              The cameras on this robot. Can be null.
	 * @param intake              The intake for picking up and agitating balls. Can be null.
	 * @param pneumatics          The pneumatics on this robot. Can be null.
	 * @param gearHandler         The gear handler on this robot. Can be null.
	 * @param RIOduinoPort        The I2C port of the RIOduino plugged into this robot. Can be null.
	 * @param allianceSwitch      The switch for selecting which alliance we're on. Can be null if doMP is false or
	 *                            testMP is true, but otherwise must have a value.
	 * @param dropGearSwitch      The switch for deciding whether or not to drop the gear. Can be null if doMP is false
	 *                            or testMP is true, but otherwise must have a value.
	 * @param locationDial        The dial for selecting which side of the field the robot is on. Can be null if doMP is
	 *                            false or testMP is true, but otherwise must have a value.
	 * @param boilerAuto          The command to run in autonomous on the boiler side of the field. Can be null if doMP
	 *                            is false or testMP is true, but otherwise must have a value.
	 * @param centerAuto          The command to run in autonomous on the center of the field. Can be null if doMP is
	 *                            false or testMP is true, but otherwise must have a value.
	 * @param feederAuto          The command to run in autonomous on the feeding station side of the field. Can be null
	 *                            if doMP is false or testMP is true, but otherwise must have a value.
	 * @param leftTestProfile     The profile for the left side of the drive to run in test mode. Can be null if either
	 *                            testMP or doMP are false, but otherwise must have a value.
	 * @param rightTestProfile    The profile for the right side of the drive to run in test mode. Can be null if either
	 *                            testMP or doMP are false, but otherwise must have a value.
	 * @param leftProfiles        The starting position to peg profiles for the left side. Should have options for
	 *                            "red_right", "red_center", "red_left", "blue_right", "blue_center", and "blue_left".
	 *                            Can be null if doMP is false or testMP is true, but otherwise must have a value.
	 * @param rightProfiles       The starting position to peg profiles for the right side. Should have options for
	 *                            "red_right", "red_center", "red_left", "blue_right", "blue_center", and "blue_left".
	 *                            Can be null if doMP is false or testMP is true, but otherwise must have a value.
	 * @param nonMPAutoCommand    The command to run during autonomous if doMP is false. Can be null, and if it is, no
	 *                            command is run during autonomous.
	 * @param testMP              Whether to run the test or real motion profile during autonomous. Defaults to false.
	 * @param doMP                Whether to run a motion profile during autonomous. Defaults to true.
	 */
	@JsonCreator
	public RobotMap(@NotNull @JsonProperty(required = true) List<CommandButton> buttons,
	                @NotNull @JsonProperty(required = true) UnidirectionalOI oi,
	                @NotNull @JsonProperty(required = true) Logger logger,
	                @NotNull @JsonProperty(required = true) TalonClusterDrive drive,
	                @NotNull @JsonProperty(required = true) YamlCommand defaultDriveCommand,
	                @Nullable ClimberSubsystem climber,
	                @Nullable SingleFlywheelShooter shooter,
	                @Nullable CameraSubsystem camera,
	                @Nullable Intake2017 intake,
	                @Nullable PneumaticsSubsystem pneumatics,
	                @Nullable ActiveGearSubsystem gearHandler,
	                @Nullable Integer RIOduinoPort,
	                @Nullable MappedDigitalInput allianceSwitch,
	                @Nullable MappedDigitalInput dropGearSwitch,
	                @Nullable MappedDigitalInput locationDial,
	                @Nullable YamlCommand boilerAuto,
	                @Nullable YamlCommand centerAuto,
	                @Nullable YamlCommand feederAuto,
	                @Nullable MotionProfileData leftTestProfile, @Nullable MotionProfileData rightTestProfile,
	                @Nullable Map<String, MotionProfileData> leftProfiles, @Nullable Map<String, MotionProfileData> rightProfiles,
	                @Nullable YamlCommand nonMPAutoCommand,
	                boolean testMP,
	                @Nullable Boolean doMP) {
		this.buttons = buttons;
		this.oi = oi;
		this.drive = drive;
		this.climber = climber;
		this.shooter = shooter;
		this.camera = camera;
		this.intake = intake;
		this.pneumatics = pneumatics;
		this.gearHandler = gearHandler;
		this.logger = logger;
		this.RIOduinoPort = RIOduinoPort;
		this.allianceSwitch = allianceSwitch;
		this.dropGearSwitch = dropGearSwitch;
		this.locationDial = locationDial;
		this.boilerAuto = boilerAuto != null ? boilerAuto.getCommand() : null;
		this.centerAuto = centerAuto != null ? centerAuto.getCommand() : null;
		this.feederAuto = feederAuto != null ? feederAuto.getCommand() : null;
		this.leftTestProfile = leftTestProfile;
		this.rightTestProfile = rightTestProfile;
		this.leftProfiles = leftProfiles;
		this.rightProfiles = rightProfiles;
		this.defaultDriveCommand = defaultDriveCommand.getCommand();
		if (nonMPAutoCommand != null) {
			this.nonMPAutoCommand = nonMPAutoCommand.getCommand();
		} else {
			this.nonMPAutoCommand = null;
		}
		this.testMP = testMP;
		if (doMP == null) {
			doMP = true;
		}
		this.doMP = doMP;
	}

	/**
	 * @return The buttons for controlling this robot.
	 */
	@NotNull
	public List<CommandButton> getButtons() {
		return buttons;
	}

	/**
	 * @return The OI for controlling this robot's drive.
	 */
	@NotNull
	public UnidirectionalOI getOI() {
		return oi;
	}

	/**
	 * @return The logger for recording events and telemetry data.
	 */
	@NotNull
	public Logger getLogger() {
		return logger;
	}

	/**
	 * @return The drive.
	 */
	@NotNull
	public TalonClusterDrive getDrive() {
		return drive;
	}

	/**
	 * @return The command for the drive to run during the teleoperated period.
	 */
	@NotNull
	public Command getDefaultDriveCommand() {
		return defaultDriveCommand;
	}

	/**
	 * @return The climber for boarding the airship. Can be null.
	 */
	@Nullable
	public ClimberSubsystem getClimber() {
		return climber;
	}

	/**
	 * @return The shooter for shooting fuel. Can be null.
	 */
	@Nullable
	public SingleFlywheelShooter getShooter() {
		return shooter;
	}

	/**
	 * @return The cameras on this robot. Can be null.
	 */
	@Nullable
	public CameraSubsystem getCamera() {
		return camera;
	}

	/**
	 * @return The intake for picking up and agitating balls. Can be null.
	 */
	@Nullable
	public Intake2017 getIntake() {
		return intake;
	}

	/**
	 * @return The pneumatics on this robot. Can be null.
	 */
	@Nullable
	public PneumaticsSubsystem getPneumatics() {
		return pneumatics;
	}

	/**
	 * @return The gear handler on this robot. Can be null.
	 */
	@Nullable
	public ActiveGearSubsystem getGearHandler() {
		return gearHandler;
	}

	/**
	 * @return The I2C port of the RIOduino plugged into this robot. Can be null.
	 */
	@Nullable
	public Integer getRIOduinoPort() {
		return RIOduinoPort;
	}

	/**
	 * @return The switch for selecting which alliance we're on. Can be null if getDoMP returns false or getTestMP
	 * returns true, but otherwise has a value.
	 */
	@Nullable
	public MappedDigitalInput getAllianceSwitch() {
		return allianceSwitch;
	}

	/**
	 * @return The switch for deciding whether or not to drop the gear. Can be null if getDoMP returns false or
	 * getTestMP returns true, but otherwise has a value.
	 */
	@Nullable
	public MappedDigitalInput getDropGearSwitch() {
		return dropGearSwitch;
	}

	/**
	 * @return The dial for selecting which side of the field the robot is on. Can be null if getDoMP returns false or
	 * getTestMP returns true, but otherwise has a value.
	 */
	@Nullable
	public MappedDigitalInput getLocationDial() {
		return locationDial;
	}

	/**
	 * @return The command to run in autonomous on the boiler side of the field. Can be null if getDoMP returns false or
	 * getTestMP returns true, but otherwise has a value.
	 */
	@Nullable
	public Command getBoilerAuto() {
		return boilerAuto;
	}

	/**
	 * @return The command to run in autonomous on the center of the field. Can be null if getDoMP returns false or
	 * getTestMP returns true, but otherwise has a value.
	 */
	@Nullable
	public Command getCenterAuto() {
		return centerAuto;
	}

	/**
	 * @return The command to run in autonomous on the feeding station side of the field. Can be null if getDoMP returns
	 * false or getTestMP returns true, but otherwise has a value.
	 */
	@Nullable
	public Command getFeederAuto() {
		return feederAuto;
	}

	/**
	 * @return The profile for the left side of the drive to run in test mode. Can be null if either getTestMP or
	 * getDoMP return false, but otherwise has a value.
	 */
	@Nullable
	public MotionProfileData getLeftTestProfile() {
		return leftTestProfile;
	}

	/**
	 * @return The profile for the right side of the drive to run in test mode. Can be null if either getTestMP or
	 * getDoMP return false, but otherwise has a value.
	 */
	@Nullable
	public MotionProfileData getRightTestProfile() {
		return rightTestProfile;
	}

	/**
	 * @return The starting position to peg profiles for the left side. Can be null if getDoMP returns false or
	 * getTestMP returns true, but otherwise has a value. If not null, has values for the keys "red_right",
	 * "red_center", "red_left", "blue_right", "blue_center", and "blue_left".
	 */
	@Nullable
	public Map<String, MotionProfileData> getLeftProfiles() {
		return leftProfiles;
	}

	/**
	 * @return The starting position to peg profiles for the right side. Can be null if getDoMP returns false or
	 * getTestMP returns true, but otherwise has a value. If not null, has values for the keys "red_right",
	 * "red_center", "red_left", "blue_right", "blue_center", and "blue_left".
	 */
	@Nullable
	public Map<String, MotionProfileData> getRightProfiles() {
		return rightProfiles;
	}

	/**
	 * @return The command to run during autonomous if doMP is false. Can be null.
	 */
	@Nullable
	public Command getNonMPAutoCommand() {
		return nonMPAutoCommand;
	}

	/**
	 * @return Whether to run the test or real motion profile during autonomous.
	 */
	public boolean getTestMP() {
		return testMP;
	}

	/**
	 * @return Whether to run a motion profile during autonomous.
	 */
	public boolean getDoMP() {
		return doMP;
	}
}
