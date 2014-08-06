package eu.lod2.rsine.remotenotification;

import javax.naming.ServiceUnavailableException;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;

public class UnavailableRemoteServiceDetector implements IRemoteServiceDetector {

  public URI getRemoteService(Resource resource) throws ServiceUnavailableException {
    throw new ServiceUnavailableException("Not available by definition");
  }

}
