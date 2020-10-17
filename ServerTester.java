public class ServerTester {

  /*
  public static void main(String[] args){

    Simulator simTest = new Simulator();
    Source trafficSource = new Source(simTest.getTimeline(), 2.0);
    Sink trafficSink = new Sink(simTest.getTimeline());

    MMNServer mmn = new MMNServer(simTest.getTimeline(), 1.0, "MMN1", 2);
    trafficSource.routeTo(mmn, 1.0);
    mmn.routeTo(trafficSink, 1.0);
    simTest.addMonitoredResource(mmn);
    simTest.simulate(1000.0);


  }

   */
  public static void main(String[] args){
    Simulator simTest = new Simulator();
    Source trafficSource = new Source(simTest.getTimeline(), 2.0);
    Sink trafficSink = new Sink(simTest.getTimeline());

    MG1Server mg1Server = new MG1Server(simTest.getTimeline(), 3.0, .34,
        1.0, .4, 5.0, .26, "MG1");
    trafficSource.routeTo(mg1Server, 1.0);
    mg1Server.routeTo(trafficSink, 1.0);
    simTest.addMonitoredResource(mg1Server);
    simTest.simulate(1000.0);
  }
}
