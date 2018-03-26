package se.util.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import se.ipc.ESocket;
import se.util.Logger;

public class HttpRequest {

    HashMap<String, String> headers;
    private String data;
    private String url;
    private String method;
    private OutputStream out;
    
    OutputStream getOutputStream() throws IOException {
        return out;
    }
    
    HttpRequest(ESocket socket) throws HttpException, IOException {

        headers = new HashMap<>();
        out = socket.getOutputStream();
        
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            String firstLine = reader.readLine();
            String chunks[] = firstLine.split(" ", 2);

            Logger.ilog(Logger.DEBUG, firstLine);
            
            method = chunks[0];
            url = chunks[1].substring(0, chunks[1].lastIndexOf(" "));
            
            /* process the url correctly */
            if(url.contains("http"))
                url = url.split("//")[1];
            if(url.contains(":"))
                url = url.split(":", 1)[1];
            if(url.charAt(0) == '/')
                url = url.substring(1);
            
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (line.equals(""))
                    break;
                String kvps[] = line.split(":", 2);
                headers.put(kvps[0], kvps[1]);
            }

            /* read data here if there is something */
            while (line != null) {
                line = reader.ready() ? reader.readLine() : null;
                sb.append(line).append("\n");
            }
            data = sb.toString();
        } catch (IOException ex) {
            throw new HttpException("Request parsing failed [StreamError]");
        } catch(ArrayIndexOutOfBoundsException aex) {
            throw new HttpException("Request parsing failed [Malformed Request]");
        }
    }

    public String getUrl() {
        return url;
    }
    
    public String getHeader(String name) {
        return headers.get(name);
    }
    
    public String getData() {
        return data;
    }
    
    
}
