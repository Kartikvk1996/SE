package wserver;

import dmgr.SearchResult;
import java.util.ArrayList;
import jsonparser.JsonExposed;
import se.util.Address;

public class SearchResponse {
    
    @JsonExposed(comment = "Proxy server")
    public Address prxyAddr;
    
    @JsonExposed(comment = "Time taken to complete the search in milliseconds")
    public long millis;
    
    @JsonExposed(comment = "results from the dmgrs")
    public ArrayList<SearchResult> rset;

    public SearchResponse(long millis, ArrayList<SearchResult> rset, Address prxyAddr) {
        this.millis = millis;
        this.rset = rset;
        this.prxyAddr = prxyAddr;
    }
}
