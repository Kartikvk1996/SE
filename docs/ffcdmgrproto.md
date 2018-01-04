## Protocol adapted between DMGR and FFC.

Since JSON parsing can be a very performance overhead we want to eliminate this by defining a simple protocol between a (FFCs & RGENs) and DMGRs. The communication between FFCs and DMGRs is very frequent and doesn't require the identification of the sender or reciever. We exploit this to build a simple protocol which can save a lot of parsing overhead. 

##### `newline` delimited PDU.

The communication with DMGR usually requires fixed number of arguments (except in case of RGEN and DMGR). So we specify different fields in different lines.

#### fields to be specified.
| Line | Field  | Comment                                                                 |
|------|--------|-------------------------------------------------------------------------|
| #1   | Sender | The class of sender. (RGEN \| DMGR) Used to interpret the further data. |
| #2   | DocId* | Document Id assigned by crawler unit to the document.                   |
| #3   | Rank*  | Rank assigned by crawler unit.                                          |
| ...  | Data*  | Extra data                                                              |

