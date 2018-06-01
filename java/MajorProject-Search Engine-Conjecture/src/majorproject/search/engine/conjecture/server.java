/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package majorproject.search.engine.conjecture;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 *
 * @author akjantal
 */
public class server {
    
    public static void main(String[] args) throws IOException {
        int port = 9000;
HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
System.out.println("server started at " + port);
server.createContext("/", (HttpHandler) new RootHandler());
//server.createContext("/echoHeader", new EchoHeaderHandler());
server.createContext("/echoGet", new ProcessGet());
server.createContext("/echoPost", new processPost());
server.setExecutor(null);
server.start();
    }
    
}
