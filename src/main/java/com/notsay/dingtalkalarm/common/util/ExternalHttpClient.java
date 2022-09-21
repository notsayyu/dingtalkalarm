package com.notsay.dingtalkalarm.common.util;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.Map;
import org.apache.commons.io.IOUtils;


@Component
@Slf4j
public class ExternalHttpClient {

    private static String httpsProtol = "SSLv3";

    private CloseableHttpClient wrapClient;

    private static PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();

    /**
     * 采用绕过验证的方式处理https  GET请求
     *
     * @param url
     * @return
     */
    public Integer doGet(String url, Map<String, String> paramMap, Map<String, String> headerMap) {
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            for (String key : paramMap.keySet()) {
                uriBuilder.setParameter(key, paramMap.get(key));
            }
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            for (String key : headerMap.keySet()) {
                httpGet.setHeader(key, headerMap.get(key));
            }
            HttpResponse response = wrapClient.execute(httpGet);
            Integer statusCode = response.getStatusLine().getStatusCode();
            return statusCode;
        } catch (Exception e) {
            log.error("请求异常，地址为：{}", url, e);
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 发送http请求
     */
    public String doPost(String url, String bodyJson, Map<String, String> headerMap) {
        try {

            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new StringEntity(bodyJson, ContentType.APPLICATION_JSON));
            for (String key : headerMap.keySet()) {
                httpPost.setHeader(key, headerMap.get(key));
            }
            HttpResponse response = wrapClient.execute(httpPost);
//            int statusCode = response.getStatusLine().getStatusCode();
            @Cleanup InputStream inputStream = response.getEntity().getContent();
            String body = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            return body;
        } catch (Exception e) {
            log.error("请求异常，地址为：{}", url, e);
            e.printStackTrace();
            return "请求钉钉失败";
        }
    }



    /**
     * 忽略证书的CloseableHttpClient
     *
     * @return
     */
    public CloseableHttpClient wrapClient() {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] arg0, String arg1) {
                }
            };
            ctx.init(null, new TrustManager[]{tm}, null);
            SSLConnectionSocketFactory ssf = new SSLConnectionSocketFactory(ctx, NoopHostnameVerifier.INSTANCE);
            //将最大连接数增加到200
            cm.setMaxTotal(200);
            //将每个路由的默认最大连接数增加到20
            cm.setDefaultMaxPerRoute(20);

            RequestConfig requestConfig = RequestConfig.custom()
                    //从连接池中获取连接的超时时间
                    .setConnectionRequestTimeout(3000)
                    //与服务器连接超时时间：httpclient会创建一个异步线程用以创建socket连接，此处设置该socket的连接超时时间
                    .setConnectTimeout(3000)
                    //socket读数据超时时间：从服务器获取响应数据的超时时间
                    .setSocketTimeout(3000)
                    .build();
            CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(cm).setSSLSocketFactory(ssf).setDefaultRequestConfig(requestConfig).build();

            return httpclient;
        } catch (Exception e) {
            return HttpClients.createDefault();
        }
    }

    public ExternalHttpClient() {
        wrapClient = wrapClient();
    }

}
