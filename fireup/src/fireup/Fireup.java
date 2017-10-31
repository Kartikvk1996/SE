package fireup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

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

    Fireup() throws UnknownHostException, IOException {
        processes = new ArrayList<>();
        hostAddress = InetAddress.getLocalHost();

        servSock = new ServerSocket(0);
        fireupPort = servSock.getLocalPort();
    }
    
    public void setOberserver(MainPage mainPage) {
        this.mainPage = mainPage;
    }

    public boolean createProcess(String ...command) {
        WrappedProcess proc = WrappedProcess.createProcess(command);
        if(proc != null) {
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
                String data = br.readLine();

                System.out.println(data);
                
                /* TODO: process the string someway */
                String tokens[] = data.split(" ");
                String cmds[] = new String[tokens.length - 1];
                for (int i = 0; i < cmds.length; i++) {
                    cmds[i] = tokens[i+1];
                }
                switch (tokens[0].toUpperCase()) {
                    case "CREATE":
                        os.write((createProcess(cmds) + "\n").getBytes());
                        break;
                    default:
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
}
