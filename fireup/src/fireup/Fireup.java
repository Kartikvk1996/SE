package fireup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
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
    Properties props;

    //This is where all the binaries must be located
    private static String SEHOME;

    private static final String PROPSFILE = "./fireupconfig.props";
    private static final String METHOD = "METHOD";
    private static final String CREATE = "CREATE";
    private static final String SECRET = "SECRET";
    private static final String STATUS = "STATUS";
    private static final String ARGS = "DATA.ARGS";
    private static final String CMD = "DATA.CMD";
    private static final String VSEHOME = "SEHOME";

    Fireup() throws UnknownHostException, IOException {

        //Initialise properties 
        try {
            props = new Properties();
            props.load(new FileInputStream(PROPSFILE));
            SEHOME = props.getProperty(VSEHOME);
            if (SEHOME == null) {
                createPropsFile();
            }
        } catch (Exception ex) {
            createPropsFile();
        }

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
                String cmdline[];
                OutputStream os = conn.getOutputStream();
                InputStreamReader isr = new InputStreamReader(conn.getInputStream());
                int end = isr.read(buffer);
                String data = new String(Arrays.copyOf(buffer, end - 1));

                JsonWrapper jreq = new JsonWrapper(data);
                JsonWrapper jres = new JsonWrapper();
                if (false
                        && //<--------------
                        !this.secret.equals(jreq.get(SECRET))) {
                    jres.set("status", "Auth Error");
                } else {
                    jres.set(SECRET, this.secret);
                    switch (jreq.get(METHOD).toUpperCase()) {
                        case CREATE:
                            cmdline = getCommandLine(jreq);
                            System.err.println("Process " + Arrays.toString(cmdline) + " created.");
                            jres.set(STATUS, createProcess(cmdline));
                            break;
                        default:
                    }
                }
                jres.setFixedParams(getrunningPort());
                os.write(jres.toString().getBytes());
                os.write(-1);
                os.close();
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
            JsonWrapper jreq = new JsonWrapper();
            jreq.set("SECRET", secret);
            jreq.set(METHOD, "CONNECT");
            jreq.setFixedParams(getrunningPort());
            sock.getOutputStream().write(jreq.toString().getBytes());
            sock.getOutputStream().write(-1);
            sock.getOutputStream().flush();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    private void createPropsFile() {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(PROPSFILE);
            pw.append(VSEHOME + "=.");
            pw.flush();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Fireup.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            pw.close();
        }
    }

    private String[] getCommandLine(JsonWrapper jreq) {
        String args[] = jreq.get(ARGS).split("[ \t]");
        String executable = SEHOME + "/" + jreq.get(CMD);
        String cmdchunks[] = new String[1 + args.length];
        cmdchunks[0] = executable;
        for (int i = 0; i < args.length; i++) {
            cmdchunks[i + 1] = args[i];
        }
        return cmdchunks;
    }

}
