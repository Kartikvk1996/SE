package fireup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Madhusoodan Pataki
 */
public class Fireup implements Runnable {

    private static int fireupPort = 5678;
    ArrayList<WrappedProcess> processes;
    private InetAddress hostAddress;
    private ServerSocket servSock;
    private MainPage mainPage;
    private String secret;

    private static final String METHOD = "METHOD";
    private static final String CREATE = "CREATE";
    private static final String SECRET = "SECRET";
    private static final String STATUS = "STATUS";
    private static final String ARGS = "ARGS";
    private static final String CMD = "CMD";

    Fireup() throws UnknownHostException, IOException {
        processes = new ArrayList<>();
        hostAddress = InetAddress.getLocalHost();

        servSock = new ServerSocket(0);
        fireupPort = servSock.getLocalPort();

        Random r = new Random();
        secret = Math.abs((((long) r.nextInt()) << 32) | r.nextInt()) + "";
    }

    public void setOberserver(MainPage mainPage) {
        this.mainPage = mainPage;
    }

    public boolean createProcess(String... command) {
        WrappedProcess proc = WrappedProcess.createProcess(command);
        if (proc != null) {
            processes.add(proc);
            mainPage.processAdded();
        }
        return (proc != null);
    }

    @Override
    public void run() {
        while (true) {
            try (Socket conn = servSock.accept()) {
                
                char buffer[] = new char[1024];
                
                InputStreamReader isr = new InputStreamReader(conn.getInputStream());
                isr.read(buffer);
                OutputStream os = conn.getOutputStream();
                String data = new String(buffer), line;
                
                JsonObject jreq = new JsonObject(data);
                JsonObject jres = new JsonObject();
                if (!this.secret.equals(jreq.get(SECRET))) {
                    jres.set("status", "Auth Error");
                } else {
                    jres.set(SECRET, this.secret);
                    switch (jreq.get(METHOD).toUpperCase()) {
                        case CREATE:
                            jres.set(STATUS, createProcess(jreq.get(CMD), jreq.get(ARGS)));
                            break;
                        default:
                    }
                }
                os.write(jres.toString().getBytes());
                os.flush();
            } catch (IOException ex) {
                Logger.getLogger(Fireup.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String getInetAddress() {
        return hostAddress.getHostAddress();
    }

    public int getrunningPort() {
        return fireupPort;
    }

    void connectToMaster(String host, int port) {
        try {
            Socket sock = new Socket(host, port);
            JsonObject jreq = new JsonObject();
            jreq.set("SECRET", secret);
            jreq.set("HOST", getInetAddress());
            jreq.set("PORT", getrunningPort());
            sock.getOutputStream().write(jreq.toString().getBytes());
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
