package org.usfirst.frc.team449.robot.drive.talonCluster;

import com.ctre.CANTalon;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import maps.org.usfirst.frc.team449.robot.components.ToleranceBufferAnglePIDMap;
import maps.org.usfirst.frc.team449.robot.components.UnitlessCANTalonSRXMap;
import org.usfirst.frc.team449.robot.components.NavxSubsystem;
import org.usfirst.frc.team449.robot.components.RotPerSecCANTalonSRX;
import org.usfirst.frc.team449.robot.drive.DriveSubsystem;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.DefaultArcadeDrive;
import org.usfirst.frc.team449.robot.oi.OI2017ArcadeGamepad;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A drive with a cluster of any number of CANTalonSRX controlled motors on each side.
 */
public class TalonClusterDrive extends DriveSubsystem implements NavxSubsystem {

	/**
	 * Right master Talon
	 */
	public RotPerSecCANTalonSRX rightMaster;
	/**
	 * Left master Talon
	 */
	public RotPerSecCANTalonSRX leftMaster;

	/**
	 * The NavX gyro
	 */
	public AHRS navx;

	/**
	 * The PIDAngleCommand constants for turning to an angle with the NavX
	 */
	public ToleranceBufferAnglePIDMap.ToleranceBufferAnglePID turnPID;

	/**
	 * The PIDAngleCommand constants for using the NavX to drive straight
	 */
	public ToleranceBufferAnglePIDMap.ToleranceBufferAnglePID straightPID;

	/**
	 * The oi used to drive the robot
	 */
	public OI2017ArcadeGamepad oi;

	/**
	 * The solenoid that shifts between gears
	 */
	public DoubleSolenoid shifter;

	// TODO take these out after testing
	/**
	 * Current status of the left side MP
	 */
	public CANTalon.MotionProfileStatus leftTPointStatus;
	/**
	 * Current status of the right side MP
	 */
	public CANTalon.MotionProfileStatus rightTPointStatus;

	/**
	 * The time (nanoseconds) when the robot was enabled (for use in logging)
	 */
	private long startTime;

	/**
	 * The name of the file to log to
	 */
	private String logFN;

	/**
	 * Whether or not to use the NavX for driving straight
	 */
	public boolean overrideNavX;

	/**
	 * Measured max speed of robot reached in a run. Used for testing and tuning. NOT max_speed tuning constant
	 *
	 * @deprecated
	 */
	@Deprecated
	private double maxSpeed;

	// TODO externalize
	/**
	 * Joystick scaling constant. Joystick output is scaled by this before being handed to the PID loop to give the
	 * loop space to compensate.
	 */
	private final double PID_SCALE = 0.9;

	/**
	 * Upshift timeout.
	 * The robot will wait for this this number of milliseconds before upshifting after a downshift
	 */
	private double upTimeThresh;
	/**
	 * Downshift timeout.
	 * The robot will wait for this this number of milliseconds before downshifting after an upshift
	 */
	private double downTimeThresh;

	/**
	 * Whether we can up shift, used as a flag for the delay
	 */
	private boolean okToUpshift;
	/**
	 * Whether we can down shift, used as a flag for the delay
	 */
	private boolean okToDownshift;

	// TODO refactor; THIS IS NOT A DEADBAND
	/**
	 * The setpoint (on a 0-1 scale) below which we stay in low gear
	 */
	private double upshiftFwdDeadband;

	/**
	 * The last time (in milliseconds) at which we upshifted
	 */
	private long timeAboveShift;
	/**
	 * The last time (in milliseconds) at which we downshifted
	 */
	private long timeBelowShift;

	/**
	 * The time we last shifted (milliseconds)
	 */
	private long timeLastShifted;

	// TODO simplify shifting and make this a primitive
	/**
	 * The minimum time between shifting in either direction
	 */
	private Double shiftDelay;

	/**
	 * Whether not to override auto shifting
	 */
	public boolean overrideAutoShift;

	// TODO set this when we shift
	// TODO make this an enum instead of a boolean
	/**
	 * Whether we're in low gear
	 */
	private boolean lowGear = true;    //we want to start in low gear

	/**
	 * The speed setpoint at the upshift break
	 */
	private double upshift;

	/**
	 * The speed setpoint at the downshiftshift break
	 */
	private double downshift;

