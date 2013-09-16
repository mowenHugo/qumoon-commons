package com.trilemon.commons;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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
     * @throws net.lingala.zip4j.exception.ZipException 解压缩错误抛出异常
     */
    public static File[] unzip(String sourceFile, String desDir)
            throws ZipException {
        return unzip(sourceFile, desDir, null);
    }

    public static String getContent(final String url) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        HttpParams params = new BasicHttpParams();
        params.setParameter("http.socket.timeout", 10000);
        params.setParameter("http.connection.timeout", 10000);
        httpGet.setParams(params);
        HttpResponse response = httpclient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        return EntityUtils.toString(entity);
    }

    public static void main(String[] args) throws Exception {
        System.out.println(getContent("http://fuwu.taobao.com/serv/rencSubscList.do?serviceCode=ts-11496&currentPage="));
    }
}
