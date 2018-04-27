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

    public OutputStream getOutputStream() throws IOException {
        return out;
    }

    /* dirtiest code */
    private int toHex(char c) {

        c = Character.toLowerCase(c);

        if (c >= 'a' && c <= 'z') {
            c = (char) (c - 'a' + 10);
        } else {
            c = (char) (c - '0');
        }
        return c;
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
            if (url.contains("http")) {
                throw new HttpException("HTTP proxies not supported");
            }
            if (url.charAt(0) == '/') {
                url = url.substring(1);
            }

            StringBuilder urlDec = new StringBuilder();
            for (int i = 0; i < url.length(); i++) {
                char ch = url.charAt(i);
                if (ch == '%' && (i + 2) < url.length()) {

                    int n1 = toHex(url.charAt(i + 1));
                    int n2 = toHex(url.charAt(i + 2));
                    
                    ch = (char) ((n1 << 4) | (n2));
                    i += 2;
                }
                urlDec.append(ch);
            }
            url = urlDec.toString();

            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (line.equals("")) {
                    break;
                }
                String kvps[] = line.split(":", 2);
                headers.put(kvps[0], kvps[1]);
            }

            String urlNData[] = url.split("[?]");
            url = urlNData[0];
            if (urlNData.length > 1) {
                data = urlNData[1];
            } else {

                /* read data here if there is something */
                while (line != null) {
                    line = reader.ready() ? reader.readLine() : null;
                    sb.append(line).append("\n");
                }
                data = sb.toString();
            }
        } catch (IOException ex) {
            throw new HttpException("Request parsing failed [StreamError]");
        } catch (ArrayIndexOutOfBoundsException aex) {
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
