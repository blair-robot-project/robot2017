package org.usfirst.frc.team449.robot;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team449.robot.autonomous.BoilerAuto2017;
import org.usfirst.frc.team449.robot.autonomous.CenterAuto2017;
import org.usfirst.frc.team449.robot.autonomous.FeederAuto2017;
import org.usfirst.frc.team449.robot.components.MappedDigitalInput;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.UnidirectionalNavXArcadeDrive;
import org.usfirst.frc.team449.robot.mechanism.activegear.ActiveGearSubsystem;
import org.usfirst.frc.team449.robot.mechanism.climber.ClimberSubsystem;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.Intake2017;
import org.usfirst.frc.team449.robot.mechanism.pneumatics.PneumaticsSubsystem;
import org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.SingleFlywheelShooter;
import org.usfirst.frc.team449.robot.oi.ArcadeOIWithDPad;
import org.usfirst.frc.team449.robot.oi.ButtonMapOI;
import org.usfirst.frc.team449.robot.util.Logger;
import org.usfirst.frc.team449.robot.util.MotionProfileData;
import org.usfirst.frc.team449.robot.util.YamlCommand;
import org.usfirst.frc.team449.robot.vision.CameraSubsystem;

import java.util.Map;

/**
 * The Jackson-compatible object representing the entire robot.
 */
public class RobotMap {
	private ButtonMapOI buttonMapOI;

	private ArcadeOIWithDPad arcadeOI;

	private TalonClusterDrive drive;

	private UnidirectionalNavXArcadeDrive defaultDriveCommand;

	private ClimberSubsystem climber;

	private SingleFlywheelShooter shooter;

	private CameraSubsystem camera;

	private Intake2017 intake;

	private PneumaticsSubsystem pneumatics;

	private ActiveGearSubsystem gearHandler;

	private Logger logger;

	private Integer RIOduinoPort;

	private MappedDigitalInput allianceSwitch;

	private MappedDigitalInput dropGearSwitch;

	private MappedDigitalInput locationDial;

	private BoilerAuto2017 boilerAuto;

	private CenterAuto2017 centerAuto;

	private FeederAuto2017 feederAuto;

	private MotionProfileData leftTestProfile;

	private MotionProfileData rightTestProfile;

	private Map<String, MotionProfileData> leftProfiles;

	private Map<String, MotionProfileData> rightProfiles;

	private Command nonMPAutoCommand;

	private boolean testMP;

	private boolean doMP;

