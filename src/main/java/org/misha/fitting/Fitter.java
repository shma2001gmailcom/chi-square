package org.misha.fitting;

import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import org.apache.log4j.Logger;
import org.misha.io.Io;

import java.io.File;
import java.util.*;

/**
 * author: misha
 * date: 8/21/15 10:16 PM.
 */
public class Fitter implements Iterable<Long> {
    private static final Logger log = Logger.getLogger(Fitter.class);
    private final File data;
    private final int range;
    private final List<Integer> callTimes = new ArrayList<Integer>();
    private final Map<Interval<Integer>, Long> empiricDistribution = new TreeMap<Interval<Integer>, Long>();
    private final Map<Integer, Interval<Integer>> pointToIntervalMap = new TreeMap<Integer, Interval<Integer>>();
    private Set<Interval<Integer>> intervals;

    private Fitter(final File data, final int range) {
        this.data = data;
        this.range = range;
    }

    public static Fitter fitter(final File data, final int range) throws Exception {
        final Fitter fitter = new Fitter(data, range);
        fitter.init();
        return fitter;
    }

    public static void defineExpected(
            final double[] expected, final int i, final AbstractRealDistribution distribution,
            final Interval<Integer> interval
    ) {
        expected[i] = distribution.density(0.5 * (interval.left + interval.right));
    }

    public static double doubleVar(final double[] observed) {
        return new Variance(false).evaluate(observed);
    }

    public static double doubleMean(final double[] observed) {
        return new Mean().evaluate(observed);
    }

    public void init() throws Exception {
        Io.parseData(callTimes, data);
        makeEmpiricDistribution();
        Io.writeToCsv(empiricDistribution, range);
        intervals = new TreeSet<Interval<Integer>>(pointToIntervalMap.values());
    }

    public int size() {
        return empiricDistribution.size();
    }

    public void makeEmpiricDistribution() throws Exception {
        int currentValue = callTimes.get(0);
        int counter = 0;
        Collection<Integer> points = new TreeSet<Integer>();
        for (final Integer callTime : callTimes) {
            ++counter;
            points.add(callTime);
            if (callTime - currentValue > range) {
                final Interval<Integer> interval = Interval.interval(currentValue, callTime);
                for (final Integer point : points) {
                    pointToIntervalMap.put(point, interval);
                }
                empiricDistribution.put(interval, (long) counter);
                points = new ArrayList<Integer>();
                currentValue = callTime;
                //log.debug(interval + " " + counter);
                counter = 0;
            }
        }
    }

    @Override
    public Iterator<Long> iterator() {
        return empiricDistribution.values().iterator();
    }

    synchronized public Intervals intervals() {
        return new Intervals();
    }

    public static class Interval<T extends Comparable<T>> implements Comparable<Interval<T>> {
        private final T left;
        private final T right;

        private Interval(final T left, final T right) {
            if (left == null || right == null) {
                throw new IllegalArgumentException("left=" + left + "; right=" + right);
            }
            this.left = left;
            this.right = right;
        }

        public static <S extends Comparable<S>> Interval<S> interval(final S left, final S right) {
            if (left.compareTo(right) >= 0) {
                throw new IllegalArgumentException("left must be less than or equal to right");
            }
            return new Interval<S>(left, right);
        }

        @Override
        public String toString() {
            return "[" + left + ", " + right + "]";
        }

        @Override
        public int compareTo(final Interval<T> o) {
            return right.compareTo(o.right);
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final Interval interval = (Interval) o;
            return left.equals(interval.left) && right.equals(interval.right);
        }

        @Override
        public int hashCode() {
            int result = left.hashCode();
            result = 31 * result + (right.hashCode());
            return result;
        }
    }

    public class Intervals implements Iterable<Interval<Integer>> {
        @Override
        public Iterator<Interval<Integer>> iterator() {
            return intervals.iterator();
        }
    }
}
