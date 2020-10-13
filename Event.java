public class Event implements Comparable<Event>{

  private EventType type;
  private Double timestamp;
  private Request rq;
  private EventGenerator source;

  public Event(EventType type, Request rq, Double timestamp, EventGenerator source){
    super();
    this.type = type;
    this.timestamp = timestamp;
    this.rq = rq;
    this.source = source;
  }

  public Event(Event evt, Double IAT){
    super();
    this.type = evt.type;
    this.timestamp = evt.timestamp + Exp.getExp(1/IAT);
    this.rq = evt.rq;
  }

  @Override
  public int compareTo(Event evt){
    return this.getTimestamp().compareTo(evt.getTimestamp());
  }

  public Double getTimestamp(){
    return this.timestamp;
  }

  public EventType getType(){
    return this.type;
  }

  public Request getRequest(){
    return this.rq;
  }

  public EventGenerator getSource(){
    return this.source;
  }

  @Override
  public String toString(){
    return this.rq.toString() + this.type + ": " + this.timestamp;
  }

}
