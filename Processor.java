public class Processor extends SimpleServer{

  private double nextFreeTime = 0.0;
  private MMNServer homeServer;
  public Processor(Timeline timeline, Double servTime, String name, MMNServer homeServer){
    super(timeline, servTime, name);
    this.homeServer = homeServer;
  }

  @Override
  protected void startService(Event evt, Request curRequest){
    this.nextFreeTime = evt.getTimestamp()+Exp.getExp(1/this.servTime);
    Event nextEvent = new Event(EventType.DEATH, curRequest,
        this.nextFreeTime, this);
    curRequest.recordServiceStart(evt.getTimestamp());
    cumulTw += curRequest.getServiceStart() - curRequest.getArrival();

    System.out.println(curRequest + " START " + this + ": " + evt.getTimestamp());

    timeline.addEvent(nextEvent);
  }



  @Override
  public void releaseRequest(Event evt){
    Request curRequest = evt.getRequest();
    curRequest.recordDeparture(evt.getTimestamp());

    busyTime += curRequest.getDeparture() - curRequest.getServiceStart();
    cumulTq += curRequest.getDeparture() - curRequest.getArrival();
    homeServer.setCumulTq(homeServer.getCumulTq()+this.cumulTq);
    servedReqs++;
    homeServer.incrementServedReq();

    homeServer.releaseRequest(evt);

  }

  public double getNextFreeTime(){
    return this.nextFreeTime;
  }

  public void setNextFreeTime(double time){
    this.nextFreeTime = time;
  }

  public String toString(){
    return this.name;
  }

}
