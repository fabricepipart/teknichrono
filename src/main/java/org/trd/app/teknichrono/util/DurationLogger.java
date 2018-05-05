package org.trd.app.teknichrono.util;

import org.jboss.logging.Logger;

public class DurationLogger {

  private Logger endpointLogger;
  private long start;
  private String doing;

  public DurationLogger(Logger logger) {
    this.endpointLogger = logger;
  }

  public static DurationLogger get(Logger logger) {
    DurationLogger perfLogger = new DurationLogger(logger);
    return perfLogger;
  }

  public DurationLogger start(String doing) {
    this.doing = doing;
    this.start = System.nanoTime();
    return this;
  }

  public DurationLogger end() {
    long end = System.nanoTime();
    double duration = (end - start) / 1000000.0;
    endpointLogger.info(doing + " (" + String.format("%.02f", duration) + " ms)");
    return this;
  }
}
