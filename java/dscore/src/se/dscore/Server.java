package se.dscore;

import se.util.Logger;
import se.ipc.EServerSocket;
import se.ipc.ESocket;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final int DEFAULT_MAX_THREADS = 10;
    private EServerSocket servSocket;
    private RequestHandler handler;
    private int max_threads = DEFAULT_MAX_THREADS;
    private ExecutorService executor;

    public Server(EServerSocket serverSock) {
        this.servSocket = serverSock;
    }

    private void init(RequestHandler handler, int port, int max_threads) throws IOException {
        this.servSocket = new EServerSocket(port);
        this.handler = handler;
        this.max_threads = max_threads;
        executor = Executors.newFixedThreadPool(max_threads);
    }

    public Server(int port, RequestHandler handler) throws IOException {
        init(handler, port, DEFAULT_MAX_THREADS);
    }

    public Server(int port, RequestHandler handler, int max_threads) throws IOException {
        init(handler, port, max_threads);
    }

    public Server(RequestHandler handler) throws IOException {
        init(handler, 0, DEFAULT_MAX_THREADS);
    }

    public Server(RequestHandler handler, int max_threads) throws IOException {
        init(handler, 0, max_threads);
    }

    public int getTpoolSize() {
        return max_threads;
    }

    public void run() throws IOException {
        while (true) {
            ESocket sock = servSocket.accept();
            executor.submit(() -> {
                try {
                    handler.handle(sock);
                    sock.close();
                } catch (IOException ex) {
                    Logger.elog(Logger.HIGH, ex.getMessage());
                }
            });
            Logger.ilog(Logger.DEBUG, "Accepted a connection");
        }
    }

    public String getHost() {
        return servSocket.getHost();
    }

    public int getPort() {
        return servSocket.getPort();
    }
}
