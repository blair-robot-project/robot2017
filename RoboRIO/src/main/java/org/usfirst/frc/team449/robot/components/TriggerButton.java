package org.usfirst.frc.team449.robot.components;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import maps.org.usfirst.frc.team449.robot.oi.TriggerButtonMap;
import org.usfirst.frc.team449.robot.oi.components.SmoothedThrottle;
import org.usfirst.frc.team449.robot.oi.components.Throttle;

/**
 * Created by Noah Gleason on 3/5/2017.
 */
public class TriggerButton extends Button{

	private Throttle throttle;
	private double triggerAt;

	public TriggerButton(int port, int axis, double triggerAt){
		throttle = new SmoothedThrottle(new Joystick(port), axis);
		this.triggerAt = triggerAt;
	}

	public TriggerButton(TriggerButtonMap.TriggerButton map){
		this(map.getPort(), map.getAxis(), map.getTriggerAt());
	}

	@Override
	public boolean get() {
		return throttle.getValue() >= triggerAt;
	}
}
