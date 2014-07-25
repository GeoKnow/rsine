package eu.lod2.rsine.dissemination.notifier.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.lod2.rsine.dissemination.notifier.INotifier;

public class HttpNotifier implements INotifier {

  private final Logger logger = LoggerFactory.getLogger(HttpNotifier.class);

  private String method_name, uri;

  public HttpNotifier(String method, String uri) {
    this.method_name = method;
    this.uri = uri;
  }

  public void notify(Collection<String> messages) {
    HttpClient client = new HttpClient();
    HttpMethod method = null;

    if ("POST".equals(method_name)) {
      PostMethod proxyMethod = new PostMethod(uri);
      proxyMethod.addParameter("messages", messages.toString());

    } else if ("GET".equals(method_name)) {

      StringBuilder params = new StringBuilder();
      try {
        params.append(String
            .format("?messages=%s", URLEncoder.encode(messages.toString(), "UTF-8")));
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }

      String url = String.format("%s%s", uri, params.toString());
      GetMethod proxyMethod = new GetMethod(url);
    }

    try {
      client.executeMethod(method);
      method.releaseConnection();
    } catch (Exception e) {
      logger.error("Couldnt notify to " + uri + " using " + method_name + " " + e.getMessage());
      e.printStackTrace();
    }

  }
}
