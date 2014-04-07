package sonar;

import orc.Orc;
import orc.SRF02;

import rss_msgs.SonarMsg;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

public class SonarPublisher implements Runnable {
	ConnectedNode node;
	Orc orc;
	SRF02[] sonars;
	Publisher<SonarMsg> pub;
    // TODO: Fill in actual sonar addrs here
    final int[] sonarAddrs = {0x70, 0x72};
	Object lock;

    /*
	public static void main(String[] args){
		Orc orc = Orc.makeOrc();
		SRF02 one = new SRF02(orc);
		SRF02 two = new SRF02(orc, 0x72);
		while(true){
			System.out.println("distance for one:\t" + one.measure());
			System.out.println("distance for two:\t" + two.measure());
		}
	}
    */

	public SonarPublisher(ConnectedNode n, Orc o, Object lock){
		this.node = n;
		this.orc = o;
		this.lock = lock;
                sonars = new SRF02[sonarAddrs.length];
                for (int i = 0; i < sonarAddrs.length; i++) {
                    sonars[i] = new SRF02(orc, sonarAddrs[i]);
                }
                pub = node.newPublisher("/sense/Sonar", "rss_msgs/SonarMsg");
	}

	@Override public void run() {
		// TODO Auto-generated method stub
		SonarMsg msg = pub.newMessage();
                double[] measurements = new double[sonars.length];
		while(true){
			synchronized(lock) {
                            for (int i = 0; i < sonars.length; i++) {
                                measurements[i] = sonars[i].measure();
                            }
			}
                        msg.setSonarValues(measurements);
			pub.publish(msg);
		}
	}
}
