import java.util.LinkedList;
import java.util.NoSuchElementException;

public class MMNServer extends SimpleServer {

  private LinkedList<Processor> processors = new LinkedList<>();
  public MMNServer(Timeline timeline, Double servTime, String name, int numProcessors){
    super(timeline, servTime, name);
    for (int i = 1; i <= numProcessors; i++){
      processors.add(new Processor(timeline, this.name+","+i, servTime, this));
    }
  }


  @Override
  public void receiveRequest(Event evt){
    Request req = evt.getRequest();
    req.moveTo(this);

    req.recordArrival(evt.getTimestamp());
    if (theQueue.isEmpty()){
      Processor p = getFreeProcessor(evt.getTimestamp());
      if (p != null){
        p.receiveRequest(evt);
      } else {
        theQueue.add(req);
      }
    } else{
      theQueue.add(req);
    }
  }


  public Request giveNext(){
    if (!theQueue.isEmpty()){
      return theQueue.removeFirst();
    } else {
      return null;
    }

  }

  public Processor getFreeProcessor(double time){
    if (processors.get(0).isFree() && processors.get(1).isFree()){ //if both free choose randomly
      double rand = Math.random();
      if (rand <= .5){
        return processors.get(0);
      } else {
        return processors.get(1);
      }
    } else if (processors.get(0).isFree()){
      return processors.get(0);
    } else if (processors.get(1).isFree()){
      return processors.get(1);
    } else {
      return null;
    }
  }

  @Override
  public void executeSnapshot(){
    snapCount++;
    cumulQ += theQueue.size();
    for (Processor p : processors){
      cumulQ += (p.isFree() ? 0.0:1.0);
    }
    cumulW += Math.max(theQueue.size()-1, 0);
  }

  @Override
  public void routeTo(EventGenerator next, Double prob){
    for (Processor p : processors){
      p.routeTo(next, prob);
    }
    super.routeTo(next, prob);
  }

  public void incCumulTq(double value){
    this.cumulTq += value;
  }

  @Override
  public double getTRESP(){
    return this.cumulTq / this.servedReqs;
  }

  @Override
  public double getQLEN(){
    return this.cumulQ/snapCount;
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
