import java.util.LinkedList;

public class MMNServer extends SimpleServer{

  private int numProcessors;
  private LinkedList<Processor> processors = new LinkedList<>();

  public MMNServer(Timeline timeline, Double servTime, String name, int numProcessors){
    super(timeline, servTime, name);
    this.numProcessors = numProcessors;
    for (int i = 1; i <= numProcessors; i++){
      processors.add(new Processor(timeline, servTime, this.name+","+i, this));
    }
  }

  @Override
  public void receiveRequest(Event evt) {
    Request curRequest = evt.getRequest();
    curRequest.moveTo(this);

    curRequest.recordArrival(evt.getTimestamp());
    if(theQueue.isEmpty()){
      Processor p = getFreeProcessor(curRequest.getArrival());
      if (p != null){
        p.startService(evt, curRequest);
      }
    }
    theQueue.add(curRequest);
  }

  @Override
  public void releaseRequest(Event evt){
    EventGenerator next = super.getNext();

    Request curRequest = evt.getRequest();
    Request queueHead = theQueue.removeFirst();
    assert (curRequest == queueHead);


    if (next.getClass() != Sink.class){
      //RX FROM S0 TO S2
      System.out.println(evt.getRequest() + " FROM " + this + " TO " + next + ": " + evt.getTimestamp());
    }
    next.receiveRequest(evt);

    if(!theQueue.isEmpty()){
      Request nextRequest = theQueue.peekFirst();
      Processor p = getFreeProcessor(nextRequest.getArrival());
      if (p!= null){
        p.startService(evt, nextRequest);
      }
    }
  }

  private Processor getFreeProcessor(double arrTime){
    for (Processor p : processors){
      if (arrTime >= p.getNextFreeTime()){
        return p;
      }
    }
    return null;
  }

  @Override
  public void printStats(Double time){
    for (Processor p : processors){
      System.out.println(p + " UTIL: " + p.getUTIL(time));
    }
    System.out.println(this + " QLEN: " + this.getQLEN());
    System.out.println(this + " TRESP: " + this.getTRESP());
  }

}
