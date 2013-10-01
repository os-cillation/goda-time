package org.goda.chronic.handlers;

import java.util.List;
import org.goda.chronic.Options;
import org.goda.util.CollectionUtils;
import org.goda.chronic.utils.Token;
import org.goda.time.MutableInterval;


public class SRPAHandler extends SRPHandler {

  @Override
  public MutableInterval handle(List<Token> tokens, Options options) {
    MutableInterval anchorMutableInterval = Handler.getAnchor(CollectionUtils.subList(tokens,3, tokens.size()), options);
    return super.handle(tokens, anchorMutableInterval, options);
  }

}
