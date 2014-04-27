package motion;

import orc.Orc;
import orc.OrcStatus;
import orc.QuadratureEncoder;

import rss_msgs.EncoderMsg;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

public class EncoderPublisher {

    ConnectedNode node;
    Orc orc;
    QuadratureEncoder left;
    QuadratureEncoder right;
    EncoderMsg msg;
    Publisher<EncoderMsg> pub;
    Object lock;
	
    public EncoderPublisher(ConnectedNode node, Orc orc) {
	this.node = node;
	left = new QuadratureEncoder(orc, 0, false);
	right = new QuadratureEncoder(orc, 1, true);
	pub = node.newPublisher("/sense/Encoder", "rss_msgs/EncoderMsg");
    }
    
    public void publish(final OrcStatus status) {
	msg = pub.newMessage();
        msg.setLeft(left.getPosition(status));
        msg.setRight(right.getPosition(status));
        pub.publish(msg);
    }

}
