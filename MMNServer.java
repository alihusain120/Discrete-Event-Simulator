import java.util.LinkedList;

public class MMNServer extends SimpleServer{

  private int numProcessors;
  private LinkedList<Processor> processors = new LinkedList<>();

  public MMNServer(Timeline timeline, Double servTime, String name, int numProcessors){
    super(timeline, servTime, name);
    this.numProcessors = numProcessors;
    for (int i = 1; i <= numProcessors; i++){
      processors.add(new Processor(this.name+","+i, this));
    }
  }

  @Override
  protected void startService(Event evt, Request curRequest){
    double nextTime = Exp.getExp(1/this.servTime);
    Event nextEvent = new Event(EventType.DEATH, curRequest,
        evt.getTimestamp()+nextTime, this);

    curRequest.recordServiceStart(evt.getTimestamp());
    cumulTw += curRequest.getServiceStart() - curRequest.getArrival();

    System.out.println(curRequest + " START " +
        curRequest.getMmnProcessorBelongsTo() + ": " + evt.getTimestamp());

    curRequest.getMmnProcessorBelongsTo().addNextFreeTime(nextTime);

    timeline.addEvent(nextEvent);
  }

  @Override
  public void receiveRequest(Event evt) {
    Request curRequest = evt.getRequest();
    curRequest.moveTo(this);

    curRequest.recordArrival(evt.getTimestamp());
    if(theQueue.size() < 2){
      Processor p = getFreeProcessor(curRequest.getArrival());
      if (p != null){
        curRequest.setMmnProcessorBelongsTo(p);
        startService(evt, curRequest);
      }
    }
    theQueue.add(curRequest);
  }

  @Override
  public void releaseRequest(Event evt){

    Request curRequest = evt.getRequest();
    Request queueHead = theQueue.removeFirst();
    assert (curRequest == queueHead);
    curRequest.recordDeparture(evt.getTimestamp());

    busyTime += curRequest.getDeparture() - curRequest.getServiceStart();
    curRequest.getMmnProcessorBelongsTo().addBusyTime(curRequest.getDeparture() - curRequest.getServiceStart());
    cumulTq += curRequest.getDeparture() - curRequest.getArrival();
    servedReqs++;

    EventGenerator next = super.getNext();
    if (next.getClass() != Sink.class){
      //RX FROM S0 TO S2
      System.out.println(evt.getRequest() + " FROM " + this + " TO " + next + ": " + evt.getTimestamp());
    }
    next.receiveRequest(evt);

    if(theQueue.size() >= 2){
      Request nextRequest = theQueue.peekFirst();
      Processor p = getFreeProcessor(nextRequest.getArrival());
      if (p!= null){
        nextRequest.setMmnProcessorBelongsTo(p);
        startService(evt, nextRequest);
      }
    }
  }

  private Processor getFreeProcessor(double arrTime){
    double lowestNextFreeTime = 0.0;
    Processor toReturn =null;
    for (Processor p : processors){
      if (p.getNextFreeTime() <= lowestNextFreeTime){
        lowestNextFreeTime = p.getNextFreeTime();
        toReturn = p;
      }
      /*
      if (arrTime >= p.getNextFreeTime()){
        System.out.println("ASSIGNING TO " + p + " ArrTime=" + arrTime + " nextFreeTime= "+ p.getNextFreeTime());
        return p;
      }
       */
    }
    return toReturn;
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
