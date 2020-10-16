import java.util.LinkedList;

public class Simulator {

  private LinkedList<EventGenerator> resources = new LinkedList<EventGenerator>();
  private Timeline timeline = new Timeline();
  private Double now;

  public void addMonitoredResource(EventGenerator resource){
    this.resources.add(resource);
  }
  //a change was made here
  private void addMonitor(){

    Double monRate = Double.POSITIVE_INFINITY;

    for (int i = 0; i < resources.size(); i++){
      Double rate = resources.get(i).getRate();
      if (monRate > rate){
        monRate = rate;
      }
    }

    assert !monRate.equals(Double.POSITIVE_INFINITY);

    Monitor monitor = new Monitor(timeline, monRate, resources);
  }

  public void simulate(Double simTime){
    now = new Double(0);
    addMonitor();

    while (now < simTime){
      Event evt = timeline.popEvent();
      now = evt.getTimestamp();
      EventGenerator eventFrom = evt.getSource();
      eventFrom.processEvent(evt);
    }

    //Print stats
    for (EventGenerator resource : resources){
      System.out.println("UTIL " + resource + ": " + resource.getUTIL(simTime));
    }
    double TRESP = 0;
    double TWAIT = 0;
    double avgRuns = 0;
    for (EventGenerator resource : resources){
      System.out.println("QLEN " + resource + ": " + resource.getQLEN());
      TRESP += resource.getCumulTq();
      TWAIT += resource.getCumulTw();
      avgRuns += resource.getServedReqs();
    }

    TRESP /= timeline.getCompletedRequests();
    TWAIT /= timeline.getCompletedRequests();
    avgRuns /= timeline.getCompletedRequests();

    System.out.println("TRESP: " + TRESP);
    System.out.println("TWAIT: " + TWAIT);
    System.out.println(  "RUNS: " + avgRuns);

  }

  public static void main (String[] args){

    double simTime = Double.valueOf(args[0]);
    double lambda = Double.valueOf(args[1]);
    double serverOneTime = Double.valueOf(args[2]);
    double serverTwoTime = Double.valueOf(args[3]);
    double probExitFromOne = Double.valueOf(args[4]);
    double probExitFromTwo = 1.0 - Double.valueOf(args[5]);
    assert probExitFromOne + probExitFromTwo < 2.0;

    Simulator sim = new Simulator();

    Source trafficSource = new Source(sim.timeline, lambda);

    Sink trafficSink = new Sink(sim.timeline);

    SimpleServer server1 = new SimpleServer(sim.timeline, serverOneTime, "0", probExitFromOne);
    SimpleServer server2 = new SimpleServer(sim.timeline, serverTwoTime, "1", probExitFromTwo);

    trafficSource.routeTo(server1);
    server1.routeTo(server2);
    server2.routeTo(server1);

    sim.addMonitoredResource(server1);
    sim.addMonitoredResource(server2);

    sim.simulate(simTime);

  }
}
