package rs;

//#rs
public interface Subscription {
  public void request(long n);
  public void cancel();
}
//#rs