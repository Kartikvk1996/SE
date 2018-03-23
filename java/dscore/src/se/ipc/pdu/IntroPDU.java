package se.ipc.pdu;

import java.util.Arrays;
import se.ipc.Consts;
import se.util.Logger;

public class IntroPDU extends PDU {

    public static final String GUEST_PORT = "GUEST_PORT";
    public static final String GUEST_HOST = "GUEST_HOST";
    
    public IntroPDU(String host, int port) {
        super();
        setMethod(PDU.METHOD_INTRO);
        try {
            setValue(Consts.jPath(PDU.DATA, GUEST_HOST), host);
            setValue(Consts.jPath(PDU.DATA, GUEST_PORT), port + "");
        } catch (JsonPathNotExistsException ex) {
            Logger.ilog(Logger.HIGH, Arrays.toString(ex.getStackTrace()));
        }
    }
    
    public String getGuestHost() {
        return getValue(Consts.jPath(PDU.DATA, GUEST_HOST));
    }
    
    public int getGuestPort() {
        return Integer.parseInt(getValue(Consts.jPath(PDU.DATA, GUEST_PORT)));
    }
    
}
