package org.usfirst.frc.team449.robot.drive.meccanum;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import maps.org.usfirst.frc.team449.robot.components.ToleranceBufferAnglePIDMap;
import org.usfirst.frc.team449.robot.components.NavxSubsystem;
import org.usfirst.frc.team449.robot.components.UnitlessCANTalonSRX;
import org.usfirst.frc.team449.robot.drive.DriveSubsystem;
import org.usfirst.frc.team449.robot.oi.OI2017;

/**
 * Created by sam on 1/26/17.
 */
public class MeccanumDrive extends DriveSubsystem implements NavxSubsystem {

    public AHRS navx;
    public UnitlessCANTalonSRX frontLeft, frontRight, backLeft, backRight;
    public ToleranceBufferAnglePIDMap.ToleranceBufferAnglePID turnPID;
    public ToleranceBufferAnglePIDMap.ToleranceBufferAnglePID straightPID;
    public OI2017 oi;
    private long startTime;

    public MeccanumDrive(maps.org.usfirst.frc.team449.robot.drive.meccanum.MeccanumDriveMap.MeccanumDrive map, OI2017 oi){
        super(map.getDrive());
        this.map = map;
        this.navx = new AHRS(SPI.Port.kMXP);
        this.turnPID = map.getTurnPID();
        this.straightPID = map.getStraightPID();
        this.oi = oi;

        this.frontLeft = new UnitlessCANTalonSRX(map.getFrontLeft());
        this.frontRight = new UnitlessCANTalonSRX(map.getFrontRight());
        this.backLeft = new UnitlessCANTalonSRX(map.getBackLeft());
        this.backRight = new UnitlessCANTalonSRX(map.getBackRight());
    }

    private void setVBusThrottle(double fl, double fr, double bl, double br){
        frontLeft.setPercentVbus(fl);
        frontRight.setPercentVbus(fr);
        backLeft.setPercentVbus(bl);
        backRight.setPercentVbus(br);
    }

    private void setPIDThrottle(double fl, double fr, double bl, double br){
        frontLeft.setSpeed(.7 * (fl * frontLeft.getMaxSpeed()));
        frontRight.setSpeed(.7 * (fr * frontRight.getMaxSpeed()));
        backLeft.setSpeed(.7 * (bl * backLeft.getMaxSpeed()));
        backRight.setSpeed(.7 * (br * backRight.getMaxSpeed()));
    }

    public void setDefaultThrottle(double fl, double fr, double bl, double br) {
        setPIDThrottle(fl, fr, bl, br);
    }

    @Override
    public double getGyroOutput(){
        return navx.pidGet();
    }

    @Override
    public void initDefaultCommand(){
        //Do nothing
    }

}
