public class Processor extends EventGenerator {

  private boolean isFree;
  private MMNServer homeServer;
  private double servTime;
  private double busyTime = 0.0;
  private double cumulTq = 0.0;
  private int numServed = 0;
  private String name;

  public Processor(Timeline timeline, String name, double servTime, MMNServer homeServer){
    super(timeline);
    this.name = name;
    this.servTime = servTime;
    this.homeServer = homeServer;
    this.isFree = true;
  }

  public void startService(Event evt, Request curRequest){
    this.isFree = false;
    Event nextEvent = new Event(EventType.DEATH, curRequest,
        evt.getTimestamp()+Exp.getExp(1/this.servTime), this);

    curRequest.recordServiceStart(evt.getTimestamp());

    System.out.println(curRequest + " START " + this + ": " + evt.getTimestamp());

    timeline.addEvent(nextEvent);
  }


  @Override
  public void receiveRequest(Event evt){
    super.receiveRequest(evt);
    Request curRequest = evt.getRequest();
    startService(evt, curRequest);
  }

  @Override
  public void releaseRequest(Event evt){
    Request curRequest = evt.getRequest();
    curRequest.recordDeparture(evt.getTimestamp());
    busyTime += curRequest.getDeparture() - curRequest.getServiceStart();
    homeServer.setCumulTq(homeServer.getCumulTq() + curRequest.getDeparture() - curRequest.getArrival());
    this.cumulTq += curRequest.getDeparture() - curRequest.getArrival();
    homeServer.incrementServedReq();
    this.numServed++;

    EventGenerator next = getNext();
    if (next.getClass() != Sink.class){
      //RX FROM S0 TO S2
      System.out.println(evt.getRequest() + " FROM " + this.homeServer + " TO " + next + ": " + evt.getTimestamp());
    }
    next.receiveRequest(evt);

    this.isFree = true;

    Request nextRequest = homeServer.giveNext();
    if (nextRequest != null){
      nextRequest.moveTo(this);
      startService(evt, nextRequest);
    }
  }

  public boolean isFree() {
    return isFree;
  }

  public double getTRESP(){
    return this.cumulTq / this.numServed;
  }

  public double getUTIL(double time){
    //System.out.println("PRINTING UTIL OF " + this + " busyTime=" + busyTime + " time= " + time);
    return this.busyTime / time;

  }
  public String toString(){
    return this.name;
  }

}
