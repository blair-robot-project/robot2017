package org.usfirst.frc.team449.robot.mechanism.intake;

import com.google.protobuf.Message;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import org.usfirst.frc.team449.robot.MappedSubsystem;
import org.usfirst.frc.team449.robot.components.UnitlessCANTalonSRX;
import org.usfirst.frc.team449.robot.mechanism.intake.commands.IntakeIn;
import org.usfirst.frc.team449.robot.oi.OI2017;

/**
 * Created by Justin on 1/28/2017.
 */
public class Intake extends MappedSubsystem {
	UnitlessCANTalonSRX fixed_talon;
	UnitlessCANTalonSRX actuated_talon;
	DoubleSolenoid piston;
	OI2017 oi;

	/**
	 * Creates a mapped subsystem and sets its map
	 *
	 * @param map the map of constants relevant to this subsystem
	 */
	public Intake(maps.org.usfirst.frc.team449.robot.mechanism.intake.IntakeMap.Intake map, OI2017 oi) {
		super(map.getMechanism());
		this.map = map;
		this.fixed_talon = new UnitlessCANTalonSRX(map.getFixedTalon());
		this.actuated_talon = new UnitlessCANTalonSRX(map.getActuatedTalon());
		this.piston = new DoubleSolenoid(map.getPiston().getForward(), map.getPiston().getReverse());
		this.oi = oi;
	}

	public void setPercentVbus(double percentVbus) {
		fixed_talon.setPercentVbus(percentVbus);
		actuated_talon.setPercentVbus(percentVbus);
	}

	public void setPiston(DoubleSolenoid.Value value) {
		piston.set(value);
	}

	/**
	 * Initialize the default command for a subsystem By default subsystems have
	 * no default command, but if they do, the default command is set with this
	 * method. It is called on all Subsystems by CommandBase in the users program
	 * after all the Subsystems are created.
	 */
	@Override
	protected void initDefaultCommand() {
		setDefaultCommand(new IntakeIn(this));
	}
}
