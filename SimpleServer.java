import java.util.LinkedList;

public class SimpleServer extends EventGenerator{

  private LinkedList<Request> theQueue = new LinkedList<Request>();
  private Double servTime;
  private String name;

  private double probExit;

  private Double cumulQ = new Double(0);
  private Double cumulW = new Double(0);
  private Double cumulTq = new Double(0);
  private Double cumulTw = new Double(0);
  private Double busyTime = new Double(0);
  private int snapCount = 0;
  private int servedReqs = 0;

  public SimpleServer(Timeline timeline, Double servTime, String name, double probExit){
    super(timeline);
    this.servTime = servTime;
    this.name = name;
    this.probExit = probExit;
  }

  private void startService(Event evt, Request curRequest){
    Event nextEvent = new Event(EventType.DEATH, curRequest,
        evt.getTimestamp()+Exp.getExp(1/this.servTime), this);

    curRequest.recordServiceStart(evt.getTimestamp());
    cumulTw += curRequest.getServiceStart() - curRequest.getArrival();

    System.out.println(curRequest + " START " + this.name + ": " + evt.getTimestamp());

    super.timeline.addEvent(nextEvent);
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

    assert super.next != null;

    double doesExit = Math.random();
    if (doesExit <= probExit){ //send to sink
      timeline.getSink().receiveRequest(evt);
    } else {
      System.out.println(evt.getRequest() + " NEXT " + super.next + ": " + evt.getTimestamp());
      super.next.receiveRequest(evt);
    }


    if(!theQueue.isEmpty()){
      Request nextRequest = theQueue.peekFirst();
      startService(evt, nextRequest);
    }
  }

  @Override
  public int getServedReqs(){
    return this.servedReqs;
  }
  @Override
  public double getCumulTw(){
    return this.cumulTw;
  }

  @Override
  public double getCumulTq(){
    return this.cumulTq;
  }

  @Override
  Double getRate(){
    return 1/this.servTime;
  }

  @Override
  public void executeSnapshot(){
    snapCount++;
    cumulQ += theQueue.size();
    cumulW += Math.max(theQueue.size()-1, 0);
  }

  @Override
  public void printStats(Double time){
    System.out.println("UTIL: " + busyTime/time);
    System.out.println("QLEN: " + cumulQ/snapCount);
    System.out.println("TRESP: " + cumulTq/servedReqs);
  }

  @Override
  public double getUTIL(Double time){
    return this.busyTime/time;
  }

  @Override
  public double getQLEN(){
    return this.cumulQ/snapCount;
  }

  @Override
  public double getTRESP(){
    return this.cumulTq/this.servedReqs;
  }

  @Override
  public double getTWAIT(){
    return this.cumulTw/this.servedReqs;
  }

  @Override
  public String toString(){
    return this.name;
  }
}
