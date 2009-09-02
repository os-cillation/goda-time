package org.goda.chronic;


import org.goda.chronic.tags.Pointer;
import org.goda.time.DateTime;

public class Options {
  private Pointer.PointerType _context;
  private DateTime _now;
  private boolean _guess;
  private boolean _debug;
  private int _ambiguousTimeRange;
  private boolean _compatibilityMode;

  public Options() {
    this(Pointer.PointerType.FUTURE, new DateTime(), true, 6);
  }

  public Options(DateTime now) {
    this(Pointer.PointerType.FUTURE, now, true, 6);
  }

  public Options(DateTime now, boolean guess) {
    this(Pointer.PointerType.FUTURE, now, guess, 6);
  }

  public Options(Pointer.PointerType context) {
    this(context,new DateTime(), true, 6);
  }

  public Options(boolean guess) {
    this(Pointer.PointerType.FUTURE, new DateTime(), guess, 6);
  }

  public Options(int ambiguousTimeRange) {
    this(Pointer.PointerType.FUTURE, new DateTime(), true, ambiguousTimeRange);
  }

  public Options(Pointer.PointerType context, DateTime now, boolean guess, int ambiguousTimeRange) {
    _context = context;
    _now = now;
    _guess = guess;
    _ambiguousTimeRange = ambiguousTimeRange;
  }
  
  public void setDebug(boolean debug) {
    _debug = debug;
  }
  
  public boolean isDebug() {
    return _debug;
  }
  
  public void setCompatibilityMode(boolean compatibilityMode) {
    _compatibilityMode = compatibilityMode;
  }
  
  public boolean isCompatibilityMode() {
    return _compatibilityMode;
  }

  public void setContext(Pointer.PointerType context) {
    _context = context;
  }

  public Pointer.PointerType getContext() {
    return _context;
  }

  public void setNow(DateTime now) {
    _now = now;
  }

  public DateTime getNow() {
    return _now;
  }

  public void setGuess(boolean guess) {
    _guess = guess;
  }

  public boolean isGuess() {
    return _guess;
  }

  public void setAmbiguousTimeRange(int ambiguousTimeRange) {
    _ambiguousTimeRange = ambiguousTimeRange;
  }

  public int getAmbiguousTimeRange() {
    return _ambiguousTimeRange;
  }
}
