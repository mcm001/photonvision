package org.photonvision.server;

import org.photonvision.common.util.TimedTaskManager;

import edu.wpi.first.networktables.IntegerPublisher;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * Provides an interface to the NetworkTables server used by the frontend. This runs a NetworkTables server on
 * - NT4/4.1: 5801
 * - NT3 (should not be used): 5802
 */
public class NtDashboardInterface {
    private static final int kNt3Port = 5802; // NT3 port
    private static final int kNt4Port = 5801; // NT4/4.1 port

    private final NetworkTableInstance kDashboardServerInst;

    int hb = 1;
    private final IntegerPublisher heartbeat;

    private NtDashboardInterface() {
        kDashboardServerInst = NetworkTableInstance.create();
        kDashboardServerInst.startServer("dashboard_nt.json", "", kNt3Port, kNt4Port);
        heartbeat = kDashboardServerInst.getIntegerTopic("/backend/heartbeat").publish();

        TimedTaskManager.getInstance().addTask("NtDashboard", () -> {
            heartbeat.set(hb++);
        }, 0, 1000);
    }

    private static NtDashboardInterface INSTANCE;

    public static synchronized NtDashboardInterface getInstance() {
        if (INSTANCE == null) INSTANCE = new NtDashboardInterface();
        return INSTANCE;
    }
}
