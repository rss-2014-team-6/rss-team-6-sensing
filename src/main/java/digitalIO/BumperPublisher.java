package digitalIO;

import orc.DigitalInput;
import orc.Orc;
import orc.OrcStatus;

import rss_msgs.BumpMsg;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

public class BumperPublisher {

    public static final String BUMP_MSG = "rss_msgs/BumpMsg";
    public static final String BUMP_CHANNEL = "/sense/Bump";

    private DigitalInput left;
    private DigitalInput right;
    private BumpMsg msg;
    private Publisher<BumpMsg> pub;

    public BumperPublisher(ConnectedNode node, Orc orc){
        left = new DigitalInput(orc, 0, true, true);
        right = new DigitalInput(orc, 1, true, true);
        pub = node.newPublisher(BUMP_CHANNEL, BUMP_MSG);
    }

    public void publish(final OrcStatus status){
        msg = pub.newMessage();
        msg.setLeft(left.getValue(status));
        msg.setRight(right.getValue(status));
        pub.publish(msg);
    }
}
