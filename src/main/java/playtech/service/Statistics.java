package playtech.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Statistics {
    private static final Logger LOGGER = LoggerFactory.getLogger(Statistics.class);
    private static final long REPORT_PERIOD_IN_MILLIS = 60 * 1000;

    private int count;
    private long min;
    private long max;
    private long sum;

    public Statistics() {
        new Thread(new StatisticsLogger(this)).start();
    }

    public synchronized void reportRequest(long requestDurationInMillis) {
        count++;
        sum += requestDurationInMillis;

        if (min == 0 || min > requestDurationInMillis) {
            min = requestDurationInMillis;
        }

        if (max < requestDurationInMillis) {
            max = requestDurationInMillis;
        }
    }

    private synchronized StatisticsStatus takeStatistics() {
        final StatisticsStatus status = new StatisticsStatus();
        status.count = count;
        count = 0;
        status.min = min;
        min = 0;
        status.max = max;
        max = 0;
        status.sum = sum;
        sum = 0;
        return status;
    }

    private static class StatisticsStatus {
        public int count;
        public long min;
        public long max;
        public long sum;
    }

    private static class StatisticsLogger implements Runnable {
        private final Statistics statistics;

        private StatisticsLogger(final Statistics statistics) {
            this.statistics = statistics;
        }

        @Override
        public void run() {
            for (; ; ) {
                try {
                    Thread.sleep(REPORT_PERIOD_IN_MILLIS);
                } catch (InterruptedException e) {
                    return;
                }

                final StatisticsStatus status = statistics.takeStatistics();
                if (status.count != 0) {
                    LOGGER.info("STAT: {} requests: duration: min {} ms / average {} ms / max {} ms",
                            new Object[]{status.count, status.min, (status.sum / status.count), status.max});
                }
            }
        }
    }
}
