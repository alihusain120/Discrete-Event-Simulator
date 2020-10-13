import java.util.LinkedList;

public class Monitor extends EventGenerator{

  private Double rate;
  private Simulator sim;
  private LinkedList<EventGenerator> resources;

  public Monitor(Timeline timeline, Double lambda, LinkedList<EventGenerator> resources){
    super(timeline);
    this.rate = lambda;
    this.resources = resources;

    Event firstEvent = new Event(EventType.MONITOR, null, new Double(0), this);

    super.timeline.addEvent(firstEvent);
  }

  @Override
  public void processEvent(Event evt){
    Event nextEvent = new Event(EventType.MONITOR, null,
        evt.getTimestamp()+Exp.getExp(this.rate), this);

    for (int i = 0; i < resources.size(); ++i){
      resources.get(i).executeSnapshot();
    }

    super.timeline.addEvent(nextEvent);
  }

  @Override
  Double getRate(){
    return this.rate;
  }
}
