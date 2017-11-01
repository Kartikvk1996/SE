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

    Fireup() throws UnknownHostException, IOException {
        processes = new ArrayList<>();
        hostAddress = InetAddress.getLocalHost();

        servSock = new ServerSocket(0);
        fireupPort = servSock.getLocalPort();

        Random r = new Random();
        secret = "" + Math.abs((((long) r.nextInt()) << 32) | r.nextInt()) + "\n";
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
            try {
                Socket conn = servSock.accept();
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                OutputStream os = conn.getOutputStream();

                String tsecret, data;
                
                tsecret = br.readLine();
                if (!this.secret.equals(tsecret + "\n")) {
                    os.write("Auth error\n".getBytes());
                } else {
                    data = br.readLine();

                    /* TODO: process the string someway */
                    String tokens[] = data.split(" ");
                    String cmds[] = new String[tokens.length - 1];
                    for (int i = 0; i < cmds.length; i++) {
                        cmds[i] = tokens[i + 1];
                    }
                    
                    os.write(secret.getBytes());
                    switch (tokens[0].toUpperCase()) {
                        case "CREATE":
                            os.write((createProcess(cmds) + "\n").getBytes());
                            break;
                        default:
                    }
                }
                os.flush();
                conn.close();
            } catch (IOException ex) {
                System.err.println(ex);
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
            String buf = secret + "host=" + getInetAddress() + " port=" + getrunningPort() + "\n";
            sock.getOutputStream().write(buf.getBytes());
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
