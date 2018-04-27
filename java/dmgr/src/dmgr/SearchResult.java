package dmgr;

import java.util.HashSet;
import jsonparser.JsonExposed;

public class SearchResult {
    
    @JsonExposed(comment = "milli-seconds consumed for getting results")
    public long millis;
    
    @JsonExposed(comment = "results")
    public HashSet<Index> set;

    public SearchResult(long millis, HashSet<Index> set) {
        this.millis = millis;
        this.set = set;
    }
    
}
