package se.util;

import jsonparser.JsonExposed;

public class Address {
    
    @JsonExposed(comment = "host")
    public String host;
    
    @JsonExposed(comment = "port")
    public int port;

    public Address(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    @Override
    public String toString() {
        return "{host: " + host +", port: " + port +  "}";
    }
}
