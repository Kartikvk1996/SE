package se.ipc;

public class Consts {
    
    public static final String DATA = "DATA";
    public static final String INTRO_GUEST_HOST = "GUEST_HOST";
    public static final String INTRO_GUEST_PORT = "GUEST_PORT";
    public static final String CMD = "CMD";
    public static final String ARGS = "ARGS";
    public static final String CONNECT_PORT = "CONNECT";
    public static final String PID = "PID";
    public static final String DMGR_BIN = "dmgr";
    
    public static String jPath(String ...chunks) {
        StringBuilder path = new StringBuilder();
        for (int i = 0; i < chunks.length - 1; i++) {
            path.append(chunks[i]).append(".");
        }
        return path.append(chunks[chunks.length - 1]).toString();
    }
    
}
