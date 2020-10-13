
public class Sink extends EventGenerator{

  public Sink(Timeline timeline){
    super(timeline);
    timeline.setEndSink(this);
  }

  @Override
  public void receiveRequest(Event evt){
    super.receiveRequest(evt);
    super.timeline.addCompletedRequest();
    System.out.println(evt.getRequest() + " DONE " + evt.getRequest().last() + ": " + evt.getTimestamp());
  }
}
