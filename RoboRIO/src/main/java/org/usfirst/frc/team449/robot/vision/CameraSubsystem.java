package org.usfirst.frc.team449.robot.vision;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.cscore.MjpegServer;
import org.usfirst.frc.team449.robot.util.YamlSubsystem;
import org.usfirst.frc.team449.robot.components.MappedUsbCamera;
import org.usfirst.frc.team449.robot.util.Logger;

import java.util.List;

/**
 * Subsystem to initialize and push video to SmartDashboard
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class CameraSubsystem extends YamlSubsystem {

	/**
	 * Video server to view on SmartDashboard
	 */
	public MjpegServer server;

	/**
	 * List of cameras used on robot
	 */
	public List<MappedUsbCamera> cameras;

	/**
	 * Total number of cameras
	 */
	public int camNum;

	/**
	 * Default constructor
	 *
	 * @param serverPort The port of the {@link MjpegServer} this subsystem uses.
	 * @param serverName The human-friendly name of the {@link MjpegServer} this subsystem uses.
	 * @param cameras    The cameras this subsystem controls.
	 */
	@JsonCreator
	public CameraSubsystem(@JsonProperty(required = true) int serverPort,
	                       @JsonProperty(required = true) String serverName,
	                       @JsonProperty(required = true) List<MappedUsbCamera> cameras) {
		super();

		//Logging
		Logger.addEvent("CameraSubsystem construct start", this.getClass());
		Logger.addEvent("Set URL of MJPGServer to \"http://roboRIO-449-frc.local:" + serverPort +
				"/stream.mjpg\"", this.getClass());

		//Instantiates server
		server = new MjpegServer(serverName, serverPort);

		//Instantiates cameras
		this.cameras = cameras;

		//Starts streaming video from first camera, marks that via camNum
		server.setSource(cameras.get(0));
		camNum = 0;

		//Logging
		Logger.addEvent("CameraSubsystem construct end", this.getClass());
	}

	/**
	 * Initialize the default command for a subsystem. By default subsystems have
	 * no default command, but if they do, the default command is set with this
	 * method. It is called on all Subsystems by CommandBase in the users program
	 * after all the Subsystems are created.
	 */
	@Override
	protected void initDefaultCommand() {
		//Do nothing!
	}
}
