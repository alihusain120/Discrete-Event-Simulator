import java.util.PriorityQueue;

public class Timeline {
  private PriorityQueue<Event> timeline = new PriorityQueue<Event>();
  private int completedRequests = 0;
  private double sumTimesInSystem = 0.0;
  private Sink endSink;

  public void addEvent(Event evt){
    timeline.add(evt);
  }

  public Event popEvent() {
    return timeline.poll();
  }

  public int getCompletedRequests(){
    return this.completedRequests;
  }

  public void addCompletedRequest(){
    this.completedRequests++;
    return;
  }

  public void incrementSumTimesInSystem(double time){
    this.sumTimesInSystem += time;
  }

  public double getSumTimesInSystem() {
    return sumTimesInSystem;
  }

  public void setEndSink(Sink endSink){
    this.endSink = endSink;
  }

  public Sink getSink(){
    return this.endSink;
  }

}
