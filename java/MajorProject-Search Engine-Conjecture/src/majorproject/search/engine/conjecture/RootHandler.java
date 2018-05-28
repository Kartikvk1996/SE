/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package majorproject.search.engine.conjecture;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

public class RootHandler implements HttpHandler {

         public void handle(HttpExchange he) throws IOException {
             String response="<html>\n" +
"<head>\n" +
"\n" +
"<style>\n" +
"body{\n" +

"	background-size: cover;\n" +
"	width: 100%;\n" +
"	height: 100vh;\n" +
"	background-position: center center;\n" +
"	position: relative;\n" +
"	\n" +
"}\n" +
"\n" +
"#input{\n" +
"	\n" +
"	width:200px;\n" +
"	height:30px;\n" +
"	margin-left:41%;\n" +
"	margin-top:25%;\n" +
"}\n" +
"\n" +
"#click{\n" +
"	\n" +
"	width:70px;\n" +
"	height:30px;\n" +
"	background-image:url(\"button.png\");\n" +
"	\n" +
"}\n" +
"</style>\n" +
"<title>Hudak-Search</title></head>\n" +
"<body>\n" +
"\n" +
"<form action=\"/echoGet\" method=\"GET\">\n" +
"<input type=\"text\" id=\"input\">\n" +
"\n" +
"</input>\n" +
"<input type=\"submit\" id=\"click\">\n" +
"</input>\n" +
"\n" +
"</form>\n" +
"</body>\n" +
"</html>";
                 FileInputStream file=new FileInputStream("index.html");
                 Headers h = he.getResponseHeaders();
                 h.set("Content-Type","text/html");
                 he.sendResponseHeaders(200, response.length());
                 OutputStream os = he.getResponseBody();
                 os.write(response.getBytes());
                 os.close();
         }

   
}