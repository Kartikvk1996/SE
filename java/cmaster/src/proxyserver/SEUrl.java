package proxyserver;

import jsonparser.JsonExposed;

public class SEUrl {

    @JsonExposed(comment = "url of this proxy")
    public String url;
    
    @JsonExposed(comment = "number of hits on this url")
    public long hits;

    SEUrl(String url, long hits) {
        this.url = url;
        this.hits = hits;
    }
}
