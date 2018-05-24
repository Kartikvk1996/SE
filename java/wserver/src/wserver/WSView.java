package wserver;

import java.util.HashMap;
import jsonparser.JsonExposed;
import se.util.Address;

public class WSView {
    
    @JsonExposed(comment = "Number of queries hit")
    public long queries;
    
    @JsonExposed(comment = "Number of successful queries")
    public long squeries;
    
    @JsonExposed(comment = "Dmgrs")
    public HashMap<String, Address> dmgrs;

    public WSView(HashMap<String, Address> dmgrs) {
        this.dmgrs = dmgrs;
    }
}
