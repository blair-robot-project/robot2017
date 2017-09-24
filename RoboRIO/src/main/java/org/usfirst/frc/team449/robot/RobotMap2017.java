package org.usfirst.frc.team449.robot;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.wpi.first.wpilibj.command.Command;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.drive.unidirectional.DriveTalonCluster;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedDigitalInput;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlCommand;
import org.usfirst.frc.team449.robot.logger.Logger;
import org.usfirst.frc.team449.robot.oi.buttons.CommandButton;
import org.usfirst.frc.team449.robot.oi.unidirectional.OIUnidirectional;
import org.usfirst.frc.team449.robot.other.MotionProfileData;
import org.usfirst.frc.team449.robot.subsystem.complex.climber.ClimberCurrentLimited;
import org.usfirst.frc.team449.robot.subsystem.complex.intake.IntakeFixedAndActuated;
import org.usfirst.frc.team449.robot.subsystem.complex.shooter.LoggingFeeder;
import org.usfirst.frc.team449.robot.subsystem.interfaces.solenoid.SolenoidSimple;
import org.usfirst.frc.team449.robot.subsystem.singleImplementation.camera.CameraNetwork;
import org.usfirst.frc.team449.robot.subsystem.singleImplementation.pneumatics.Pneumatics;

import java.util.List;
import java.util.Map;

/**
 * The Jackson-compatible object representing the entire robot.
 */
public class RobotMap2017 {

	/**
	 * The buttons for controlling this robot.
	 */
	@NotNull
	private final List<CommandButton> buttons;

	/**
	 * The OI for controlling this robot's drive.
	 */
	@NotNull
	private final OIUnidirectional oi;

	/**
	 * The logger for recording events and telemetry data.
	 */
	@NotNull
	private final Logger logger;

	/**
	 * The drive.
	 */
	@NotNull
	private final DriveTalonCluster drive;

	/**
	 * The command for the drive to run during the teleoperated period.
	 */
	@NotNull
	private final Command defaultDriveCommand;

	/**
	 * The climber for boarding the airship. Can be null.
	 */
	@Nullable
	private final ClimberCurrentLimited climber;

	/**
	 * The multiSubsystem for shooting fuel. Can be null.
	 */
	@Nullable
	private final LoggingFeeder shooter;

	/**
	 * The cameras on this robot. Can be null.
	 */
	@Nullable
	private final CameraNetwork camera;

	/**
	 * The intake for picking up and agitating balls. Can be null.
	 */
	@Nullable
	private final IntakeFixedAndActuated intake;

	/**
	 * The pneumatics on this robot. Can be null.
	 */
	@Nullable
	private final Pneumatics pneumatics;

	/**
	 * The gear handler on this robot. Can be null.
	 */
	@Nullable
	private final SolenoidSimple gearHandler;

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
	 * @param shooter             The multiSubsystem for shooting fuel. Can be null.
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
	public RobotMap2017(@NotNull @JsonProperty(required = true) List<CommandButton> buttons,
	                    @NotNull @JsonProperty(required = true) OIUnidirectional oi,
	                    @NotNull @JsonProperty(required = true) Logger logger,
	                    @NotNull @JsonProperty(required = true) DriveTalonCluster drive,
	                    @NotNull @JsonProperty(required = true) YamlCommand defaultDriveCommand,
	                    @Nullable ClimberCurrentLimited climber,
	                    @Nullable LoggingFeeder shooter,
	                    @Nullable CameraNetwork camera,
	                    @Nullable IntakeFixedAndActuated intake,
	                    @Nullable Pneumatics pneumatics,
	                    @Nullable SolenoidSimple gearHandler,
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
	public OIUnidirectional getOI() {
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
	public DriveTalonCluster getDrive() {
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
	public ClimberCurrentLimited getClimber() {
		return climber;
	}

	/**
	 * @return The multiSubsystem for shooting fuel. Can be null.
	 */
	@Nullable
	public LoggingFeeder getShooter() {
		return shooter;
	}

	/**
	 * @return The cameras on this robot. Can be null.
	 */
	@Nullable
	public CameraNetwork getCamera() {
		return camera;
	}

	/**
	 * @return The intake for picking up and agitating balls. Can be null.
	 */
	@Nullable
	public IntakeFixedAndActuated getIntake() {
		return intake;
	}

	/**
	 * @return The pneumatics on this robot. Can be null.
	 */
	@Nullable
	public Pneumatics getPneumatics() {
		return pneumatics;
	}

	/**
	 * @return The gear handler on this robot. Can be null.
	 */
	@Nullable
	public SolenoidSimple getGearHandler() {
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
