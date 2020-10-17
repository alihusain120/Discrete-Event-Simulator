import java.util.TreeMap;

public class EventGenerator {

  protected Timeline timeline;
  private TreeMap<Double, EventGenerator> nextRoutes = new TreeMap<Double, EventGenerator>();
  private double totalProb = 0.0;

  public EventGenerator(Timeline timeline){
    this.timeline = timeline;
  }

  public void receiveRequest(Event evt){
    Request req = evt.getRequest();
    req.moveTo(this);
  }

  public void releaseRequest(Event evt){

  }

  public void routeTo(EventGenerator next, Double prob){
    this.nextRoutes.put(totalProb += prob, next);

  }

  public void setTimeline(Timeline timeline){
    this.timeline = timeline;
  }

  public void processEvent(Event evt){
    if (evt.getType() == EventType.BIRTH){
      receiveRequest(evt);
    } else if (evt.getType() == EventType.DEATH || evt.getType() == EventType.DROP){
      releaseRequest(evt);
    }
  }

  public EventGenerator getNext(){
    double nextProb = Math.random();
    return this.nextRoutes.ceilingEntry(nextProb).getValue();
  }

  Double getRate(){
    return Double.POSITIVE_INFINITY;
  }

}
