### Protocol for master slave communication

The communication between master-slave, master-master is done using the following protocol whose feilds are described below.

#### Note that every PDU is in the JSON format to ease the parsing process.


|  Parameter   |  Description   |  Possible Values |
|--------------|----------------|------------------|
|  method      | what do you want  |  GET, CONNECT, CREATE, UPDATE, KILL, ACK |
|  receiver_ip | the ip of host this packet is being sent to | valid ip address |
|receiver_port | the port on which the peer is listening | valid port number (not a string) |
|  sender_ip   | the ip of the sender host | valid ip address |
| sender_port | the port from where you are sending this PDU | any valid port number |
| who | the class name of sender | CRAWLER, MASTER, FFC, DMGR, WS |
| whom | the class name of receiver | CRAWLER, MASTER, FFC, DMGR, WS |
| data | data needed for processing the request | any valid json string |
||

````
// a sample PDU.
{
    "method": "GET",
    "receiver_ip": "123.231.212.100",
    "receiver_port": 3475,
    "sender_ip": "122.123.124.135",
    "sender_port": 2343,

    "who": "crawler",
    "whom": "master",

    "data": "a_json_string"
}
````