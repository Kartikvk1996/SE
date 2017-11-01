import java.io.*;
import java.net.*;
import java.util.*;

class server {
	public static void main(String args[]) {
		try {
			ServerSocket sock = new ServerSocket(0);
			System.out.println("port=" + sock.getLocalPort());
			while(true) {
				Socket conn = sock.accept();
				BufferedReader br = new BufferedReader(
						new InputStreamReader(
						    conn.getInputStream()
						)
					);
				String line;
				while((line=br.readLine()) != null)
					System.out.println(line);
				conn.close();
			}
		} catch(Exception e) {}
	}
}
