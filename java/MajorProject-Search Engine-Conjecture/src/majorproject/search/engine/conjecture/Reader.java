/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package majorproject.search.engine.conjecture;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 *
 * @author akjantal
 */
public class Reader {
    
    public String dumpDirectory="";
   
    public static void main(String args[]) throws IOException{
        //Document d1=new Document("test.html");
        ServerSocket server =new ServerSocket(7878);
        System.out.println("Starting server1");
        Socket in;
        Socket sock=new Socket("192.168.43.236",8082);
        String payload="{\"ipaddress\":\"192.168.43.156\",\n\"mode\":1000,\n\"port\":7878,\n\"type\":21,\n\"urlids\":[ 1,2,3]}";
        System.out.println(payload);
        
        
        OutputStream os=sock.getOutputStream();
        os.write(payload.getBytes());
        in=server.accept();
        String line;
        //BufferedInputStream br =new BufferedInputStream(in.getInputStream());
        DataInputStream is=new DataInputStream(in.getInputStream());
        do{
            line=is.readLine();
        System.out.println("Received"+line);
            
        }while(line!=null);
        
    Document d2=new Document("Scandinavia.html");}
    
}


