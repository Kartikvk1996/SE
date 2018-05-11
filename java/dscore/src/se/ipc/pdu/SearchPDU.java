package se.ipc.pdu;

import jsonparser.DictObject;
import jsonparser.JsonExposed;

public class SearchPDU extends PDU {
    
    @JsonExposed(comment = "the query")
    public String query;
    
    public SearchPDU(DictObject jObject) throws InvalidPDUException {
        super(jObject);
    }
    
    public SearchPDU(String query) {
        super(PDUConsts.METHOD_SEARCH);
        this.query = query;
    }
    
    public String getQuery() {
        return query;
    }
}
