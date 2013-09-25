package com.trilemon.commons;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
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

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author kevin
 */
public class WebUtils {
    public static final String USER_AGENT_1 = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.65 Safari/537.36";
    private static Logger logger = LoggerFactory.getLogger(WebUtils.class);

    public static Map<String, String> getQueryParameterPair(String url) throws MalformedURLException, UnsupportedEncodingException {
        Map<String, String> queryPairs = Maps.newLinkedHashMap();
        String query = new URL(url).getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            queryPairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
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

    /**
     * 解压缩zip文件。
     *
     * @param sourceFile 压缩文件。
     * @param desDir     目标目录
     * @param password   密码
     * @return 解压缩出来的的 {@link java.io.File}
     * @throws net.lingala.zip4j.exception.ZipException
     *          解压缩错误抛出异常
     */
    public static File[] unzip(String sourceFile, String desDir, String password) throws ZipException {
        ZipFile zipFile = new ZipFile(sourceFile);
        if (zipFile.isEncrypted() && null != password) {
            zipFile.setPassword(password);
        }
        zipFile.extractAll(desDir);
        return new File(desDir).listFiles();
    }

    /**
     * 解压缩zip文件。
     *
     * @param sourceFile 压缩文件。
     * @param desDir     目标目录
     * @return 解压缩出来的的 {@link java.io.File}
     * @throws ZipException 解压缩错误抛出异常
     */
    public static File[] unzip(String sourceFile, String desDir)
            throws ZipException {
        return unzip(sourceFile, desDir, null);
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
        params.setParameter("http.socket.timeout", 8000);
        params.setParameter("http.connection.timeout", 8000);
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
        params.setParameter("http.socket.timeout", 8000);
        params.setParameter("http.connection.timeout", 8000);
        if (null != httpHost) {
            params.setParameter(ConnRoutePNames.DEFAULT_PROXY, httpHost);
        }
        httpGet.setParams(params);
        httpGet.setHeader("User-Agent", USER_AGENT_1);
        return httpclient.execute(httpGet);
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
}
