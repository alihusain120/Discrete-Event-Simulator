import java.util.LinkedList;

public class Simulator {

  private LinkedList<SimpleServer> resources = new LinkedList<SimpleServer>();
  private Timeline timeline = new Timeline();
  private Double now;

  public double getLambda() {
    return lambda;
  }

  public void setLambda(double lambda) {
    this.lambda = lambda;
  }

  private double lambda;

  public void addMonitoredResource(SimpleServer resource){
    this.resources.add(resource);
  }

  public Timeline getTimeline(){
    return this.timeline;
  }

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

    double TRESP = 0.0;
    double QTOT = 0.0;
    int totalSnapCount = 0;
    for (SimpleServer resource : resources){
      resource.printStats(simTime);
      QTOT += resource.getCumulQ() / resource.getSnapCount();
      totalSnapCount += resource.getSnapCount();
    }

    //TRESP = QTOT / lambda;
    TRESP = timeline.getSumTimesInSystem() / timeline.getCompletedRequests();
    System.out.println("QTOT: " + QTOT);
    System.out.println("TRESP: " + TRESP);

  }

  public static void main (String[] args){

    double simTime = Double.valueOf(args[0]);
    double lambda = Double.valueOf(args[1]);
    double s0ServTime = Double.valueOf(args[2]);
    double s1ServTime = Double.valueOf(args[3]);
    double s2ServTime = Double.valueOf(args[4]);
    double s3T1 = Double.valueOf(args[5]);
    double s3P1 = Double.valueOf(args[6]);
    double s3T2 = Double.valueOf(args[7]);
    double s3P2 = Double.valueOf(args[8]);
    double s3T3 = Double.valueOf(args[9]);
    double s3P3 = Double.valueOf(args[10]);
    int s2MaxLen = Integer.valueOf(args[11]);
    double probS0toS1 = Double.valueOf(args[12]);
    double probS0toS2 = Double.valueOf(args[13]);
    double probS3toSink = Double.valueOf(args[14]);
    double probS3toS1 = Double.valueOf(args[15]);
    double probS3toS2 = Double.valueOf(args[16]);

    Simulator sim = new Simulator();
    sim.setLambda(lambda);
    Source trafficSource = new Source(sim.timeline, lambda);

    Sink trafficSink = new Sink(sim.timeline);

    SimpleServer server0 = new SimpleServer(sim.timeline, s0ServTime, "S0");
    MMNServer server1 = new MMNServer(sim.timeline, s1ServTime, "S1", 2);
    MM1KServer server2 = new MM1KServer(sim.timeline, s2ServTime, "S2", s2MaxLen);
    MG1Server server3 = new MG1Server(sim.timeline, s3T1, s3P1, s3T2, s3P2, s3T3, s3P3, "S3");

    trafficSource.routeTo(server0, 1.0);

    server0.routeTo(server1, probS0toS1);
    server0.routeTo(server2, probS0toS2);

    server1.routeTo(server3, 1.0);
    server2.routeTo(server3, 1.0);

    server3.routeTo(trafficSink, probS3toSink);
    server3.routeTo(server1, probS3toS1);
    server3.routeTo(server2, probS3toS2);

    sim.addMonitoredResource(server0);
    sim.addMonitoredResource(server1);
    sim.addMonitoredResource(server2);
    sim.addMonitoredResource(server3);

    sim.simulate(simTime);

  }
}
