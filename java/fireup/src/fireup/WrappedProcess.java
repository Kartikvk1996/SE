package fireup;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import se.dscore.MasterProxy;
import se.util.Logger;

/**
 *
 * @author Madhusoodan Pataki
 */
public class WrappedProcess {

    String procName;
    String startTime;
    Process process;
    String errorFile, outputFile;
    String cmdline[];

    public static WrappedProcess createProcess(
            String ticket, String newPID,
            String executableName,
            MasterProxy mproxy,
            NodemanagerConfig nconf,
            String[] arguments) {

        Path err, out;
        String command[] = null;
        WrappedProcess wp = new WrappedProcess();
        ArrayList<String> cmdchunks = new ArrayList<>();

        if (executableName.contains(".jar")) {

            /* check whether the JAVA_HOME is defined */
            String jhome = System.getenv("JAVA_HOME");

            if (jhome == null) {
                Logger.elog(Logger.HIGH, "JAVA_HOME environment variable not set");
            }

            cmdchunks.add(Paths.get(jhome, "bin", "java.exe").toString());
            cmdchunks.add("-jar");
            cmdchunks.add(executableName);
            cmdchunks.add(ticket);
            cmdchunks.add(newPID);
            cmdchunks.add(mproxy.getHost());
            cmdchunks.add(mproxy.getPort() + "");
            cmdchunks.addAll(Arrays.asList(arguments));

            command = new String[7 + arguments.length];

        } else {
            cmdchunks.add(executableName);
            cmdchunks.addAll(Arrays.asList(arguments));
        }

        cmdchunks.toArray(command);
        wp.cmdline = command;
        try {
            /* check whether the log folder exists */
            File logd = new File(Paths.get(nconf.getLogsDir(), newPID).toAbsolutePath().toString());
            if (!logd.exists()) {
                Files.createDirectories(Paths.get(nconf.getLogsDir(), newPID));
            }
            out = Files.createFile(Paths.get(nconf.getLogsDir(), newPID, "out.txt"));
            err = Files.createFile(Paths.get(nconf.getLogsDir(), newPID, "err.txt"));

            wp.errorFile = err.toAbsolutePath().toString();
            wp.outputFile = out.toAbsolutePath().toString();

        } catch (IOException ex) {
            Logger.elog(Logger.HIGH, "Log file creation failed for process " + newPID, ex);
        }

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectError(new File(wp.errorFile));
        pb.redirectOutput(new File(wp.outputFile));

        wp.procName = command[0];
        wp.startTime = (new Date()).toString();

        try {
            wp.process = pb.start();
            Logger.ilog(Logger.MEDIUM, "Process creation " + Arrays.toString(command) + " was successful");
        } catch (IOException ex) {
            Logger.ilog(Logger.MEDIUM, "Process creation " + Arrays.toString(command) + " failed");
            return null;
        }
        return wp;
    }

    void kill() {
        process.destroy();
    }

    String getOutputFileName() {
        return outputFile;
    }

    String getErrorFileName() {
        return errorFile;
    }

}
