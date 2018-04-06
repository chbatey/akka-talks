## Demo time line

~ 15 minutes

- Show that code in slides is pulled from the demo.

## Scalability 

- Heap is set to something small e.g. 128Mb
- Threads are scaled down as not to use all the cores of the laptop. See application.conf
- Open Mission Control to show threads and memory usage
- Gatling: 500 concurrent requests with ~5 threads. Under 100Mb memory.


## Flow control

- Introduce ClientDriver, explain why we need something to control demand to demo this
- Show code for Client. Explain use of Akka Streams 
- Show code for Server. Explain use of Akka Streams

#### Outcomes

- Constant memory foot print. Even for large requests. 
- Preventing unnecessary work. Only pulling rows from the database if there is client demand. 
- Translating Akka Streams flow control into TCP flow control and back again.
