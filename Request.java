import java.util.HashMap;

public class Request {
  private EventGenerator at;
  private EventGenerator last;
  private int id;
  private static int unique_ID = 0;
  private HashMap<EventGenerator, Stats> stats = new HashMap<EventGenerator, Stats>();
  private Processor mmnProcessorBelongsTo = null;

  public Request (EventGenerator created_at){
    this.at = created_at;
    this.last = null;
    this.id = unique_ID;
    unique_ID++;


    this.stats.put(this.at, new Stats());
  }

  public void moveTo(EventGenerator at){
    this.last = this.at;
    this.at = at;
    this.stats.put(this.at, new Stats());
  }

  public EventGenerator where(){
    return this.at;
  }

  public EventGenerator last(){
    return this.last;
  }

  @Override
  public String toString(){
    return "R" + this.id;
  }

  public void recordArrival(Double timestamp){
    Stats curStats = this.stats.get(this.at); //Get stats object for current eventGenerator
    curStats.arrival = timestamp;
  }

  public void recordServiceStart(Double timestamp){
    Stats curStats = this.stats.get(this.at);
    curStats.serviceStart = timestamp;
    curStats.totalRuns++;
  }

  public void recordDeparture(Double timestamp){
    Stats curStats = this.stats.get(this.at);
    curStats.departure = timestamp;
  }

  public Double getArrival(){
    Stats curStats = this.stats.get(this.at); //Get stats object for current eventGenerator
    return curStats.arrival;
  }

  public Double getServiceStart(){
    Stats curStats = this.stats.get(this.at);
    return curStats.serviceStart;
  }

  public Double getDeparture(){
    Stats curStats = this.stats.get(this.at);
    return curStats.departure;
  }

  public Double getTotalRuns(){
    Stats curStats = this.stats.get(this.at);
    return curStats.totalRuns;
  }

  //MMN Server specific functions:
  public Processor getMmnProcessorBelongsTo() {
    return mmnProcessorBelongsTo;
  }

  public void setMmnProcessorBelongsTo(Processor mmnProcessorBelongsTo) {
    this.mmnProcessorBelongsTo = mmnProcessorBelongsTo;
  }
  //End MMn SErver specific functions

}