	/**
	 * Construct a TalonClusterDrive
	 *
	 * @param map config map
	 * @param oi  OI to read throttle from
	 */
	public TalonClusterDrive(maps.org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDriveMap
			                         .TalonClusterDrive map, OI2017ArcadeGamepad oi) {
		super(map.getDrive());

		// Write the headers for the logfile.
		// TODO externalize this shit
		logFN = "/home/lvuser/logs/driveLog-" + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + "" +
				".csv";
		try (PrintWriter writer = new PrintWriter(logFN)) {
			writer.println("time,left,right,left error,right error,left setpoint,right setpoint");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.map = map;
		this.oi = oi;
		navx = new AHRS(SPI.Port.kMXP);
		turnPID = map.getTurnPID();
		straightPID = map.getStraightPID();
		upTimeThresh = map.getUpTimeThresh();
		downTimeThresh = map.getDownTimeThresh();
		upshiftFwdDeadband = map.getUpshiftFwdDeadband();
		upshift = map.getUpshift();
		downshift = map.getDownshift();

		// Shift delay is optional, so it'll be null if it isn't in map.
		if (map.hasShiftDelay()) {
			this.shiftDelay = map.getShiftDelay();
		}

		// Initialize shifting constants, assuming robot is stationary.
		okToUpshift = false;
		okToDownshift = true;
		overrideAutoShift = false;
		timeLastShifted = 0;

		// If the map has the shifting piston, instantiate it.
		if (map.hasShifter()) {
			this.shifter = new DoubleSolenoid(map.getModuleNumber(), map.getShifter().getForward(), map.getShifter()
					.getReverse());
		}

		// Initialize max
		maxSpeed = -1;

		// Initialize master talons
		rightMaster = new RotPerSecCANTalonSRX(map.getRightMaster());
		leftMaster = new RotPerSecCANTalonSRX(map.getLeftMaster());

		// Initialize slave talons.
		for (UnitlessCANTalonSRXMap.UnitlessCANTalonSRX talon : map.getRightSlaveList()) {
			RotPerSecCANTalonSRX talonObject = new RotPerSecCANTalonSRX(talon);
			talonObject.canTalon.changeControlMode(CANTalon.TalonControlMode.Follower);
			talonObject.canTalon.set(map.getRightMaster().getPort());
		}
		for (UnitlessCANTalonSRXMap.UnitlessCANTalonSRX talon : map.getLeftSlaveList()) {
			RotPerSecCANTalonSRX talonObject = new RotPerSecCANTalonSRX(talon);
			talonObject.canTalon.changeControlMode(CANTalon.TalonControlMode.Follower);
			talonObject.canTalon.set(map.getLeftMaster().getPort());
		}

		leftTPointStatus = new CANTalon.MotionProfileStatus();
		rightTPointStatus = new CANTalon.MotionProfileStatus();
	}

	/**
	 * Sets the left and right wheel speeds as a percent of max voltage, not nearly as precise as PID.
	 *
	 * @param left  The left voltage throttle, [-1, 1]
	 * @param right The right voltage throttle, [-1, 1]
	 */
	private void setVBusThrottle(double left, double right) {
		leftMaster.setPercentVbus(left);
		rightMaster.setPercentVbus(-right);
	}

	// TODO refactor to specifiy PID VELOCITY setpoint

	/**
	 * Sets left and right wheel PID velocity setpoint as a percent of max setpoint
	 *
	 * @param left  The left PID velocity setpoint as a percent [-1, 1]
	 * @param right The right PID velocity setpoint as a percent [-1, 1]
	 */
	private void setPIDThrottle(double left, double right) {
		if (overrideAutoShift) {
			leftMaster.setSpeed(PID_SCALE * (left * leftMaster.getMaxSpeed()));
			rightMaster.setSpeed(PID_SCALE * (right * rightMaster.getMaxSpeed()));
		} else {
			leftMaster.setSpeed(PID_SCALE * (left * leftMaster.getMaxSpeedHG()));
			rightMaster.setSpeed(PID_SCALE * (right * rightMaster.getMaxSpeedHG()));
		}
	}

	/**
	 * setPIDThrottle or setVBusThrottle, whichever is set as the "default"
	 *
	 * @param left  Left throttle value
	 * @param right Right throttle value
	 */
	public void setDefaultThrottle(double left, double right) {
		setPIDThrottle(clipToOne(left), clipToOne(right));
		//setVBusThrottle(left, right);
	}

	/**
	 * Log stuff to file
	 */
	public void logData() {
		try (FileWriter fw = new FileWriter(logFN, true)) {
			StringBuilder sb = new StringBuilder();
			sb.append((System.nanoTime() - startTime) / Math.pow(10, 9));
			sb.append(",");
			sb.append(leftMaster.getSpeed());
			sb.append(",");
			sb.append(rightMaster.getSpeed());
			sb.append(",");
			sb.append(leftMaster.getError());
			sb.append(",");
			sb.append(rightMaster.getError());
			 /*
			 sb.append(",");
	         sb.append(leftTPointStatus.activePoint.position);
	         sb.append(",");
	         sb.append(rightTPointStatus.activePoint.position);
	         */
			sb.append("\n");

			fw.write(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		maxSpeed = Math.max(maxSpeed, Math.max(Math.abs(leftMaster.getSpeed()), Math.abs(rightMaster.getSpeed())));
		SmartDashboard.putNumber("Max Speed", maxSpeed);
		SmartDashboard.putNumber("Left", leftMaster.getSpeed());
		SmartDashboard.putNumber("Right", rightMaster.getSpeed());
		SmartDashboard.putNumber("Throttle", leftMaster.nativeToRPS(leftMaster.canTalon.getSetpoint()));
		SmartDashboard.putNumber("Heading", navx.pidGet());
		SmartDashboard.putNumber("Left Setpoint", leftMaster.nativeToRPS(leftMaster.canTalon.getSetpoint()));
		SmartDashboard.putNumber("Left Error", leftMaster.nativeToRPS(leftMaster.canTalon.getError()));
		SmartDashboard.putNumber("Right Setpoint", rightMaster.nativeToRPS(rightMaster.canTalon.getSetpoint()));
		SmartDashboard.putNumber("Right Error", rightMaster.nativeToRPS(rightMaster.canTalon.getError()));
		SmartDashboard.putNumber("Left F", leftMaster.canTalon.getF());
		SmartDashboard.putNumber("Right F", rightMaster.canTalon.getF());
		SmartDashboard.putNumber("Left P", leftMaster.canTalon.getP());
		SmartDashboard.putNumber("Right P", rightMaster.canTalon.getP());
		SmartDashboard.putBoolean("In low gear?", lowGear);
	}

	/**
	 * Log stuff to file, with a given veocity setpoint to log
	 *
	 * @param sp velocity setpoint
	 */
	public void logData(double sp) {
		try (FileWriter fw = new FileWriter(logFN, true)) {
			StringBuilder sb = new StringBuilder();
			sb.append((System.nanoTime() - startTime) / Math.pow(10, 9));
			sb.append(",");
			sb.append(leftMaster.getSpeed());
			sb.append(",");
			sb.append(rightMaster.getSpeed());
			sb.append(",");
			sb.append(leftMaster.getError());
			sb.append(",");
			sb.append(rightMaster.getError());
			sb.append(",");
			sb.append(PID_SCALE * sp * leftMaster.getMaxSpeed());
			sb.append(",");
			sb.append(PID_SCALE * sp * rightMaster.getMaxSpeed());
			 /*
			 sb.append(",");
	         sb.append(leftTPointStatus.activePoint.position);
	         sb.append(",");
	         sb.append(rightTPointStatus.activePoint.position);
	         */
			sb.append("\n");

			fw.write(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		maxSpeed = Math.max(maxSpeed, Math.max(Math.abs(leftMaster.getSpeed()), Math.abs(rightMaster.getSpeed())));
		SmartDashboard.putNumber("Max Speed", maxSpeed);
		SmartDashboard.putNumber("Left", leftMaster.getSpeed());
		SmartDashboard.putNumber("Right", rightMaster.getSpeed());
		SmartDashboard.putNumber("Throttle", leftMaster.nativeToRPS(leftMaster.canTalon.getSetpoint()));
		SmartDashboard.putNumber("Heading", navx.pidGet());
		SmartDashboard.putNumber("Left Setpoint", leftMaster.nativeToRPS(leftMaster.canTalon.getSetpoint()));
		SmartDashboard.putNumber("Left Error", leftMaster.nativeToRPS(leftMaster.canTalon.getError()));
		SmartDashboard.putNumber("Right Setpoint", rightMaster.nativeToRPS(rightMaster.canTalon.getSetpoint()));
		SmartDashboard.putNumber("Right Error", rightMaster.nativeToRPS(rightMaster.canTalon.getError()));
		SmartDashboard.putNumber("Left F", leftMaster.canTalon.getF());
		SmartDashboard.putNumber("Right F", rightMaster.canTalon.getF());
		SmartDashboard.putNumber("Left P", leftMaster.canTalon.getP());
		SmartDashboard.putNumber("Right P", rightMaster.canTalon.getP());
		SmartDashboard.putBoolean("In low gear?", lowGear);
	}

	/**
	 * Stuff run on enable
	 * Reset starTime, turn on navX control, and start DefaultArcadeDrive
	 */
	@Override
	protected void initDefaultCommand() {
		startTime = System.nanoTime();
		overrideNavX = false;
		//		setDefaultCommand(new PIDTest(this));
		//		setDefaultCommand(new OpArcadeDrive(this, oi));
		setDefaultCommand(new DefaultArcadeDrive(straightPID, this, oi));
	}

	/**
	 * Get the robot's heading using the navX
	 *
	 * @return robot heading (degrees) [-180, 180]
	 */
	public double getGyroOutput() {
		return navx.pidGet();
	}

	// TODO generalize up-down with an enum

	/**
	 * Shift as appropriate
	 *
	 * @param setLowGear whether to shift down (if false, shift up)
	 */
	public void setLowGear(boolean setLowGear) {
		if (shifter != null) {
			if (setLowGear) {
				shifter.set(DoubleSolenoid.Value.kForward);
				rightMaster.switchToLowGear();
				leftMaster.switchToLowGear();
				lowGear = true;
			} else {
				shifter.set(DoubleSolenoid.Value.kReverse);
				rightMaster.switchToHighGear();
				leftMaster.switchToHighGear();
				lowGear = false;
			}
			timeLastShifted = System.currentTimeMillis();
		} else {
			System.out.println("You're trying to shift gears, but your drive doesn't have a shifter.");
		}
	}

	/**
	 * @return left cluster measured velocity
	 */
	public double getLeftSpeed() {
		return leftMaster.getSpeed();
	}

	/**
	 * @return right cluster measured velocity
	 */
	public double getRightSpeed() {
		return rightMaster.getSpeed();
	}

	/**
	 * @return whether the robot is in low gear
	 */
	public boolean inLowGear() {
		return lowGear;
	}

	// TODO simplify the should-shift logic

	/**
	 * @return whether the robot should downshift
	 */
	public boolean shouldDownshift() {
		boolean okToShift = Math.min(Math.abs(getLeftSpeed()), Math.abs(getRightSpeed())) < downshift && !lowGear &&
				!overrideAutoShift || oi.getFwd() == 0 && oi.getRot() != 0 && !overrideAutoShift;
		if (shiftDelay != null) {
			return okToShift && (System.currentTimeMillis() - timeLastShifted > shiftDelay * 1000);
		}
		if (okToShift && !okToDownshift) {
			okToDownshift = true;
			timeBelowShift = System.currentTimeMillis();
		} else if (!okToShift && okToDownshift) {
			okToDownshift = false;
		}
		return (System.currentTimeMillis() - timeBelowShift > downTimeThresh * 1000 && okToShift);
	}

	/**
	 * @return whether the robot should upshift
	 */
	public boolean shouldUpshift() {
		boolean okToShift = Math.max(Math.abs(getLeftSpeed()), Math.abs(getRightSpeed())) > upshift && lowGear &&
				!overrideAutoShift && Math.abs(oi.getFwd()) > upshiftFwdDeadband;
		if (shiftDelay != null) {
			return okToShift && (System.currentTimeMillis() - timeLastShifted > shiftDelay * 1000);
		}
		if (okToShift && !okToUpshift) {
			okToUpshift = true;
			timeAboveShift = System.currentTimeMillis();
		} else if (!okToShift && okToUpshift) {
			okToUpshift = false;
		}
		return (System.currentTimeMillis() - timeAboveShift > upTimeThresh * 1000 && okToShift);
	}

	/**
	 * Shift if necessary
	 */
	public void autoShift() {
		if (shouldUpshift()) {
			setLowGear(false);
		} else if (shouldDownshift()) {
			setLowGear(true);
		}
	}

	/**
	 * Simple helper function for clipping output to the -1 to 1 scale.
	 *
	 * @param in The number to be processed.
	 * @return That number, clipped to 1 if it's greater than 1 or clipped to -1 if it's less than -1.
	 */
	private static double clipToOne(double in) {
		if (in > 1)
			return 1;
		else if (in < -1)
			return -1;
		else
			return in;
	}
}