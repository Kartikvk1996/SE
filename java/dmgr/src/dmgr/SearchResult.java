package dmgr;

import java.io.Serializable;
import java.util.HashMap;
import jsonparser.JsonExposed;

public class SearchResult implements Serializable {
    
    @JsonExposed(comment = "index")
    public HashMap<Long, Index> rset;

    public SearchResult() {
    }
    
    public SearchResult(HashMap<Long, Index> rset) {
        this.rset = rset;
    }
}
