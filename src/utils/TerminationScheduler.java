package utils;

import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import static behaviour.TimeTickerBehaviour.DEFAULT_TIME;

public final class TerminationScheduler {

    private static final int TIME_TO_TERMINATION_MS = ((30+1)*DEFAULT_TIME);

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
