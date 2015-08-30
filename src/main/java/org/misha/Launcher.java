package org.misha;

import org.apache.commons.math3.distribution.LogNormalDistribution;
import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.apache.commons.math3.util.FastMath;
import org.apache.log4j.Logger;
import org.misha.fitting.Fitter;
import org.misha.fitting.Fitter.Interval;

import java.io.File;

import static org.misha.fitting.Fitter.*;

/**
 * author: misha
 * date: 8/29/15 10:54 PM.
 */
public class Launcher {
    private static final Logger log = Logger.getLogger(Launcher.class);

    public static void main(final String... args) throws Exception {
        final File file = new File("resources/data/1-0.csv");
        double max = -1d;
        int index = -1;
        for (int var = 13; var < 287; ++var) {
            final Fitter fitter = fitter(file, var);
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
            final double lm = doubleMean(logObserved);
            final double logVar = doubleVar(logObserved);
            i = 0;
            for (final Interval<Integer> interval : fitter.intervals()) {
                defineExpected(logExpected, i, new LogNormalDistribution(lm, logVar), interval);
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
