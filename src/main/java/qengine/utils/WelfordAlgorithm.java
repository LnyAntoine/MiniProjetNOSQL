package qengine.utils;

public class WelfordAlgorithm {
    private long count = 0;
    private double mean = 0.0;
    private double m2 = 0.0;

    public void add(double x) {
        count++;
        double delta = x - mean;
        mean += delta / count;
        double delta2 = x - mean;
        m2 += delta * delta2;
    }

    public long getCount() {
        return count;
    }

    public double getMean() {
        return mean;
    }

    public double getVariance() {
        return count > 1 ? m2 / (count - 1) : 0.0;
    }

    public double getStdDev() {
        return Math.sqrt(getVariance());
    }
}
