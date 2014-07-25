package eu.lod2.rsine.dissemination.notifier.logging;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.lod2.rsine.dissemination.notifier.INotifier;

public class LoggingNotifier implements INotifier {

  private final Logger logger = LoggerFactory.getLogger(LoggingNotifier.class);

  public void notify(Collection<String> messages) {
    for (String message : messages) {
      logger.info(message);
    }
  }

}
