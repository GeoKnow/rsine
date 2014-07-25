package eu.lod2.rsine.dissemination.notifier.http;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
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

  private String method_name;
  private URL url;

  public HttpNotifier(String uri, String method) {
    this.method_name = method;
    try {
      this.url = new URL(uri);
    } catch (MalformedURLException e) {
      logger.error("Couldnt initialise " + uri + " " + e.getMessage());
      e.printStackTrace();
    }
  }

  public void notify(Collection<String> messages) {
    HttpClient client = new HttpClient();
    client.getHostConfiguration().setHost(url.getHost());
    HttpMethod method = null;

    if ("POST".equals(method_name)) {
      method = new PostMethod(url.toString());
      ((PostMethod) method).addParameter("messages", messages.toString());

    } else if ("GET".equals(method_name)) {

      StringBuilder params = new StringBuilder();
      try {
        params.append(String
            .format("?messages=%s", URLEncoder.encode(messages.toString(), "UTF-8")));
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }

      String uri = String.format("%s%s", url.toString(), params.toString());
      method = new GetMethod(uri);
    }

    try {
      client.executeMethod(method);
      method.releaseConnection();
    } catch (Exception e) {
      logger.error("Couldnt notify to " + url.toString() + " using " + method_name + " "
          + e.getMessage());
      e.printStackTrace();
    }

  }
}
