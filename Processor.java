public class Processor{

  private double nextFreeTime = 0.0;
  private MMNServer homeServer;
  private double busyTime;
  private String name;

  public Processor(String name, MMNServer homeServer){
    this.name = name;
    this.homeServer = homeServer;
  }


  public double getNextFreeTime(){
    return this.nextFreeTime;
  }

  public void addNextFreeTime(double time){

    this.nextFreeTime += time;
  }

  public void addBusyTime(double time){
    this.busyTime += time;
  }

  public double getUTIL(double time){
    //System.out.println("PRINTING UTIL OF " + this + " busyTime=" + busyTime + " time= " + time);
    return this.busyTime / time;

  }
  public String toString(){
    return this.name;
  }

}
