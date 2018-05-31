package fireup;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import jsonparser.DictObject;
import se.dscore.SlaveProcess;
import se.ipc.ESocket;
import se.ipc.pdu.AckPDU;
import se.ipc.pdu.CreatePDU;
import se.ipc.pdu.ErrorPDU;
import se.ipc.pdu.InvalidPDUException;
import se.ipc.pdu.KillPDU;
import se.ipc.pdu.PDU;
import se.ipc.pdu.PDUConsts;
import se.util.Logger;

public class NodeManager extends SlaveProcess implements Reportable {

    private Integer lastPID = 1;
    HashMap<String, WrappedProcess> processes;

    public NodeManager(NodemanagerConfig config) throws IOException {
        super(config);
        processes = new HashMap<>();
    }

    @Override
    public void handler(ESocket sock, PDU pdu) throws IOException {
        PDU resp = null;
        switch (pdu.getMethod()) {
            case PDUConsts.METHOD_CREATE:
                resp = createProcess((CreatePDU) pdu);
                break;
            case PDUConsts.METHOD_KILL: {
                try {
                    resp = killProcess((KillPDU) pdu);
                } catch (InvalidPDUException ex) {
                    Logger.elog(Logger.HIGH, " process kill failed");
                }
            }
            default:
                super.handler(sock, pdu);
        }
        sock.send(resp);
    }

    private PDU createProcess(CreatePDU cpdu) {

        PDU resp;
        String newPID = generatePID();
        String executableName = cpdu.getExecutable();

        /* Check whether you have latest version of JARs */
        File ef = new File(executableName);
        if (!ef.exists() || ef.lastModified() < mproxy.getJarVersion()) {
            downloadFile(executableName);
            downloadFile(executableName + ".conf");
        }

        WrappedProcess proc = WrappedProcess.createProcess(
                ticket,
                newPID,
                executableName,
                mproxy,
                (NodemanagerConfig) config,
                cpdu.getArguments()
        );

        if (proc != null) {
            processes.put(newPID, proc);
            processAdded(newPID);

            resp = new AckPDU(ticket, getHttpPort());
            DictObject dict = new DictObject();
            dict.set(PDUConsts.PID, newPID);
            resp.setData(dict);
        } else {
            resp = new ErrorPDU("process creation failed");
        }
        return resp;
    }

    public HashMap getProcesses() {
        return processes;
    }

    private synchronized String generatePID() {
        Calendar cal = GregorianCalendar.getInstance();
        lastPID++;
        return "proc_"
                + cal.get(Calendar.DAY_OF_MONTH)
                + cal.get(Calendar.MONTH)
                + cal.get(Calendar.YEAR)
                + cal.get(Calendar.HOUR_OF_DAY)
                + cal.get(Calendar.MINUTE)
                + cal.get(Calendar.SECOND)
                + "_"
                + lastPID;
    }

    private PDU killProcess(KillPDU pdu) throws InvalidPDUException {
        String pid = pdu.getPid();
        Logger.ilog(Logger.HIGH, "Killing the process " + pid);
        processes.get(pid).kill();
        DictObject dobj = new DictObject();
        dobj.set("status", "success");
        return new AckPDU(dobj);
    }

    private void downloadFile(String fileName) {
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
                            ((NodemanagerConfig) config).getNMHome(),
                            fileName
                    ),
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException ex) {
            Logger.elog(Logger.HIGH, "Couldn't download " + fileName + " " + ex.getMessage());
        }
    }

    private static boolean guiEnabled;
    private static MainPage mainPage;

    private static void setLookAndFeel() {
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) throws IOException {

        if (args.length < 3) {
            Logger.elog(Logger.HIGH, "Usage: nodemanager <master-host> <master-port> <configFile-path>");
            Logger.elog(Logger.HIGH, "A sample has been generated at ./sample.conf");
            new NodemanagerConfig().generateSample("sample.conf");
            System.exit(0);
        }

        for (String arg : args) {
            if (arg.equalsIgnoreCase("-enableGUI")) {
                guiEnabled = true;
            }
        }

        NodemanagerConfig conf;

        conf = (NodemanagerConfig) ((args.length > 2)
                ? new NodemanagerConfig(args[2])
                : new NodemanagerConfig());

        conf.setMasterHost(args[0]);
        conf.setMasterPort(Integer.parseInt(args[1]));
        conf.setProcessRole(PDUConsts.PN_FIREUP);

        NodeManager fireupMain = new NodeManager(conf);
        
        if (guiEnabled) {
            setLookAndFeel();
            MainPage.fireupModel = fireupMain;
            mainPage = new MainPage();
            java.awt.EventQueue.invokeLater(() -> {
                mainPage.setVisible(true);
            });
        }
        
        fireupMain.run();
    }

    @Override
    public void processAdded(String pname) {
        if (guiEnabled) {
            mainPage.processAdded(pname);
        }
        Logger.ilog(Logger.HIGH, pname + " process created.");
    }

    @Override
    public void setStatus(String status) {
        if (guiEnabled) {
            mainPage.setStatus(status);
        }
        Logger.ilog(Logger.HIGH, status);
    }

}
