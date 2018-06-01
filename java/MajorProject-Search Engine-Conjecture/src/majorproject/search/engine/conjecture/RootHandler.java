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
import java.net.URI;

public class RootHandler implements HttpHandler {

         public void handle(HttpExchange he) throws IOException {
             String response="<!DOCTYPE html>\n" +
"<!--\n" +
"To change this license header, choose License Headers in Project Properties.\n" +
"To change this template file, choose Tools | Templates\n" +
"and open the template in the editor.\n" +
"-->\n" +
"<html>\n" +
"<head>\n" +
"\n" +
"<style>\n" +
"body{\n" +
"	background-color:#040826;\n" +
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
"	margin-top:20%;\n" +
"        border-bottom-color: orange;\n" +
"        border-radius: 3px;\n" +
"}\n" +
"\n" +
"#click{\n" +
"	\n" +
"	width:70px;\n" +
"	height:30px;\n" +
"        margin-left: 30px;\n" +
"        border-radius: 3px;\n" +
"        border-width:3px;\n" +
"	border-bottom-color: orange;\n" +
"	\n" +
"}\n" +
"</style>\n" +
"<title>Hudak-Search</title>"
                     + "<link rel=\"shortcut icon\" href=\"/favicon.ico\"></head>\n" +
"<body>\n" +
"\n" +
"<form action=\"/echoGet?q=like this\" method=\"GET\">\n" +
"<input type=\"text\" id=\"input\">\n" +
"\n" +
"\n" +
"<input type=\"submit\" id=\"click\" value=\"Go!\">\n" +
"\n" +
"</form>\n" +
"</body>\n" +
"</html>";
                 FileInputStream file=new FileInputStream("index.html");
                 
                  URI uri=he.getRequestURI();
                 if( uri.getPath().contains("favicon.ico")){
         
         }else{
                  
                  
             
                 Headers h = he.getResponseHeaders();
                 
               
                
                 System.out.println("URI"+uri);
                 h.set("Content-Type","text");
                 he.sendResponseHeaders(200, response.length());
                 OutputStream os = he.getResponseBody();
                 os.write(response.getBytes());
                 os.close();
         }}

   
}