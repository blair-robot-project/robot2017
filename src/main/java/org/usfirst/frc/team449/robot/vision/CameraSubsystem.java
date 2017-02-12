package org.usfirst.frc.team449.robot.vision;

import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
//import edu.wpi.first.wpilibj.CameraServer;

import maps.org.usfirst.frc.team449.robot.vision.CameraMap;
import org.usfirst.frc.team449.robot.MappedSubsystem;


/**
 * Created by bryanli on 11/10/16.
 */
public class CameraSubsystem extends MappedSubsystem {


    public MjpegServer server;
    public UsbCamera cam1;
    public UsbCamera cam2;
    public int camNum;

    public static CameraMap.CamRobot staticMap;

    public CameraSubsystem(CameraMap.CamRobot map){
        super(map);
        staticMap = map;
        System.out.println("initStart");
        System.out.println("Set URL of MJPGServer to `http://roboRIO-449-frc.local:5800/stream.mjpg`");
        server = new MjpegServer("Cameras",5800);
        cam1 = new UsbCamera("cam1",0);
        cam1.setResolution(160,90);
        cam1.setFPS(30);
        cam2 = new UsbCamera("cam2",1);
        cam2.setResolution(160,120);
        cam2.setFPS(30);
        server.setSource(cam1);
        camNum = 1;
        System.out.println("initEnd");
    }

    @Override
    protected void initDefaultCommand () {
        //Do nothing!
    }

}
