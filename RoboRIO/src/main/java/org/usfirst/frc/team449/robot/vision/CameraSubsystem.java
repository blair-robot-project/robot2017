package org.usfirst.frc.team449.robot.vision;

import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
import maps.org.usfirst.frc.team449.robot.components.UsbCameraMap;
import maps.org.usfirst.frc.team449.robot.vision.CameraMap;
import org.usfirst.frc.team449.robot.MappedSubsystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Subsystem to initialize and push video to SmartDashboard
 */
public class CameraSubsystem extends MappedSubsystem {

	/**
	 * Video server to view on SmartDashboard
	 */
	public MjpegServer server;

	/**
	 * List of cameras used on robot
	 */
	public List<UsbCamera> cameras;

	/**
	 * Total number of cameras
	 */
	public int camNum;

	/**
	 * Instantiates a new CameraSubsystem with the map
	 */
	public CameraMap.Camera map;

	/**
	 * Construct a CameraSubsystem
	 *
	 * @param map The config map
	 */
	public CameraSubsystem(CameraMap.Camera map) {
		super(map);
		this.map = map;

		//Logging to SmartDashboard
		System.out.println("CameraSubsystem construct start");
		System.out.println("Set URL of MJPGServer to \"http://roboRIO-449-frc.local:" + map.getServer().getPort() +
				"/stream.mjpg\"");

		//Instantiates server
		server = new MjpegServer(map.getServer().getName(), map.getServer().getPort());

		//Instantiates cameras
		cameras = new ArrayList<>();

		//Searches for each camera, then places them into camera list.
		for (UsbCameraMap.UsbCamera camera : map.getUSBCameraList()) {
			UsbCamera tmp = new UsbCamera(camera.getName(), camera.getDev());
			tmp.setResolution(camera.getWidth(), camera.getHeight());
			tmp.setFPS(camera.getFps());
			tmp.setExposureAuto();
			System.out.println("Added " + camera.getName() + " to camera list.");
			cameras.add(tmp);
		}

		//Starts streaming video from first camera, marks that via camNum
		server.setSource(cameras.get(0));
		camNum = 0;

		//Logging to console
		System.out.println("CameraSubsystem construct end");
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
