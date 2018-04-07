package se.dscore;

import java.io.IOException;
import se.ipc.ESocket;
import se.ipc.pdu.PDU;

public interface RequestHandler {
    void handle(ESocket s) throws IOException;
    void handler(ESocket sock, PDU pdu) throws IOException;
}
