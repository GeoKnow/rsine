package eu.lod2.rsine.dissemination.notifier.logging;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import eu.lod2.rsine.dissemination.notifier.INotifier;
import eu.lod2.rsine.dissemination.notifier.NotifierDescriptor;
import eu.lod2.rsine.dissemination.notifier.NotifierParameters;
import eu.lod2.util.Namespaces;

public class LoggingNotifierDescriptor implements NotifierDescriptor {

  public URI getType() {
    return new URIImpl(Namespaces.RSINE_NAMESPACE.getName() + "loggingNotifier");
  }

  public NotifierParameters getParameters() {
    return new NotifierParameters();
  }

  public INotifier create(NotifierParameters parameters) {
    return new LoggingNotifier();
  }

}
