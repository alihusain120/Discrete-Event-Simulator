import java.util.TreeMap;

public class MG1Server extends SimpleServer {

  private TreeMap<Double, Double> uniformServTimes = new TreeMap<>();
  private double totalProb = 0.0;

  //not generalized, only for ps4
  public MG1Server(Timeline timeline, double t1, double p1, double t2, double p2,
      double t3, double p3, String name){
    super(timeline, Math.min(Math.min(t1, t2), t3), name);
    this.uniformServTimes.put(t1, totalProb+=p1);
    this.uniformServTimes.put(t2, totalProb+=p2);
    this.uniformServTimes.put(t3, totalProb+=p3);
    this.name = name;
  }

  @Override
  protected void startService(Event evt, Request curRequest){
    Event nextEvent = new Event(EventType.DEATH, curRequest,
        evt.getTimestamp()+generateServTime(), this);

    curRequest.recordServiceStart(evt.getTimestamp());
    cumulTw += curRequest.getServiceStart() - curRequest.getArrival();

    System.out.println(curRequest + " START " + this + ": " + evt.getTimestamp());

    timeline.addEvent(nextEvent);
  }

  private double generateServTime(){
    double prob = Math.random();
    return this.uniformServTimes.ceilingEntry(prob).getValue();
  }

}
