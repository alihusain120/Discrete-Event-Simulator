import java.util.LinkedList;

public class SimpleServer extends EventGenerator{

  protected LinkedList<Request> theQueue = new LinkedList<Request>();
  protected Double servTime;
  protected String name;
  protected Double cumulQ = new Double(0);
  protected Double cumulW = new Double(0);
  protected Double cumulTq = new Double(0);
  protected Double cumulTw = new Double(0);
  protected Double busyTime = new Double(0);
  protected int snapCount = 0;
  protected int servedReqs = 0;

  public SimpleServer(Timeline timeline, Double servTime, String name){
    super(timeline);
    this.servTime = servTime;
    this.name = name;
  }

  protected void startService(Event evt, Request curRequest){
    Event nextEvent = new Event(EventType.DEATH, curRequest,
        evt.getTimestamp()+Exp.getExp(1/this.servTime), this);

    curRequest.recordServiceStart(evt.getTimestamp());
    cumulTw += curRequest.getServiceStart() - curRequest.getArrival();

    System.out.println(curRequest + " START " + this + ": " + evt.getTimestamp());

    timeline.addEvent(nextEvent);
  }

  @Override
  public void receiveRequest(Event evt){
    super.receiveRequest(evt);

    Request curRequest = evt.getRequest();

    curRequest.recordArrival(evt.getTimestamp());

    if(theQueue.isEmpty()){
      startService(evt, curRequest);
    }

    theQueue.add(curRequest);

  }

  @Override
  public void releaseRequest(Event evt){
    Request curRequest = evt.getRequest();

    Request queueHead = theQueue.removeFirst();
    assert curRequest == queueHead;

    curRequest.recordDeparture(evt.getTimestamp());

    busyTime += curRequest.getDeparture() - curRequest.getServiceStart();
    cumulTq += curRequest.getDeparture() - curRequest.getArrival();
    servedReqs++;

    EventGenerator next = getNext();
    if (next.getClass() != Sink.class){
      //RX FROM S0 TO S2
      System.out.println(evt.getRequest() + " FROM " + this.name + " TO " + next + ": " + evt.getTimestamp());
    }
    next.receiveRequest(evt);

    if(!theQueue.isEmpty()){
      Request nextRequest = theQueue.peekFirst();
      startService(evt, nextRequest);
    }
  }

  public int getServedReqs(){
    return this.servedReqs;
  }

  public void incrementServedReq() {
    this.servedReqs++;
  }

  public double getCumulTw(){
    return this.cumulTw;
  }

  public double getCumulTq(){
    return this.cumulTq;
  }

  @Override
  Double getRate(){
    return 1/this.servTime;
  }

  public void executeSnapshot(){
    snapCount++;
    cumulQ += theQueue.size();
    cumulW += Math.max(theQueue.size()-1, 0);
  }

  public void printStats(Double time){
    System.out.println(this + " UTIL: " + this.getUTIL(time));
    System.out.println(this + " QLEN: " + this.getQLEN());
    System.out.println(this + " TRESP: " + this.getTRESP());
  }

  public String getName(){
    return this.name;
  }

  public void setCumulTq(Double cumulTq) {
    this.cumulTq = cumulTq;
  }

  public int getSnapCount() {
    return snapCount;
  }

  public Double getCumulQ() {
    return cumulQ;
  }

  public double getUTIL(Double time){
    return this.busyTime/time;
  }

  public double getQLEN(){
    return this.cumulQ/snapCount;
  }

  public double getTRESP(){
    return this.cumulTq/this.servedReqs;
  }

  public double getTWAIT(){
    return this.cumulTw/this.servedReqs;
  }

  public String toString(){
    return this.name;
  }

  @Override
  public EventGenerator getNext(){
    return super.getNext();
  }
}
