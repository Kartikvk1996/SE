package ipcdump;

import java.io.IOException;
import java.io.PrintWriter;
import jsonparser.DictObject;
import jsonparser.Formatter;
import jsonparser.Json;
import jsonparser.JsonException;
import se.ipc.pdu.AckPDU;
import se.ipc.pdu.CommandPDU;
import se.ipc.pdu.ConnectPDU;
import se.ipc.pdu.CreatePDU;
import se.ipc.pdu.DiePDU;
import se.ipc.pdu.ErrorPDU;
import se.ipc.pdu.GetPDU;
import se.ipc.pdu.HiPDU;
import se.ipc.pdu.IntroPDU;
import se.ipc.pdu.InvalidPDUException;
import se.ipc.pdu.KillPDU;
import se.ipc.pdu.PDU;
import se.ipc.pdu.SearchPDU;
import se.ipc.pdu.StatusPDU;

public class IPCDump {

    static PrintWriter pw;

    public static void main(String[] args) throws InvalidPDUException, JsonException, IOException {

        pw = new PrintWriter("ipcdump.html");
        DictObject kvp = new DictObject();
        kvp.set("${key}", "${value}");

        PDU.setProcessRole("${role of the generator process>");
        
        PDU pdus[] = {
            new AckPDU("${ticket}", 7348),
            new ConnectPDU("${ticket}", "${process id}", 6433, 8787),
            new CreatePDU("${executable name}", "${arg1} ${arg2} ..."),
            new DiePDU(),
            new ErrorPDU("${this is a error message}"),
            new GetPDU("${resource name}"),
            new HiPDU(7738, 80),
            new IntroPDU("${guest host}", 8734, "${guest type}"),
            new KillPDU("${process id}"),
            new SearchPDU("${search query}"),
            new StatusPDU()
        };

        sop("<html}<head><style>.classname{font-weight: bolder; font-family: monospace; font-size: 1.2em;}</style></head>");
        for (PDU pdu : pdus) {
            dumpPDU(pdu);
        }
        
        pw.close();
    }

    private static void dumpPDU(PDU p) throws JsonException, IOException {
        sop("<span class='classname'>");
        sop(p.getClass().getName());
        sop("</span>");
        sop("<pre>");
        String out = Formatter.format(Json.dump(p));
        sop(out);
        sop("</pre>");
    }

    private static void sop(String str) {
        pw.println(str);
    }

}
