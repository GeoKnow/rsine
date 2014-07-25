package eu.lod2.rsine.dissemination.notifier.http;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.XMLSchema;

import eu.lod2.rsine.dissemination.notifier.INotifier;
import eu.lod2.rsine.dissemination.notifier.NotifierDescriptor;
import eu.lod2.rsine.dissemination.notifier.NotifierParameters;
import eu.lod2.util.Namespaces;

public class HttpNotifierDescriptor implements NotifierDescriptor {

  public static final URI HTTP_ABSOLUTE_URI = new URIImpl("http://www.w3.org/2011/http#absoluteURI");
  public static final URI HTTP_METHOD_NAME = new URIImpl("http://www.w3.org/2011/http#methodName");

  public URI getType() {
    return new URIImpl(Namespaces.RSINE_NAMESPACE.getName() + "emailNotifier");
  }

  public NotifierParameters getParameters() {
    NotifierParameters params = new NotifierParameters();
    params.add(HTTP_ABSOLUTE_URI, XMLSchema.ANYURI, true);
    params.add(HTTP_METHOD_NAME, XMLSchema.ANYURI, true);
    return params;
  }

  public INotifier create(NotifierParameters parameters) {
    return new HttpNotifier(parameters.getValueById(HTTP_ABSOLUTE_URI).stringValue(), parameters
        .getValueById(HTTP_METHOD_NAME).stringValue());
  }

}
