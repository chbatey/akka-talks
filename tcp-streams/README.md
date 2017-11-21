# Simple example of TCP back pressure

Server opens a socket and reads data at 2 bytes per 2 seconds.

Two clients that both try to send a large chunk of bytes every 10 milliseconds.

`Client` listens to the TCP back pressure and stops sending due to the slow
server.

`StupidClient` carries on trying to send even when the server is slow.