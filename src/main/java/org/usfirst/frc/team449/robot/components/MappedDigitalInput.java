package org.usfirst.frc.team449.robot.components;

import edu.wpi.first.wpilibj.DigitalInput;
import maps.org.usfirst.frc.team449.robot.components.DigitalInputMap;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by bryanli on 3/20/17.
 */
public class MappedDigitalInput {
    private DigitalInputMap.DigitalInput map;

    private List<DigitalInput> digitalInputs;


    public MappedDigitalInput(DigitalInputMap.DigitalInput map) {
        this.map = map;

        digitalInputs = new ArrayList<>();
        for (int portNum : map.getPortList()) {
            DigitalInput tmp = new DigitalInput(portNum);
            digitalInputs.add(tmp);
        }
    }

    public List<Boolean> getStatus(){
        List<Boolean> digitalValues = new ArrayList<>();
        for (int i = 0; i < digitalInputs.size(); i++) {
            digitalValues.add(digitalInputs.get(i).get());
        }
        return digitalValues;
    }
}