## An anatomy of servers ##
How do the webservers work?
Why does thread-based concurrency not scale?
How can event-based/non-blocking servers shine?


## Prerequisites ##
* Java 8
## Types of server implementations ##
| Concurrency | Blocking or Non Blocking |
| Single Threaded | Blocking |
| MultiThreaded | Blocking |
| SingleThreaded | Non Blocking |
| Multithreaded | Non Blocking |


sudo sysctl -w kern.maxfiles=1000200
sudo sysctl -w kern.maxfilesperproc=1000100
sudo ulimit -n 1000000




