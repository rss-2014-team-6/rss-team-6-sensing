package digitalIO;

import orc.DigitalInput;
import orc.Orc;
import orc.OrcStatus;

import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import rss_msgs.*;

public class BreakBeamPublisher {
	
    ConnectedNode node;
    Orc orc;
    DigitalInput input;
    BreakBeamMsg msg;
    Publisher<BreakBeamMsg> pub;

    public BreakBeamPublisher(ConnectedNode node, Orc orc) {
	this.node = node;
	this.orc = orc;
	input = new DigitalInput(orc, 7, false, false);
	pub = node.newPublisher("/sense/BreakBeam", "rss_msgs/BreakBeamMsg");
    }

    public void publish(final OrcStatus status) {
	msg = pub.newMessage();
        msg.setBeamBroken(input.getValue(status));
        pub.publish(msg);
    }
    
}
