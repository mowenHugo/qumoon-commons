package com.qumoon.commons;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;

import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

/**
 * @author kevin
 */
public class WebUtils {

  public static final String
      USER_AGENT_1 =
      "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.65 Safari/537.36";
  private static Logger logger = LoggerFactory.getLogger(WebUtils.class);

  public static Map<String, String> getQueryParameterPair(String url) throws MalformedURLException {
    Map<String, String> queryPairs = Maps.newLinkedHashMap();
    String query = new URL(url).getQuery();
    String[] pairs = query.split("&");
    for (String pair : pairs) {
      int idx = pair.indexOf("=");
      queryPairs.put(Encodes.urlDecode(pair.substring(0, idx)), Encodes.urlDecode(pair.substring(idx + 1)));
    }
    return queryPairs;
  }

  public static Map<String, String> getQueryParameterPair2(String query) {
    Map<String, String> queryPairs = Maps.newLinkedHashMap();
    String[] pairs = query.split("&");
    for (String pair : pairs) {
      int idx = pair.indexOf("=");
      queryPairs.put(Encodes.urlDecode(pair.substring(0, idx)), Encodes.urlDecode(pair.substring(idx + 1)));
    }
    return queryPairs;
  }

  /**
   * 下载文件
   *
   * @param url         下载地址
   * @param filename    保存的文件名
   * @param overwritten 是否覆盖同名文件
   * @return 下载的文件大小
   */
  public static long download(String url, String filename, boolean overwritten) throws IOException {
    File file = new File(filename);
    if (overwritten) {
      FileUtils.deleteQuietly(file);
    }

    HttpClient httpclient = new DefaultHttpClient();
    HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 5000);
    HttpGet httpget = new HttpGet(url);
    HttpResponse response = httpclient.execute(httpget);
    int statusCode = response.getStatusLine().getStatusCode();
    if (statusCode == 200 || statusCode == 301 || statusCode == 302
        || statusCode == 304) {
      HttpEntity entity = response.getEntity();
      if (entity != null) {
        InputStream inputStream = entity.getContent();
        try {
          OutputStream output = new FileOutputStream(file);
          try {
            logger.info("start to download from {} to {}", url, filename);
            Stopwatch stopwatch = new Stopwatch().start();
            long size = ByteStreams.copy(inputStream, output);
            stopwatch.stop();
            long time = stopwatch.elapsed(TimeUnit.SECONDS);
            long speed = (time == 0 ? size : (size / time));
            logger.info("download ok , size[{}] , speed[{}/s]",
                        FileUtils.byteCountToDisplaySize(size),
                        FileUtils.byteCountToDisplaySize(speed));
          } finally {
            Closeables.closeQuietly(output);
          }
        } finally {
          Closeables.closeQuietly(inputStream);
        }
      }
    }
    return FileUtils.sizeOf(file);
  }

  public static Header[] getHttpHead(HttpResponse httpResponse) throws Exception {
    return httpResponse.getAllHeaders();
  }

  public static Header[] getHttpHead(final String url) throws Exception {
    return getHttpHead(url, null);
  }

  public static Header[] getHttpHeadViaProxy(final String url, final String ip, int port,
                                             String schema) throws Exception {
    HttpHost httpHost = new HttpHost(ip, port, schema);
    return getHttpHead(url, httpHost);
  }

  public static Header[] getHttpHead(final String url, final HttpHost httpHost) throws Exception {
    HttpClient httpclient = new DefaultHttpClient();
    HttpHead httpHead = new HttpHead(url);
    HttpParams params = new BasicHttpParams();
    params.setParameter("http.socket.timeout", 10000);
    params.setParameter("http.connection.timeout", 10000);
    if (null != httpHost) {
      params.setParameter(ConnRoutePNames.DEFAULT_PROXY, httpHost);
    }
    httpHead.setParams(params);
    httpHead.setHeader("User-Agent", USER_AGENT_1);
    return httpclient.execute(httpHead).getAllHeaders();
  }

  public static Header[] getHttpHead(final String url, int retry) throws Exception {
    int count = 0;
    while (true) {
      try {
        return getHttpHead(url);
      } catch (Exception e) {
        count++;
        if (count > retry) {
          throw e;
        } else {
          logger.info("retry[{}/{}] get http headers of url[{}]", count, retry, url);
        }
      }
    }
  }

  public static String getHttpBody(final String url) throws Exception {
    HttpResponse response = getHttpResponse(url);
    HttpEntity entity = response.getEntity();
    return EntityUtils.toString(entity);
  }

  public static String getHttpBodyViaProxy(final String url, final String ip, int port,
                                           String schema) throws Exception {
    HttpResponse response = getHttpResponseViaProxy(url, ip, port, schema);
    HttpEntity entity = response.getEntity();
    return EntityUtils.toString(entity);
  }

  public static String getHttpBody(HttpResponse httpResponse) throws Exception {
    HttpEntity entity = httpResponse.getEntity();
    return EntityUtils.toString(entity);
  }

  public static String getHttpBody(final String url, int retry) throws Exception {
    int count = 0;
    while (true) {
      try {
        return getHttpBody(url);
      } catch (Exception e) {
        count++;
        if (count > retry) {
          throw e;
        } else {
          logger.info("retry[{}/{}] get http body of url[{}]", count, retry, url);
        }
      }
    }
  }

  public static HttpResponse getHttpResponseViaProxy(final String url, final String ip, int port,
                                                     String schema) throws Exception {
    HttpHost httpHost = new HttpHost(ip, port, schema);
    return getHttpResponse(url, httpHost);
  }

  public static HttpResponse getHttpResponse(final String url, int retry) throws Exception {
    int count = 0;
    while (true) {
      try {
        return getHttpResponse(url);
      } catch (Exception e) {
        count++;
        if (count > retry) {
          throw e;
        } else {
          logger.info("retry[{}/{}] get http response of url[{}]", count, retry, url);
        }
      }
    }
  }

  public static HttpResponse getHttpResponse(final String url) throws Exception {
    return getHttpResponse(url, null);
  }

  public static HttpResponse getHttpResponse(final String url, final HttpHost httpHost) throws Exception {

    HttpClient httpclient = new DefaultHttpClient();
    HttpGet httpGet = new HttpGet(url);
    HttpParams params = new BasicHttpParams();
    params.setParameter("http.socket.timeout", 10000);
    params.setParameter("http.connection.timeout", 10000);
    if (null != httpHost) {
      params.setParameter(ConnRoutePNames.DEFAULT_PROXY, httpHost);
    }
    httpGet.setParams(params);
    httpGet.setHeader("User-Agent", USER_AGENT_1);
    try {
      return httpclient.execute(httpGet);
    } catch (Exception e) {
      httpGet.releaseConnection();
      throw e;
    }
  }

  public static String getHttpBodyViaProxy(final String url, int retry, final String ip, int port,
                                           String type) throws Exception {
    int count = 0;
    while (true) {
      try {
        return getHttpBodyViaProxy(url, ip, port, type);
      } catch (Exception e) {
        count++;
        if (count > retry) {
          throw e;
        } else {
          logger.info("retry[{}/{}] get http body via proxy {} of url[{}]", count, retry,
                      "<" + type + ">" + ip + ":" + port, url);
        }
      }
    }
  }

  public static String getExternalIp() throws Exception {
    return getHttpBody("http://checkip.amazonaws.com");

  }

  public static List<String> getInternalIps() throws SocketException {
    List<String> ipList = Lists.newArrayList();
    for (Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
         networkInterfaces.hasMoreElements(); ) {
      NetworkInterface networkInterface = networkInterfaces.nextElement();
      if (networkInterface.isUp() && !networkInterface.isLoopback()) {
        for (Enumeration<InetAddress> enumIpAddr = networkInterface.getInetAddresses();
             enumIpAddr.hasMoreElements(); ) {
          InetAddress inetAddress = enumIpAddr.nextElement();
          if (inetAddress instanceof Inet4Address) {
            ipList.add(inetAddress.getHostAddress());
          }
        }
      }
    }
    return ipList;
  }

  public static String get192Ip() throws SocketException {
    for (String ip : getInternalIps()) {
      if (ip.startsWith("192")) {
        return ip;
      }
    }
    return null;
  }

  public static String getRequestHost(HttpServletRequest request) {
    return String.format("%s://%s:%d", request.getScheme(), request.getServerName(), request.getServerPort());
  }
}
