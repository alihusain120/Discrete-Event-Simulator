
public class Sink extends EventGenerator{

  public Sink(Timeline timeline){
    super(timeline);
    timeline.setEndSink(this);
  }

  @Override
  public void receiveRequest(Event evt){
    super.receiveRequest(evt);
    Request curRequest = evt.getRequest();
    if (evt.getType() == EventType.DEATH){
      super.timeline.addCompletedRequest();
      super.timeline.incrementSumTimesInSystem(evt.getTimestamp() - curRequest.getEntryTime());
      System.out.println(evt.getRequest() + " DONE " + evt.getRequest().last() + ": " + evt.getTimestamp());
    } else if (evt.getType() == EventType.DROP){
      System.out.println(evt.getRequest() + " DROP " + evt.getRequest().last() + ": " + evt.getTimestamp());
    }
  }
}
