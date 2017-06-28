package org.usfirst.frc.team449.robot;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.wpi.first.wpilibj.command.Command;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.components.MappedDigitalInput;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.UnidirectionalNavXDefaultDrive;
import org.usfirst.frc.team449.robot.mechanism.activegear.ActiveGearSubsystem;
import org.usfirst.frc.team449.robot.mechanism.climber.ClimberSubsystem;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.Intake2017;
import org.usfirst.frc.team449.robot.mechanism.pneumatics.PneumaticsSubsystem;
import org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.SingleFlywheelShooter;
import org.usfirst.frc.team449.robot.oi.ArcadeOIWithDPad;
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

	@NotNull
	private final ArcadeOIWithDPad arcadeOI;

	@NotNull
	private final TalonClusterDrive drive;

	@NotNull
	private final UnidirectionalNavXDefaultDrive defaultDriveCommand;

	@Nullable
	private final ClimberSubsystem climber;

	@Nullable
	private final SingleFlywheelShooter shooter;

	@Nullable
	private final CameraSubsystem camera;

	@Nullable
	private final Intake2017 intake;

	@Nullable
	private final PneumaticsSubsystem pneumatics;

	@Nullable
	private final ActiveGearSubsystem gearHandler;

	@NotNull
	private final Logger logger;

	@Nullable
	private final Integer RIOduinoPort;

	@Nullable
	private final MappedDigitalInput allianceSwitch;

	@Nullable
	private final MappedDigitalInput dropGearSwitch;

	@Nullable
	private final MappedDigitalInput locationDial;

	@Nullable
	private final YamlCommand boilerAuto;

	@Nullable
	private final YamlCommand centerAuto;

	@Nullable
	private final YamlCommand feederAuto;

	@Nullable
	private final MotionProfileData leftTestProfile;

	@Nullable
	private final MotionProfileData rightTestProfile;

	@Nullable
	private final Map<String, MotionProfileData> leftProfiles;

	@Nullable
	private final Map<String, MotionProfileData> rightProfiles;

	@Nullable
	private final Command nonMPAutoCommand;

	private final boolean testMP;

	private final boolean doMP;

	@NotNull
	private final List<CommandButton> buttons;

	/**
	 * Default constructor.
	 *
	 * @param buttons             The buttons for controlling this robot.
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
	 *                            testMP
	 *                            is true, but otherwise must have a value.
	 * @param dropGearSwitch      The switch for deciding whether or not to drop the gear. Can be null if doMP is false
	 *                            or
	 *                            testMP is true, but otherwise must have a value.
	 * @param locationDial        The dial for selecting which side of the field the robot is on. Can be null if doMP
	 *                            is
	 *                            false or testMP is true, but otherwise must have a value.
	 * @param boilerAuto          The command to run in autonomous on the boiler side of the field. Can be null if doMP
	 *                            is
	 *                            false or testMP is true, but otherwise must have a value.
	 * @param centerAuto          The command to run in autonomous on the center of the field. Can be null if doMP is
	 *                            false
	 *                            or testMP is true, but otherwise must have a value.
	 * @param feederAuto          The command to run in autonomous on the feeding station side of the field. Can be
	 *                            null
	 *                            if
	 *                            doMP is false or testMP is true, but otherwise must have a value.
	 * @param leftTestProfile     The profile for the left side of the drive to run in test mode. Can be null if either
	 *                            testMP or doMP are false, but otherwise must have a value.
	 * @param rightTestProfile    The profile for the right side of the drive to run in test mode. Can be null if
	 *                            either
	 *                            testMP or doMP are false, but otherwise must have a value.
	 * @param leftProfiles        The starting position to peg profiles for the left side. Should have options for
	 *                            "red_right", "red_center", "red_left", "blue_right", "blue_center", and "blue_left".
	 *                            Can
	 *                            be null if doMP is false or testMP is true, but otherwise must have a value.
	 * @param rightProfiles       The starting position to peg profiles for the right side. Should have options for
	 *                            "red_right", "red_center", "red_left", "blue_right", "blue_center", and "blue_left".
	 *                            Can
	 *                            be null if doMP is false or testMP is true, but otherwise must have a value.
	 * @param nonMPAutoCommand    The command to run during autonomous if doMP is false. Can be null, and if it is, no
	 *                            command is run during autonomous.
	 * @param testMP              Whether to run the test or real motion profile during autonomous. Defaults to false.
	 * @param doMP                Whether to run a motion profile during autonomous. Defaults to true.
	 */
	@JsonCreator
	public RobotMap(@NotNull @JsonProperty(required = true) List<CommandButton> buttons,
	                @NotNull @JsonProperty(required = true) ArcadeOIWithDPad arcadeOI,
	                @NotNull @JsonProperty(required = true) Logger logger,
	                @NotNull @JsonProperty(required = true) TalonClusterDrive drive,
	                @NotNull @JsonProperty(required = true) UnidirectionalNavXDefaultDrive defaultDriveCommand,
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
		this.arcadeOI = arcadeOI;
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
		this.boilerAuto = boilerAuto;
		this.centerAuto = centerAuto;
		this.feederAuto = feederAuto;
		this.leftTestProfile = leftTestProfile;
		this.rightTestProfile = rightTestProfile;
		this.leftProfiles = leftProfiles;
		this.rightProfiles = rightProfiles;
		this.defaultDriveCommand = defaultDriveCommand;
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

	@NotNull
	public TalonClusterDrive getDrive() {
		return drive;
	}

	@Nullable
	public ClimberSubsystem getClimber() {
		return climber;
	}

	@Nullable
	public SingleFlywheelShooter getShooter() {
		return shooter;
	}

	@Nullable
	public CameraSubsystem getCamera() {
		return camera;
	}

	@Nullable
	public Intake2017 getIntake() {
		return intake;
	}

	@Nullable
	public PneumaticsSubsystem getPneumatics() {
		return pneumatics;
	}

	@Nullable
	public ActiveGearSubsystem getGearHandler() {
		return gearHandler;
	}

	@NotNull
	public Logger getLogger() {
		return logger;
	}

	@Nullable
	public Integer getRIOduinoPort() {
		return RIOduinoPort;
	}

	@Nullable
	public MappedDigitalInput getAllianceSwitch() {
		return allianceSwitch;
	}

	@Nullable
	public MappedDigitalInput getDropGearSwitch() {
		return dropGearSwitch;
	}

	@Nullable
	public MappedDigitalInput getLocationDial() {
		return locationDial;
	}

	@Nullable
	public MotionProfileData getLeftTestProfile() {
		return leftTestProfile;
	}

	@Nullable
	public MotionProfileData getRightTestProfile() {
		return rightTestProfile;
	}

	public boolean getTestMP() {
		return testMP;
	}

	public boolean getDoMP() {
		return doMP;
	}

	@Nullable
	public Map<String, MotionProfileData> getLeftProfiles() {
		return leftProfiles;
	}

	@Nullable
	public Map<String, MotionProfileData> getRightProfiles() {
		return rightProfiles;
	}

	@Nullable
	public YamlCommand getBoilerAuto() {
		return boilerAuto;
	}

	@Nullable
	public YamlCommand getCenterAuto() {
		return centerAuto;
	}

	@Nullable
	public YamlCommand getFeederAuto() {
		return feederAuto;
	}

	@Nullable
	public Command getNonMPAutoCommand() {
		return nonMPAutoCommand;
	}

	@NotNull
	public UnidirectionalNavXDefaultDrive getDefaultDriveCommand() {
		return defaultDriveCommand;
	}

	@NotNull
	public ArcadeOIWithDPad getArcadeOI() {
		return arcadeOI;
	}

	@NotNull
	public List<CommandButton> getButtons() {
		return buttons;
	}
}
