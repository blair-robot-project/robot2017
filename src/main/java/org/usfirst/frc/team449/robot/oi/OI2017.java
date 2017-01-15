package org.usfirst.frc.team449.robot.oi;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

/**
 * Created by Justin on 1/12/2017.
 */
public class OI2017 extends OISubsystem {

    Joystick joystick;
    JoystickButton climbButton;

    public OI2017(maps.org.usfirst.frc.team449.robot.oi.OI2017Map.OI2017 map) {
        super(map.getOi());
        this.map = map;

        joystick=new Joystick(0);
        setupButtons();
    }

    @Override
    protected void initDefaultCommand() {
        //Inheritance is stupid sometimes.
    }

    private void setupButtons(){
        climbButton=new JoystickButton(joystick,6);
    }

    private void mapButtons(){

    }

    @Override
    public double getClimberThrottle(){
        return joystick.getAxis(Joystick.AxisType.kY);
    }

    @Override
    public double getDriveAxisLeft() {
        return 0; //Do Nothing!
    }

    @Override
    public double getDriveAxisRight() {
        return 0; //Do Nothing!
    }

    @Override
    public void toggleCamera() {
        //Do Nothing!
    }
}
