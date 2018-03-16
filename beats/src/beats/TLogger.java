package beats;

import java.util.Date;

/* Nothing to do here. This logger helps logging with thread ID which can help in debugging. */
public class TLogger {

    static void log(String logent) {
        System.err.println((new Date()) + " : " + Thread.currentThread().getName() + " : " + logent);
    }
    
}
