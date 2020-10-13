
public class EventGenerator {

  protected Timeline timeline;
  protected EventGenerator next;

  public EventGenerator(Timeline timeline){
    this.timeline = timeline;
  }

  public void receiveRequest(Event evt){
    Request req = evt.getRequest();
    req.moveTo(this);
  }

  public void releaseRequest(Event evt){

  }

  public void routeTo(EventGenerator next){
    this.next = next;
  }

  public void setTimeline(Timeline timeline){
    this.timeline = timeline;
  }

  public void processEvent(Event evt){
    if (evt.getType() == EventType.BIRTH){
      receiveRequest(evt);
    } else if (evt.getType() == EventType.DEATH){
      releaseRequest(evt);
    }
  }

  Double getRate(){
    return Double.POSITIVE_INFINITY;
  }

  public void executeSnapshot(){

  }

  public String getName(){
    return null;
  }

  public double getUTIL(Double time){
    return Double.POSITIVE_INFINITY;
  }

  public double getQLEN(){
    return Double.POSITIVE_INFINITY;
  }

  public double getTRESP(){
    return Double.POSITIVE_INFINITY;
  }

  public double getTWAIT(){
    return Double.POSITIVE_INFINITY;
  }

  public double getCumulTw(){
    return Double.POSITIVE_INFINITY;
  }

  public double getCumulTq(){
    return Double.POSITIVE_INFINITY;
  }

  public int getServedReqs(){
    return Integer.MAX_VALUE;
  }

  public void printStats(Double time){

  }
}