	/**
	 * Default constructor.
	 *
	 * @param buttonMapOI      The oi containing the buttons for this robot..
	 * @param logger           The logger for recording events and telemetry data.
	 * @param drive The drive.
	 * @param defaultDriveCommand The command for the drive to run during the teleoperated period.
	 * @param climber          The climber for boarding the airship. Can be null.
	 * @param shooter          The shooter for shooting fuel. Can be null.
	 * @param camera           The cameras on this robot. Can be null.
	 * @param intake           The intake for picking up and agitating balls. Can be null.
	 * @param pneumatics       The pneumatics on this robot. Can be null.
	 * @param gearHandler      The gear handler on this robot. Can be null.
	 * @param RIOduinoPort     The I2C port of the RIOduino plugged into this robot. Can be null.
	 * @param allianceSwitch   The switch for selecting which alliance we're on. Can be null if doMP is false or testMP
	 *                         is true, but otherwise must have a value.
	 * @param dropGearSwitch   The switch for deciding whether or not to drop the gear. Can be null if doMP is false or
	 *                         testMP is true, but otherwise must have a value.
	 * @param locationDial     The dial for selecting which side of the field the robot is on. Can be null if doMP is
	 *                         false or testMP is true, but otherwise must have a value.
	 * @param boilerAuto       The command to run in autonomous on the boiler side of the field. Can be null if doMP is
	 *                         false or testMP is true, but otherwise must have a value.
	 * @param centerAuto       The command to run in autonomous on the center of the field. Can be null if doMP is false
	 *                         or testMP is true, but otherwise must have a value.
	 * @param feederAuto       The command to run in autonomous on the feeding station side of the field. Can be null if
	 *                         doMP is false or testMP is true, but otherwise must have a value.
	 * @param leftTestProfile  The profile for the left side of the drive to run in test mode. Can be null if either
	 *                         testMP or doMP are false, but otherwise must have a value.
	 * @param rightTestProfile The profile for the right side of the drive to run in test mode. Can be null if either
	 *                         testMP or doMP are false, but otherwise must have a value.
	 * @param leftProfiles     The starting position to peg profiles for the left side. Should have options for
	 *                         "red_right", "red_center", "red_left", "blue_right", "blue_center", and "blue_left". Can
	 *                         be null if doMP is false or testMP is true, but otherwise must have a value.
	 * @param rightProfiles    The starting position to peg profiles for the right side. Should have options for
	 *                         "red_right", "red_center", "red_left", "blue_right", "blue_center", and "blue_left". Can
	 *                         be null if doMP is false or testMP is true, but otherwise must have a value.
	 * @param nonMPAutoCommand The command to run during autonomous if doMP is false. Can be null, and if it is, no
	 *                         command is run during autonomous.
	 * @param testMP           Whether to run the test or real motion profile during autonomous. Defaults to false.
	 * @param doMP             Whether to run a motion profile during autonomous. Defaults to true.
	 */
	@JsonCreator
	public RobotMap(@JsonProperty(required = true) ButtonMapOI buttonMapOI,
	                @JsonProperty(required = true) ArcadeOIWithDPad arcadeOI,
	                @JsonProperty(required = true) Logger logger,
	                @JsonProperty(required = true) TalonClusterDrive drive,
	                @JsonProperty(required = true) UnidirectionalNavXArcadeDrive defaultDriveCommand,
	                ClimberSubsystem climber,
	                SingleFlywheelShooter shooter,
	                CameraSubsystem camera,
	                Intake2017 intake,
	                PneumaticsSubsystem pneumatics,
	                ActiveGearSubsystem gearHandler,
	                Integer RIOduinoPort,
	                MappedDigitalInput allianceSwitch,
	                MappedDigitalInput dropGearSwitch,
	                MappedDigitalInput locationDial,
	                BoilerAuto2017 boilerAuto,
	                CenterAuto2017 centerAuto,
	                FeederAuto2017 feederAuto,
	                MotionProfileData leftTestProfile, MotionProfileData rightTestProfile,
	                Map<String, MotionProfileData> leftProfiles, Map<String, MotionProfileData> rightProfiles,
	                YamlCommand nonMPAutoCommand,
	                boolean testMP,
	                Boolean doMP) {
		this.buttonMapOI = buttonMapOI;
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
		}
		this.testMP = testMP;
		if (doMP == null) {
			doMP = true;
		}
		this.doMP = doMP;
	}

	public ButtonMapOI getButtonMapOI() {
		return buttonMapOI;
	}

	public TalonClusterDrive getDrive() {
		return drive;
	}

	public ClimberSubsystem getClimber() {
		return climber;
	}

	public SingleFlywheelShooter getShooter() {
		return shooter;
	}

	public CameraSubsystem getCamera() {
		return camera;
	}

	public Intake2017 getIntake() {
		return intake;
	}

	public PneumaticsSubsystem getPneumatics() {
		return pneumatics;
	}

	public ActiveGearSubsystem getGearHandler() {
		return gearHandler;
	}

	public Logger getLogger() {
		return logger;
	}

	public Integer getRIOduinoPort() {
		return RIOduinoPort;
	}

	public MappedDigitalInput getAllianceSwitch() {
		return allianceSwitch;
	}

	public MappedDigitalInput getDropGearSwitch() {
		return dropGearSwitch;
	}

	public MappedDigitalInput getLocationDial() {
		return locationDial;
	}

	public MotionProfileData getLeftTestProfile() {
		return leftTestProfile;
	}

	public MotionProfileData getRightTestProfile() {
		return rightTestProfile;
	}

	public boolean getTestMP() {
		return testMP;
	}

	public boolean getDoMP() {
		return doMP;
	}

	public Map<String, MotionProfileData> getLeftProfiles() {
		return leftProfiles;
	}

	public Map<String, MotionProfileData> getRightProfiles() {
		return rightProfiles;
	}

	public BoilerAuto2017 getBoilerAuto() {
		return boilerAuto;
	}

	public CenterAuto2017 getCenterAuto() {
		return centerAuto;
	}

	public FeederAuto2017 getFeederAuto() {
		return feederAuto;
	}

	public Command getNonMPAutoCommand() {
		return nonMPAutoCommand;
	}

	public UnidirectionalNavXArcadeDrive getDefaultDriveCommand() {
		return defaultDriveCommand;
	}

	public ArcadeOIWithDPad getArcadeOI() {
		return arcadeOI;
	}
}
