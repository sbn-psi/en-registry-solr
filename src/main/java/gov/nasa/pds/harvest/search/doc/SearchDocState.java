package gov.nasa.pds.harvest.search.doc;

public class SearchDocState {

  private int counter = -1;
  
  public int getCounter() {
    return this.counter;
  }

  public void setCounter(int counter) {
    this.counter = counter;
  }

  public void incrementCounter() {
    this.counter++;
  }
  
}
