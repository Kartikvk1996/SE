package se.dscore;

import java.util.LinkedHashMap;

public interface Scheduler {
    void schedule(String host, LinkedHashMap<String, NodeProxy> slaves);
}
