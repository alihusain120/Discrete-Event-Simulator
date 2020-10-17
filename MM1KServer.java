public class MM1KServer extends SimpleServer {

  private int queueLength;
  private int numDropped = 0;

  public MM1KServer(Timeline timeline, Double servTime, String name, int queueLength){
    super(timeline, servTime, name);
    this.queueLength = queueLength;
  }

  @Override
  public void receiveRequest(Event evt){

    Request curRequest = evt.getRequest();
    curRequest.moveTo(this);
    curRequest.recordArrival(evt.getTimestamp());

    if(this.theQueue.isEmpty()){
      startService(evt, curRequest);
    } else if (this.theQueue.size() == this.queueLength){
      ++numDropped;
      super.timeline.addEvent(new Event(EventType.DROP, curRequest, evt.getTimestamp(), this));
      return;
    }

    //this should be in an else
    this.theQueue.add(curRequest);

  }

  @Override
  public void releaseRequest(Event evt){
    if (evt.getType() == EventType.DEATH){
      super.releaseRequest(evt);
    } else if (evt.getType() == EventType.DROP){
      timeline.getSink().receiveRequest(evt);
    }
  }

  @Override
  public void printStats(Double time){
    super.printStats(time);
    System.out.println(this + " DROPPED: " + numDropped);
  }
}
