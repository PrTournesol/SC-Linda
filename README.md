# SC-Linda

**System for sharing typed data as tuples inspired from the Linda model (or TSpaces)**

Authors
* Damien Kleiber
* Philippe Leleux
* Arthur Manoha

##Presentation
###What's a memory space of type Linda?
Data base (memory space) handling saving and sending tuples to clients. A tuple is a n-uplet composed of elements of diverse types (ex: *[ 'a' 21 [ 'z' 51 ] "voiture"]*). An element can be a type itself (ex: *?Integer*) which is a wild card for any element of this type.
A memory space of tuples allows to share data of diverse types with others locally, on a server or through a network of servers.

###Three implementations
* **Shared memory** (shm): all users share the same local memory where the code runs,
* **Server** (server): access from a distant server centralising the tuples memory,
* **Multi-server** (server): each **Client** access to a unique server in which he writes the tuples and all servers are connected to each other. When the **Client** reads it will look in his server but in case of tuple not found, the server propagate the query.

*N.B.:*
* In the first implementation, the **Server** and **Clients** are all created from the same main and run in the same processus,
* In both latter, the **Server** in run on a machine and communicate through a certain port (default 6060). To send queries to the server, the **Client** send their request to its address and port.

###Elementary methods:
* **write**: add a tuple in the memory space, a tuple can be present more than once,
* **read**: look for a tuple in the memory space and send it, without erasing it,
* **take**: idem but erase the tuple before sending it,
* **tryTake** and **tryRead**: non-blocking versions of **read** and **take**, return null if tuple not present,
* **takeAll** and **readAll** : same as **tryTake** and **tryRead** but acting on all occurences of the searched tuple,
* **eventRegister** : subscribe immediately to the occurence of a tuple. It calls a **Callback** which will take the tuple out of the memory space until the **CallBack** ends.

*N.B.:*
* **read** and **take** are blocking functions: if the wanted tuple is not present, the client is blocked in waiting state until a corresponding tuple is added.
* If there are several actions in conflicts on a same tuple at the same time, the **CallBacks** are prioritary then **read** and finally **take**.

###Launch
To make this application work, you need to launch the server (or **Server**) first, then launch the **IHM** which automatically create a **Client** object. The mains for each implementation are in the files:
* Shared memory:
..* **Client** and **Server**: *shm/MainShm.java*
* Server:
..* **Client**: *server/MainPrClient.java*
..* **Server**: *server/MainPrServer.java*
* Multi-Server:
..* **Client**: *server/MainPrClient.java*
..* **Server**: *server/MainPrMultiServer.java*

##Details
###Shared memory
![Shared memory implementation architecture](https://github.com/pleleux-enseeiht/SC-Linda/blob/master/Graphics/Architecture%20Cenralized%20Linda.png "Shared memory implementation architecture")

The user sees the **IHM** (Interface Human Machine) which allows to enter a tuple and access all actions of the Client element. The client then make the queries to the Shared Memory space: **Centralized Linda** (**CL**). Several Clients can access **CL** at the same time

###Server
![Server implementation architecture](https://github.com/pleleux-enseeiht/SC-Linda/blob/master/Graphics/Architecture%20Serveur%20Linda.png "Server implementation architecture")

For the user it's all the same. The **Server** is only receiving the requests from the **Client** and forwarding the request to an internal **CentralizedLinda** as in the shared memory version.

###Multi-Server
![Multi-server implementation architecture](https://github.com/pleleux-enseeiht/SC-Linda/blob/master/Graphics/Architecture%20Multi-Serveur%20Linda.png "Multi-server implementation architecture")

While the servers are interconnected, some can be not connected, each server memorizes the ID of all servers he is connected to. Ex:
    ![Multi-server structure example](https://github.com/pleleux-enseeiht/SC-Linda/blob/master/Graphics/Sch%C3%A9ma%20multi-serveur.png "Multi-server structure example")

The callbacks are then working as follows:
    ![CallBack principle in multi-server mode](https://github.com/pleleux-enseeiht/SC-Linda/blob/master/Graphics/Callback%20Serveur%20Linda.png "CallBack principle in multi-server mode")
