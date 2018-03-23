package se.dscore;

import java.io.IOException;
import se.ipc.ESocket;
import se.ipc.pdu.AckPDU;
import se.ipc.pdu.ConnectPDU;
import se.ipc.pdu.GetPDU;
import se.ipc.pdu.IntroPDU;

public interface RequestHandler {
    void handle(ESocket s) throws IOException;
    void handle_get(ESocket sock, GetPDU gpdu) throws IOException;
    void handle_intro(ESocket sock, IntroPDU ipdu) throws IOException;
    void handle_connect(ESocket sock, ConnectPDU cpdu) throws IOException;
    void handle_ack(ESocket sock, AckPDU apdu) throws IOException;
}
