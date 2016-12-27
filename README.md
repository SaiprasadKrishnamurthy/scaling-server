## An anatomy of servers ##
* How do the webservers work?
* Why does thread-based concurrency not scale?
* How can event-based/non-blocking servers shine?


## Prerequisites ##
* Java 8

## Types of server implementations ##
| Concurrency                    | Blocking or Non Blocking              |
|--------------------------------|---------------------------------------|
| Single Threaded                | Blocking                              |
| SingleThreaded                 | Non Blocking                          |
| MultiThreaded                  | Blocking                              |
| Multithreaded                  | Non Blocking                          |


## Handy commands I used on OS/X to increase the no of open sockets ##
```
sudo sysctl -w kern.maxfiles=1000200
sudo sysctl -w kern.maxfilesperproc=1000100
sudo ulimit -n 1000000
```



