import javax.print.DocFlavor.READER;

public class Source extends EventGenerator{

  private Double rate;

  public Source(Timeline timeline, Double lambda){
    super(timeline);
    this.rate = lambda;

    Request firstRequest = new Request(this);
    Event firstEvent = new Event(EventType.BIRTH, firstRequest, new Double(0), this);

    super.timeline.addEvent(firstEvent);
  }

  @Override
  public void receiveRequest(Event evt){
    Request curRequest = evt.getRequest();
    curRequest.recordArrival(evt.getTimestamp());

    Request nextReq = new Request(this);
    Event nextEvent = new Event(EventType.BIRTH, nextReq,
        evt.getTimestamp()+Exp.getExp(this.rate), this);

    System.out.println(evt.getRequest() + " ARR: " + evt.getTimestamp());

    super.timeline.addEvent(nextEvent);

    super.getNext().receiveRequest(evt);
  }

  @Override
  Double getRate(){
    return this.rate;
  }

}
