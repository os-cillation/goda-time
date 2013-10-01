package org.goda.chronic.repeaters;

import org.goda.chronic.tags.Pointer.PointerType;
import org.goda.time.MutableInterval;


public class RepeaterSeason extends RepeaterUnit {
  public static final int SEASON_SECONDS = 7862400; // (91 * 24 * 60 * 60);

  @Override
  protected MutableInterval _nextMutableInterval(PointerType pointer) {
    throw new IllegalStateException("Not implemented.");
  }

  @Override
  protected MutableInterval _thisMutableInterval(PointerType pointer) {
    throw new IllegalStateException("Not implemented.");
  }

  @Override
  public MutableInterval getOffset(MutableInterval span, double amount, PointerType pointer) {
    throw new IllegalStateException("Not implemented.");
  }

  @Override
  public int getWidth() {
    // WARN: Does not use Calendar
    return RepeaterSeason.SEASON_SECONDS;
  }

  @Override
  public String toString() {
    return super.toString() + "-season";
  }
}
