package fireup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Properties;
import jsonparser.DictObject;
import jsonparser.JsonException;
import se.dscore.MasterProxy;
import se.dscore.RequestHandler;
import se.dscore.Server;
import se.ipc.ESocket;
import se.ipc.pdu.*;
import se.util.Logger;
import se.util.http.HttpServer;

public class Fireup implements RequestHandler {

    private int lastPID = 1;
    HashMap<String, WrappedProcess> processes;
    Server fireupServer;
    HttpServer httpServer;
    private MainPage mainPage;
    Properties properties;
    String ticket;
    MasterProxy mproxy;

    private static final String PROPS_FILE = "fireupconfig.props";
    private static final String PROP_SEHOME = "SEHOME";
    private static final String PROP_LOGSFOLDER = "LOGS_DIR";

    public Fireup(String args[]) throws IOException {

        try {
            properties = new Properties();
            this.properties.load(new FileInputStream(PROPS_FILE));
            if (properties.getProperty(PROP_SEHOME) == null
                    || properties.getProperty(PROP_LOGSFOLDER) == null) {
                createPropertiesFile();
            }
        } catch (IOException ex) {
            createPropertiesFile();
        }

        processes = new HashMap<>();
        fireupServer = new Server(this);
        httpServer = new HttpServer(properties.getProperty(PROP_LOGSFOLDER));

        /* Setup PDU constants */
        PDU.setProcessRole(PDUConsts.PN_FIREUP);

        /* Setlog level */
        Logger.setLoglevel(Logger.DEBUG);

        if (args.length > 1) {
            connectToMaster(args[0], Integer.parseInt(args[1]));
        }

    }

    void setObserver(MainPage mainPage) {
        this.mainPage = mainPage;
    }

    void run() {

        /* Start the Fireup server */
        new Thread(() -> {
            try {
                fireupServer.run();
            } catch (IOException ex) {
                Logger.elog(Logger.HIGH, "Error starting the fireup server. " + ex.getMessage());
            }
        }).start();

        /* Start the Http Log Server */
        new Thread(() -> {
            try {
                httpServer.run();
            } catch (IOException ex) {
                Logger.elog(Logger.HIGH, "Error starting the log server. " + ex.getMessage());
            }
        }).start();
    }

    private PDU createProcess(CreatePDU cpdu) {

        PDU resp;
        String newPID = generatePID();
        String executableName = cpdu.getExecutable();
        
        /* Check whether you have latest version of JARs */
        File ef = new File(executableName);
        if (!ef.exists() || ef.lastModified() < mproxy.getJarVersion()) {
            downloadExecutable(executableName);
            mainPage.setStatus("Downloading " + executableName);
        }

        WrappedProcess proc = WrappedProcess.createProcess(ticket, newPID, executableName, mproxy, cpdu.getArguments());

        if (proc != null) {
            processes.put(newPID, proc);
            mainPage.processAdded(newPID);

            resp = new AckPDU(ticket);
            DictObject dict = new DictObject();
            dict.set(PDUConsts.PID, newPID);
            resp.setData(dict);
        } else {
            resp = new ErrorPDU("process creation failed");
        }
        return resp;
    }

    @Override
    public void handle(ESocket sock) throws IOException {
        PDU pdu;
        PDU resp = null;
        try {
            pdu = sock.recvPDU();
            switch (pdu.getMethod()) {
                case PDUConsts.METHOD_CREATE:
                    resp = createProcess((CreatePDU) pdu);
                    break;
                case PDUConsts.METHOD_KILL:
                    resp = killProcess((KillPDU) pdu);
            }
            sock.send(resp);
        } catch (JsonException | InvalidPDUException ex) {
            Logger.elog(Logger.HIGH, "Error while accepting a PDU. " + ex.getMessage());
        }
    }

    public HashMap getProcesses() {
        return processes;
    }

    public String getHost() {
        return fireupServer.getHost();
    }

    public int getHttpPort() {
        return httpServer.getPort();
    }
    
    public int getFireupPort() {
        return fireupServer.getPort();
    }

    private void createPropertiesFile() {
        try {
            properties.put(PROP_SEHOME, ".");
            properties.put(PROP_LOGSFOLDER, ".");
            properties.store(new PrintWriter(PROPS_FILE), "");
        } catch (IOException ex) {
            Logger.elog(Logger.MEDIUM, "Error saving properties file");
        }
    }

    void connectToMaster(String host, int port) {
        try {
            mproxy = new MasterProxy(host, port);
            ConnectPDU cpdu = new ConnectPDU(
                    "", "***",
                    getFireupPort()
            );
            cpdu.setHttpPort(httpServer.getPort());
            
            AckPDU pdu = (AckPDU) mproxy.send(cpdu, true);
            mproxy.setJarVersion(Long.parseLong(pdu.getJarVersion()));
            mproxy.setHttpPort(pdu.getHttpPort());
            ticket = pdu.getTicket();
        } catch (IOException | JsonException | InvalidPDUException ex) {
            Logger.elog(Logger.HIGH, "Error connecting to master " + ex.getMessage());
        }
    }

    private String generatePID() {
        return "PID-" + (lastPID++);
    }

    private PDU killProcess(KillPDU pdu) throws InvalidPDUException {
        String pid = pdu.getPid();
        Logger.ilog(Logger.HIGH, "Killing the process " + pid);
        processes.get(pid).kill();
        DictObject dobj = new DictObject();
        dobj.set("status", "success");
        return new AckPDU(dobj);
    }

    private void downloadExecutable(String fileName) {
        try {
            Logger.ilog(Logger.MEDIUM, "Downloading " + fileName);
            URL website = new URL(
                    "http",
                    mproxy.getHost(),
                    mproxy.getHttpPort(),
                    "/" + fileName
            );
            InputStream in = website.openStream();
            Files.copy(
                    in,
                    Paths.get(
                            properties.getProperty(PROP_SEHOME),
                            fileName
                    ),
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException ex) {
            Logger.elog(Logger.HIGH, "Couldn't download " + fileName + " " + ex.getMessage());
        }
    }

    @Override
    public void handler(ESocket sock, PDU pdu) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
