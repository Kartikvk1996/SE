## Protocol adapted between DMGR and FFC.

Since JSON parsing can be a very performance overhead we want to eliminate this by defining a simple protocol between a (FFCs & RGENs) and DMGRs. The communication between FFCs and DMGRs is very frequent and doesn't require the identification of the sender or reciever. We exploit this to build a simple protocol which can save a lot of parsing overhead. 


##### Structure of the PDU.

The structure of the PDU is as follows.

```c
struct {
    unsigned int sender;    //who is the sender 0: RGEN, 1: FFC
    docid_t docid;          //document id.
    rank_t rank;            //static rank assigned by the web-graph
    char data[0];           //struct hack.
};
````
