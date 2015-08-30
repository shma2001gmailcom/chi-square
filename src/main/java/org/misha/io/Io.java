package org.misha.io;

import org.apache.commons.math3.util.FastMath;
import org.apache.log4j.Logger;
import org.misha.fitting.Fitter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * author: misha
 * date: 8/29/15 10:38 PM.
 */
public class Io {
    private static final Logger log = Logger.getLogger(Io.class);

    public static void parseData(final List<Integer> callTimes, final File data) throws FileNotFoundException {
        log.info("parsing data file");
        final Scanner sc = new Scanner(data);
        String line;
        int nextInt = 0;
        int i = 0;
        while (sc.hasNextLine()) {
            line = sc.nextLine();
            i++;
            if (line != null) {
                try {
                    nextInt = Integer.parseInt(line);
                } catch (final NumberFormatException e) {
                    log.error("line #" + i + " being " + line + " isn't an int. Will be ignored.");
                }
                if (nextInt > 0 && nextInt != 7) {
                    callTimes.add(nextInt);
                }
            }
        }
        sc.close();
        Collections.sort(callTimes);
    }

    public static void writeToCsv(
            final Map<Fitter.Interval<Integer>, Long> empiricDistribution, final int range
    ) throws IOException {
        final File file = new File("resources/trash/dis-r-" + range + "-" + System.currentTimeMillis() + ".csv");
        final FileWriter fileWriter = new FileWriter(file);
        for (final Map.Entry<Fitter.Interval<Integer>, Long> entry : empiricDistribution.entrySet()) {
            fileWriter.append(entry.getKey().toString()).append(" ")
                      .append(((Double) FastMath.log(entry.getValue())).toString()).append('\n');
        }
        fileWriter.flush();
        fileWriter.close();
    }
}
