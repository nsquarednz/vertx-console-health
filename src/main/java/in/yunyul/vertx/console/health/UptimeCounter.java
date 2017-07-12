package in.yunyul.vertx.console.health;

public class UptimeCounter {
    private long up = 0;
    private long down = 0;

    private final long windowSize;

    public UptimeCounter(long windowSize) {
        this.windowSize = windowSize;
    }

    public void updateStatus(boolean isUp) {
        if (up + down == windowSize) {
            if (isUp) {
                if (down > 0) {
                    ++up;
                    --down;
                }
            } else {
                if (up > 0) {
                    ++down;
                    --up;
                }
            }
        } else {
            if (isUp) {
                ++up;
            } else {
                ++down;
            }
        }
    }

    public float getUptimeRatio() throws IllegalStateException {
        long total = getSamplesInWindow();
        if (total == 0) {
            throw new IllegalStateException("No samples in counter");
        }
        return (float) up / (total);
    }

    public long getSamplesInWindow() {
        return up + down;
    }
}
