# Akka http examples


A set of examples for showing the streaming nature of Akka HTTP
when integrated with a system such as Cassandra or Kafka.

Two typed of endpoints are showcases:
* Future based, small payloads
* Stream based, large payloads from an external source 

To run the examples:
* Have a local Cassandra node on localhost:9042
* Run `ActivityGenerator` for a while, until nodetool shows a few hundred meg of data in your cluster.
* Start the server
* Start the client

The `ClientDriver` sends a HTTP request and streams the response. However it only
reads from the source when you type a number into StdIn.

To see server TCP send buffer fill up then the client TCP receive buffer fill up
use `ss` with:

```
watch -n 0.5 ss -tn 'dport == 8080 or sport = 8080'
```

The `Recv-Q` and `Send-Q` should fill up. The filling up of the client
`Recv-Q` will then cause the TCP Window to be reduced until 0. You can see
that with `tcpdump` or `wireshark` e.g.:

```
364217	683.939272836	127.0.0.1	127.0.0.1	TCP	66	[TCP ZeroWindow] 55022 → 8080 [ACK] Seq=93 Ack=934073 Win=0 Len=0 TSval=996409555 TSecr=996409555q
```

Then as you request more messages from the client space in the buffers will 
allow the window size to grow,the server send more data, until eventually
the server needs to pull more from Cassandra where you'll see this log:

```
[INFO] [11/21/2017 15:58:16.376]  Fetching more results from Cassandra
```

You've just witnessed akka streams flow control spanning processes via TCP.


### JVM args for Server App

# To allow getting native memory stats for threads
-XX:NativeMemoryTracking=summary
# TO show we don't need much memory
-Xmx256m 

### Useful things for demo

Gnome zoom - super alt 8



