package fireup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Random;
import jsonparser.JsonException;
import se.ipc.EServerSocket;
import se.ipc.ESocket;
import se.ipc.pdu.AckPDU;
import se.ipc.pdu.ConnectPDU;
import se.ipc.pdu.CreatePDU;
import se.ipc.pdu.ErrorPDU;
import se.ipc.pdu.InvalidPDUException;
import se.ipc.pdu.PDU;
import se.ipc.pdu.PDUConsts;
import se.util.Logger;

/**
 *
 * @author Madhusoodan Pataki
 */
public class Fireup implements Runnable {

    private static int fireupPort = 5678;
    ArrayList<WrappedProcess> processes;
    private final InetAddress hostAddress;
    private final EServerSocket servSock;
    private MainPage mainPage;
    private final String secret;
    Properties props;

    String masterAddr;
    int masterPort, masterHTTPPort;

    //This is where all the binaries must be located
    private static String SEHOME;
    private static final String PROPSFILE = "./fireupconfig.props";
    private static final String VSEHOME = "SEHOME";

    private long serverJarVersion = 0;

    Fireup() throws UnknownHostException, IOException {

        //Initialise properties 
        try {
            props = new Properties();
            props.load(new FileInputStream(PROPSFILE));
            SEHOME = props.getProperty(VSEHOME);
            if (SEHOME == null) {
                createPropsFile();
            }
        } catch (IOException ex) {
            createPropsFile();
        }

        processes = new ArrayList<>();
        hostAddress = InetAddress.getLocalHost();

        servSock = new EServerSocket(0);
        fireupPort = servSock.getPort();

        Random r = new Random();
        secret = Math.abs((((long) r.nextInt()) << 32) | r.nextInt()) + "";

        PDU.setProcessRole(PDUConsts.PN_FIREUP);
        se.util.Logger.setLoglevel(se.util.Logger.DEBUG);

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
                ESocket conn = servSock.accept();
                CreatePDU pdu = (CreatePDU) conn.recvPDU();
                PDU resp;

                if (false && this.secret.equals(pdu.getSecret())) {

                } else {
                    switch (pdu.getMethod()) {
                        case PDUConsts.METHOD_CREATE:
                            if(createProcess(getCommandLine(pdu))) {
                                resp = new AckPDU();
                                Logger.ilog(Logger.MEDIUM, "Process creation " + pdu.getExecutable() + Arrays.toString(pdu.getArguments()) + " was successful");
                            } else {
                                resp = new ErrorPDU("process creation failed");;
                                Logger.ilog(Logger.MEDIUM, "Process creation " + pdu.getExecutable() + Arrays.toString(pdu.getArguments()) + " failed");
                            }
                            conn.send(resp);
                            break;
                        default:
                    }
                }
            } catch (IOException | JsonException | InvalidPDUException ex) {
                Logger.elog(Logger.MEDIUM, "Error whilecommunicating to master " + ex.getMessage());
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
            ESocket sock = new ESocket(host, port);
            sock.send(new ConnectPDU(fireupPort));

            AckPDU ack = (AckPDU) sock.recvPDU();
            masterAddr = host;
            masterPort = port;
            serverJarVersion = Long.parseLong(ack.getJarVersion());
            masterHTTPPort = ack.getHttpPort();
        } catch (IOException | JsonException | InvalidPDUException ex) {
            Logger.elog(Logger.HIGH, "Error connecting to master " + ex.getMessage());
        }
    }

    private void createPropsFile() {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(PROPSFILE);
            pw.append(VSEHOME + "=.");
            pw.flush();
        } catch (FileNotFoundException ex) {
            Logger.elog(Logger.HIGH, "Couldn't create properties file");
        } finally {
            pw.close();
        }
    }

    private String[] getCommandLine(CreatePDU pdu) {
        String args[] = pdu.getArguments();
        String cmd = pdu.getExecutable();
        String cmdchunks[];

        if (cmd.contains(".jar")) {
            File execFile = new File(cmd);
            if (!execFile.exists() || execFile.lastModified() < serverJarVersion) {
                requestExecutable(cmd);
                mainPage.setStatus("Downloading " + cmd);
            }

            /* check whether the JAVA_HOME is defined */
            String jhome = System.getenv("JAVA_HOME");

            if(jhome == null) {
                Logger.elog(Logger.HIGH, "JAVA_HOME environment variable not set");
            }
            
            cmdchunks = new String[3 + args.length];
            cmdchunks[0] = "\"" + Paths.get(jhome, "bin",  "java.exe").toString() + "\"";
            cmdchunks[1] = "-jar";
            cmdchunks[2] = cmd;
            System.arraycopy(args, 0, cmdchunks, 3, args.length);
            return cmdchunks;
        } else {
            cmd = SEHOME + cmd;
            cmdchunks = new String[1 + args.length];
        }

        cmdchunks[0] = cmd;
        for (int i = 0; i < args.length; i++) {
            cmdchunks[i + 1] = args[i];
        }
        return cmdchunks;
    }

    private void downloadExecutables() {
        /* check the jar version (modified date) */
        File folder = new File(SEHOME);

        if (!folder.exists()) {
            folder.mkdir();
            return;
        }

        File[] jars = folder.listFiles();

        for (File jar : jars) {
            if (!jar.getName().contains(".jar")) {
                continue;
            }
            if (serverJarVersion > jar.lastModified()) {
                requestExecutable(jar.getName());
            }
        }
    }

    private void requestExecutable(String cmd) {
        try {
            Logger.ilog(Logger.MEDIUM, "Downloading " + cmd);
            URL website = new URL("http", masterAddr, masterHTTPPort, "/" + cmd);
            try (InputStream in = website.openStream()) {
                Files.copy(in, Paths.get(SEHOME, cmd), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                Logger.elog(Logger.HIGH, "Couldn't download " + cmd + " " + ex.getMessage());
            }
        } catch (MalformedURLException ex) {

        }
    }

}
