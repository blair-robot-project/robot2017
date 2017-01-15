package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.PIDCommand;
import edu.wpi.first.wpilibj.command.Scheduler;
import maps.org.usfirst.frc.team449.robot.components.AnglePIDMap;
import org.usfirst.frc.team449.robot.components.PIDAngleCommand;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;
import org.usfirst.frc.team449.robot.oi.OISubsystem;

/**
 * Program created by noah on 1/14/17.
 */
public class NavXDriveStraight extends PIDAngleCommand{

	private OISubsystem oi;
	private TalonClusterDrive drive;
	private double sp;

	public NavXDriveStraight(AnglePIDMap.AnglePID map, TalonClusterDrive drive, OISubsystem oi){
		super (map, drive);
		this.oi = oi;
		this.drive = drive;
		requires(drive);
	}

	/**
	 * Uses the value that the pid loop calculated. The calculated value is the
	 * "output" parameter. This method is a good time to set motor values, maybe
	 * something along the lines of
	 * <code>driveline.tankDrive(output, -output)</code>
	 * <p>
	 * <p>
	 * All subclasses of {@link PIDCommand} must override this method.
	 * </p>
	 * <p>
	 * <p>
	 * This method will be called in a different thread then the {@link Scheduler}
	 * thread.
	 * </p>
	 *
	 * @param output the value the pid loop calculated
	 */
	@Override
	protected void usePIDOutput(double output) {
		drive.setDefaultThrottle(oi.getDriveAxisRight()+output, oi.getDriveAxisRight()-output); //Yes these should both be right, it's driveStraight
	}

	/**
	 * The initialize method is called the first time this Command is run after
	 * being started.
	 */
	@Override
	protected void initialize() {
		this.getPIDController().setSetpoint(this.returnPIDInput());
		this.getPIDController().enable();
	}

	/**
	 * The execute method is called repeatedly until this Command either finishes
	 * or is canceled.
	 */
	@Override
	protected void execute() {
		drive.logData();
	}

	/**
	 * Returns whether this command is finished. If it is, then the command will
	 * be removed and {@link Command#end() end()} will be called.
	 * <p>
	 * <p>
	 * It may be useful for a team to reference the {@link Command#isTimedOut()
	 * isTimedOut()} method for time-sensitive commands.
	 * </p>
	 * $
	 *
	 * @return whether this command is finished.
	 * @see Command#isTimedOut() isTimedOut()
	 */
	@Override
	protected boolean isFinished() {
		return false;
	}

	/**
	 * Called when the command ended peacefully. This is where you may want to
	 * wrap up loose ends, like shutting off a motor that was being used in the
	 * command.
	 */
	@Override
	protected void end() {
		System.out.println("NavXDriveStraight end");
		this.getPIDController().disable();
	}

	/**
	 * Called when the command ends because somebody called
	 * {@link Command#cancel() cancel()} or another command shared the same
	 * requirements as this one, and booted it out.
	 * <p>
	 * <p>
	 * This is where you may want to wrap up loose ends, like shutting off a motor
	 * that was being used in the command.
	 * </p>
	 * <p>
	 * <p>
	 * Generally, it is useful to simply call the {@link Command#end() end()}
	 * method within this method
	 * </p>
	 */
	@Override
	protected void interrupted() {
		System.out.println("NavXDriveStraight interrupted!");
		this.getPIDController().disable();
	}
}
