package org.usfirst.frc.team449.robot.vision.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.vision.CameraSubsystem;

/**
 * Created by bryanli on 12/1/16.
 */
public class ChangeCam extends ReferencingCommand {

    private CameraSubsystem cameraSubsystem;

    public ChangeCam(CameraSubsystem cameraSubsystem, double timeout) {
        super(cameraSubsystem, timeout);
        requires(cameraSubsystem);
        this.cameraSubsystem = cameraSubsystem;
    }

    @Override
    protected void initialize() {
        System.out.println("ChangeCam init");
    }

    @Override
    protected void execute() {
        System.out.println("ChangeCam exec start");

        if (cameraSubsystem.cameras.size() == 1){
	        System.out.println("You're trying to switch cameras, but your robot only has one camera!");
        } else {
        	cameraSubsystem.camNum = (cameraSubsystem.camNum + 1) % cameraSubsystem.cameras.size();
        }

        cameraSubsystem.server.setSource(cameraSubsystem.cameras.get(cameraSubsystem.camNum));

        System.out.println("ChangeCam exec end");
    }

    @Override
    protected boolean isFinished(){ return true; }

    @Override
    protected void end() {
        System.out.println("ChangeCam end");
    }

    @Override
    protected void interrupted(){
	    System.out.println("ChangeCam interrupted!");
    }
}
