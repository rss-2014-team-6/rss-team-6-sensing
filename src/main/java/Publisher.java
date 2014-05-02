import motion.EncoderPublisher;
import orc.Orc;
import orc.OrcStatus;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;

import sonar.SonarPublisher;
import analogIO.AnalogIOPublisher;
import digitalIO.BreakBeamPublisher;
import digitalIO.BumperPublisher;
import digitalIO.DigitalIOPublisher;

public class Publisher extends AbstractNodeMain implements Runnable {
    Orc orc;
    Object lock = new Object();

    DigitalIOPublisher digitalPub;
    BreakBeamPublisher breakBeamPub;
    BumperPublisher bumpPub;
    AnalogIOPublisher analogPub;
    EncoderPublisher encoderPub;
    SonarPublisher sonarPub;

    Thread publisherThread;
    Thread sonarThread;

    @Override
    public void run() {
        while (true) {
            final OrcStatus status;
            synchronized(lock) {
                status = orc.getStatus();
            }
            digitalPub.publish(status);
            breakBeamPub.publish(status);
            bumpPub.publish(status);
            analogPub.publish(status);
            encoderPub.publish(status);

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {}
        }
    }

    @Override
    public void onStart(ConnectedNode node) {

        orc = Orc.makeOrc();
        if (orc.isSim()) {
            orc.setNode(node);
        }

        digitalPub = new DigitalIOPublisher(node, orc);
        breakBeamPub = new BreakBeamPublisher(node, orc);
        bumpPub = new BumperPublisher(node, orc);
        analogPub = new AnalogIOPublisher(node, orc);
        encoderPub = new EncoderPublisher(node, orc);
        sonarPub = new SonarPublisher(node, orc, lock);
        sonarThread = new Thread(sonarPub);

        //spawn a thread to publish every 50ms or so
        publisherThread = new Thread(this);
        publisherThread.start();
        //sonars go on their own thread, since they have to ping
        //separately
        sonarThread.start();
    }

    @Override
    public void onShutdown(Node node) {
        node.shutdown();
        publisherThread.stop();
        sonarThread.stop();
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("rss/uorc_publisher");
    }
}
