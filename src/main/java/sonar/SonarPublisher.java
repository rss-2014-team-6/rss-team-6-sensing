package sonar;

import orc.Orc;
import orc.OrcStatus;
import orc.SRF02;

import rss_msgs.SonarMsg;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

public class SonarPublisher implements Runnable {
    ConnectedNode node;
    Orc orc;
    SRF02[] sonars;
    Publisher<SonarMsg> pub;
    //addresses should be entered in clockwise order starting at pos x axis. 
    //addresses are 7-bit (0x70 to 0x7F) which correspond to the 8-bit ids (0xE0 to 0xFE)
    //(drop the last bit)
    //to set an address:
    //with only one sonar on I2C, send the following three commands (and no others) to the old device id:
    //0xA0, 0xAA, 0xA5, then send the new desired id. (8 bit version)
    //seems like you could use i2cTransaction in Orc (package orc) to send commands, 
    //but honestly it's a little confusing to me. You might be better off downloading the nice software from the web.
    // {front, back, left, right}
    final int[] sonarAddrs = {0x70, 0x72, 0x74, 0x76}; // testing with four sonars for now since these are the addresses we have
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

    @Override
    public void run() {
        while(true) {
            SonarMsg msg = pub.newMessage();
            double[] measurements = new double[sonars.length];
            synchronized(lock) {
                for (int i = 0; i < sonars.length; i++) {
                    sonars[i].ping();
                }
            }
            try {
                Thread.sleep(70);
            } catch (InterruptedException e) {}
            synchronized(lock) {
                for (int i = 0; i < sonars.length; i++) {
                    measurements[i] = sonars[i].readRange();
                }
            }

            msg.setSonarValues(measurements);
            pub.publish(msg);
            try {
                Thread.sleep(50);
            }
            catch (InterruptedException e) {}
        }
    }
}
