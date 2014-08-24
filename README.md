ClipSync
========

If you only want to synchronize the text-content from you clipboard between multiple systems, you can use this application! Just download and run the application and the text-content will be synchronized.

Download
--------
Checkout the [releases](https://github.com/rainu/ClipSync/releases). For each release a __runnable-jar-file__, a __zip-file__ and a __tar.gz-file__ will be created.

Usage
-----

This application works with simple client-server-connections. One of the application must be the server and the others must connect to them (they are the clients). It makes no difference if the application run in server- or client mode. On each system the clipboard text-content will be synchronized!

In the following you will see how you can configure a Simple szenario: two systems.

On system one we start the application as a server:
```
ClipSync/ $> ClipSync.sh -m server -l DEBUG
```
On system two we start the application as a client:
```
ClipSync/ $> ClipSync.sh <AddressOfServerSystem> -m client -l DEBUG
```

This is it! Now both systems synchronize their text-content from the clipboard between each other. If you have more then two systems you must decide which one is the server. After you have decide which one is the server, all other systems must only connect to them (client mode). Easy, isn't it ;)

Parameters
----------

| Short | Long   | Default value | Description                                                  |
| ----- |:------:|:-------------:| :------------------------------------------------------------|
| -p    | --port | 1310 | Define the port to listen (server mode) or to connect (client mode). |
| -t | --timeout | 5000 | Timeout for connection retries to the server (client mode) |
| -m | --mode | auto | Define the mode of the application. In __server__-Mode the application will starts a server. In __client__-Mode the application tries to connect to a running server. In __auto__-Mode the application try to connect to a server. If this try fails the application will start a server automatically. | 
| -lo | --loop | true | Should try to connect to a server again if it fails? |
| -i | --interval | 2000 | The check-clipboard-content-interval in millisecounds |
| -c | --clipboard-size | 524288 | The max size of clipboard content which should be synchronized. If tis maximum is reached the content will be cut up! |
| -l | --log | NONE(6) | Set the log-level for the application. TRACE (1), DEBUG (2), INFO (3), WARN (4), ERROR (5), NONE (6) |
