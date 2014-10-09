/*
 * The MIT License
 * 
 * Copyright (c) 2014 schristou88
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.cloudbees.jenkins.support.timer;

import com.sun.management.UnixOperatingSystemMXBean;
import hudson.Extension;
import hudson.model.PeriodicWork;
import jenkins.model.Jenkins;
import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @author schristou88
 */
@Extension
public class EntropyTrackChecker extends PeriodicWork {
  private final File entropyFile = new File(
          new File(Jenkins.getInstance().getRootDir(), "logs"),
          "custom/entropyLevels.log");
  private final File entropyAvailFile = new File("/proc/sys/kernel/random/entropy_avail");

  Map<String, Integer> entropyLevels = new LinkedMap(40); // Keep max 40 entropy levels

  OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
  final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HHmmss");


  @Override
  public long getRecurrencePeriod() {
    return TimeUnit.SECONDS.toMillis(60); // Check every minute.
  }

  public File getEntropyFile() {
    return entropyFile;
  }

  @Override
  protected void doRun() throws FileNotFoundException, UnsupportedEncodingException {
    // If not Unix, ignore.
    if (operatingSystemMXBean instanceof UnixOperatingSystemMXBean) {
      if (!entropyAvailFile.exists()) return; // If there's no entropy avilable file.

      LOGGER.fine("Entropy Level Checker");

      PrintWriter builder = new PrintWriter(
              new BufferedOutputStream(
                      new FileOutputStream(entropyFile, true)));
      try {
        Integer entropy = Integer.valueOf(
                FileUtils.readFileToString(entropyAvailFile));

        String date = format.format(new Date());
        entropyLevels.put(date, entropy);

        builder.println(date + " - " + entropy);
      } catch (IOException e) {
        // Ignore exceptions if the file does not exist.
      } finally {
        builder.close();
      }
    }
  }

  private static final Logger LOGGER = Logger.getLogger(EntropyTrackChecker.class.getCanonicalName());
}


