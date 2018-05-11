package se.ipc;

import se.ipc.pdu.PDU;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import jsonparser.DictObject;
import jsonparser.Json;
import jsonparser.JsonException;
import se.ipc.pdu.AckPDU;
import se.ipc.pdu.CommandPDU;
import se.ipc.pdu.ConnectPDU;
import se.ipc.pdu.CreatePDU;
import se.ipc.pdu.DiePDU;
import se.ipc.pdu.GetPDU;
import se.ipc.pdu.HiPDU;
import se.ipc.pdu.IntroPDU;
import se.ipc.pdu.InvalidPDUException;
import se.ipc.pdu.KillPDU;
import se.ipc.pdu.PDUConsts;
import se.ipc.pdu.SearchPDU;
import se.ipc.pdu.StatusPDU;
import se.util.Logger;

public class ESocket {

    private Socket socket;

    public ESocket(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
    }

    ESocket(Socket accept) {
        this.socket = accept;
    }

    public void send(PDU iPDU) throws IOException {
        try {
            String str = Json.dump(iPDU);
            Logger.ilog(Logger.PROTO, str);
            socket.getOutputStream().write(str.getBytes());
        } catch (JsonException ex) {
            Logger.elog(Logger.HIGH, "Unable to send data. Json Exception");
        }
        socket.getOutputStream().flush();
    }

    public String getHost() {
        return socket.getInetAddress().getHostAddress();
    }

    public OutputStream getOutputStream() throws IOException {
        return socket.getOutputStream();
    }

    public InputStream getInputStream() throws IOException {
        return socket.getInputStream();
    }

    public int getPort() {
        return socket.getPort();
    }

    public String readData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void close() throws IOException {
        socket.close();
    }

    public PDU recvPDU() throws IOException, JsonException, InvalidPDUException {
        DictObject jObj = (DictObject) Json.parse(getInputStream());
        Logger.ilog(Logger.PROTO, jObj.toString());
        PDU tmp = new PDU(jObj);
        switch (tmp.getMethod()) {
            case PDUConsts.METHOD_ACK:
                return new AckPDU(jObj);
            case PDUConsts.METHOD_CONNECT:
                return new ConnectPDU(jObj);
            case PDUConsts.METHOD_CREATE:
                return new CreatePDU(jObj);
            case PDUConsts.METHOD_ERROR:
                return new GetPDU(jObj);
            case PDUConsts.METHOD_INTRO:
                return new IntroPDU(jObj);
            case PDUConsts.METHOD_GET:
                return new GetPDU(jObj);
            case PDUConsts.METHOD_STATUS:
                return new StatusPDU(jObj);
            case PDUConsts.METHOD_UPDATE:
                break;
            case PDUConsts.METHOD_KILL:
                return new KillPDU(jObj);
            case PDUConsts.METHOD_COMMAND:
                return new CommandPDU(jObj);
            case PDUConsts.METHOD_DIE:
                return new DiePDU();
            case PDUConsts.METHOD_HI:
                return new HiPDU(jObj);
            case PDUConsts.METHOD_SEARCH:
                return new SearchPDU(jObj);
        }
        return tmp;
    }

    @Override
    public String toString() {
        return "{ host: " + getHost() + ", port : " + getPort() + " }";
    }
    
}
