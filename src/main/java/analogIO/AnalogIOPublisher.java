package analogIO;

import orc.AnalogInput;
import orc.Orc;
import orc.OrcStatus;

import rss_msgs.AnalogStatusMsg;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

public class AnalogIOPublisher {

    ConnectedNode node;
    Orc orc;
    AnalogInput[] inputs = new AnalogInput[8];
    AnalogStatusMsg msg;
    Publisher<AnalogStatusMsg> pub;
	
    public AnalogIOPublisher(ConnectedNode node, Orc orc){
	this.node = node;
	this.orc = orc;
	for (int i = 0; i < 8; i++){
	    inputs[i] = new AnalogInput(orc, i);
	}
	pub = node.newPublisher("/sense/AnalogIO", "rss_msgs/AnalogStatusMsg");
    }
	
    public void publish(final OrcStatus status){
	msg = pub.newMessage();
        double[] values = new double[8];
        for (int i = 0; i < 8; i ++){
            values[i] = inputs[i].getVoltage(status);
        }
        msg.setValues(values);
        pub.publish(msg);
    }
}
