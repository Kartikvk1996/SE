package dmgr;

import java.io.Serializable;
import jsonparser.JsonExposed;

public class Index implements Serializable {
    
    @JsonExposed(comment = "frequency of the word in this doc")
    public int frequency;

    public Index(int frequency) {
        this.frequency = frequency;
    }

    /* for serialization.
     * read here : 
     * http://www.xyzws.com/Javafaq/what-are-rules-of-serialization-in-java/208
     */
    public Index() { }
    
}
