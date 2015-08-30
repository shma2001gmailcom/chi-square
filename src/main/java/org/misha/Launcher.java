package org.misha;

import org.apache.commons.math3.distribution.LogNormalDistribution;
import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.apache.commons.math3.util.FastMath;
import org.apache.log4j.Logger;
import org.misha.fitting.Fitter;

import java.io.File;

/**
 * author: misha
 * date: 8/29/15 10:54 PM.
 */
public class Launcher {
    private static final Logger log = Logger.getLogger(Launcher.class);

    public static void main(final String... args) throws Exception {
        final File file = new File("resources/data/1-0.csv");
        double max = 0d;
        int index = 7;
        for (int var = 13; var < 287; ++var) {
            final Fitter fitter = Fitter.fitter(file, var);
            final int size = fitter.size();
            final double[] logExpected = new double[size];
            final double[] logObserved = new double[size];
            final long[] logObservedLong = new long[size];
            int i = 0;
            for (final Long x : fitter) {
                logObserved[i] = FastMath.log(x);
                logObservedLong[i] = (long) logObserved[i];
                ++i;
            }
            final double lm = Fitter.doubleMean(logObserved);
            final double logVar = Fitter.doubleVar(logObserved);
            i = 0;
            for (final Fitter.Interval<Integer> interval : fitter.intervals()) {
                Fitter.defineExpected(logExpected, i, new LogNormalDistribution(lm, logVar), interval);
                ++i;
            }
            final double result = new ChiSquareTest().chiSquareTest(logExpected, logObservedLong);
            log.debug(var + " " + result);
            if (max < result) {
                max = result;
                index = var;
            }
        }
        log.info(max);
        log.info(index);
    }
}
