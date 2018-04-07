/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.ipc.pdu;

import jsonparser.DictObject;
import jsonparser.JsonExposed;
import jsonparser.JsonObject;

/**
 *
 * @author mpataki
 */
public class CommandPDU extends PDU {
    
    @JsonExposed(comment = "This is the menthod to be executed. The data to this will be `data` field of PDU")
    public String function;
    
    public CommandPDU(DictObject jObject) throws InvalidPDUException {
        super(jObject);
    }

    public CommandPDU(String function, JsonObject data) throws InvalidPDUException {
        super(PDUConsts.METHOD_COMMAND);
        this.function = function;
        this.data = data;
    }
}
