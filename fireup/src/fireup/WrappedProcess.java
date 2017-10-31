package fireup;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 *
 * @author Madhusoodan Pataki
 */
public class WrappedProcess {
    
    String procName;
    String startTime;
    Process process;
    String errorFile, outputFile;

    public static WrappedProcess createProcess(String ...command) {

        WrappedProcess wp = new WrappedProcess();
        
        File err = new File(((new Date()).toString() + "err.txt").replaceAll(" ", "_"));
        File out = new File(((new Date()).toString() + "out.txt").replaceAll(" ", "_"));
        
        wp.errorFile = err.getAbsolutePath();
        wp.outputFile = out.getAbsolutePath();
        
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectError(err);
        pb.redirectOutput(out);
        
        wp.procName = command[0];
        wp.startTime = (new Date()).toString();
        
        try {
            wp.process = pb.start();
        } catch (IOException ex) {
            System.err.println(ex);
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
