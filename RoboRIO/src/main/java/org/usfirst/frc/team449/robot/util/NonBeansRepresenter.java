package org.usfirst.frc.team449.robot.util;

import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

/**
 * Created by noahg on 17-Jun-17.
 */
public class NonBeansRepresenter extends Representer{

	public NonBeansRepresenter(){
		System.out.println("how does this call super?");
	}

	protected class RepresentJavaBean implements Represent {
		public Node representData(Object data) {
			try {
				return representJavaBean(getProperties(data.getClass()), data);
			} catch (Exception e) {
				throw new YAMLException(e);
			}
		}
	}
}
