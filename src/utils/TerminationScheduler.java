package utils;

import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public final class TerminationScheduler {

    private static final int TIME_TO_TERMINATION_MS = ((4*2) + 1) * 1000;

    public static final void scheduleTermination(ContainerController container) {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        try {
                            container.kill();
                            Printer.safePrintf("\n\nTerminating execution ...\n");
                            System.exit(0);
                        } catch (StaleProxyException e) {
                            e.printStackTrace();
                        }
                    }
                },
                TIME_TO_TERMINATION_MS
        );
    }
}
