package se.util.http;

import java.io.IOException;

public interface RestServlet {
    public void serve(HttpRequest req) throws IOException;
}
