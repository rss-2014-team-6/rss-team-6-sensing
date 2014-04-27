package digitalIO;

import orc.DigitalInput;
import orc.Orc;
import orc.OrcStatus;

import rss_msgs.DigitalStatusMsg;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

public class DigitalIOPublisher {

    ConnectedNode node;
    Orc orc;
    DigitalInput[] slowInputs = new DigitalInput[8];
    DigitalInput[] fastInputs = new DigitalInput[8];
    DigitalStatusMsg msg;
    Publisher<DigitalStatusMsg> pub;

    public DigitalIOPublisher(ConnectedNode node, Orc orc){
	this.node = node;
	this.orc = orc;
	for (int i = 0; i < 16; i++){
	    if(i < 8){
		slowInputs[i] = new DigitalInput(orc, i, false, false); 
	    } else {
		int j = i - 8;
		fastInputs[j] = new DigitalInput(orc, i, false, false);
	    }
	}
	pub = node.newPublisher("/sense/DigitalStatus", "rss_msgs/DigitalStatusMsg");
    }


    public void publish(final OrcStatus status) {
	msg = pub.newMessage();
        boolean[] fast = new boolean[8];
        boolean[] slow = new boolean[8];
        for (int i = 0; i <8; i ++) {
            fast[i] = fastInputs[i].getValue(status);
            slow[i] = slowInputs[i].getValue(status);
        }
        msg.setFast(fast);
        msg.setSlow(slow);
        pub.publish(msg);
    }
}
