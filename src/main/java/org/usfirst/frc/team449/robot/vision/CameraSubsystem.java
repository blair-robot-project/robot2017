package org.usfirst.frc.team449.robot.vision;

import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
//import edu.wpi.first.wpilibj.CameraServer;

import maps.org.usfirst.frc.team449.robot.components.UsbCameraMap;
import maps.org.usfirst.frc.team449.robot.vision.CameraMap;
import org.usfirst.frc.team449.robot.MappedSubsystem;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by bryanli on 11/10/16.
 */
public class CameraSubsystem extends MappedSubsystem {

    public MjpegServer server;
    public List<UsbCamera> cameras;
    public int camNum;

    public static CameraMap.Camera map;

    public CameraSubsystem(CameraMap.Camera map){
        super(map);
        this.map = map;
        System.out.println("CameraSubsystem construct start");
        System.out.println("Set URL of MJPGServer to \"http://roboRIO-449-frc.local:"+map.getServer().getPort()+"/stream.mjpg\"");
        server = new MjpegServer(map.getServer().getName(),map.getServer().getPort());
        cameras = new ArrayList<>();
        for (UsbCameraMap.UsbCamera camera : map.getUSBCameraList()){
            UsbCamera tmp = new UsbCamera(camera.getName(), camera.getDev());
            tmp.setResolution(camera.getWidth(), camera.getHeight());
            tmp.setFPS(camera.getFps());
	        System.out.println("Added "+camera.getName()+" to camera list.");
	        cameras.add(tmp);
        }
        server.setSource(cameras.get(0));
        camNum = 0;
        System.out.println("CameraSubsystem construct end");
    }

    @Override
    protected void initDefaultCommand () {
        //Do nothing!
    }

}
