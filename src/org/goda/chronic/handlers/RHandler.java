package org.goda.chronic.handlers;

import java.util.List;
import org.goda.chronic.Options;
import org.goda.chronic.utils.Token;
import org.goda.time.MutableInterval;

public class RHandler implements IHandler {

  public MutableInterval handle(List<Token> tokens, Options options) {
    List<Token> ddTokens = Handler.dealiasAndDisambiguateTimes(tokens, options);
    return Handler.getAnchor(ddTokens, options);
  }

}
