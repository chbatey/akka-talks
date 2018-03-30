package rs;

//#rs
public interface Processor<T, R> extends Subscriber<T>, Publisher<R> {
}
//#rs