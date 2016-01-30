package com.crawler.service;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 负责和外部接口对接
 * 
 */
@Service
public class ApiService implements BeanFactoryAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiService.class);

//    @Autowired(required = false)
//    // 如果spring容器中存在就注入
//    private CloseableHttpClient httpClient;

    @Autowired(required = false)
    private RequestConfig requestConfig;

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /**
     * 从Spring容器中获取多例的CloseableHttpClient对象
     * @return
     */
    public CloseableHttpClient getHttpClient() {
        return this.beanFactory.getBean(CloseableHttpClient.class);
    }

    /**
     * 执行get请求
     * 
     * @param url
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public String doGet(String url) throws ClientProtocolException, IOException {
        // 创建http GET请求
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(this.requestConfig);// 设置请求参数
        CloseableHttpResponse response = null;
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("执行GET请求! url = {}", url);
        }
        try {
            // 执行请求
            response = getHttpClient().execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                if (LOGGER.isDebugEnabled()) {
                    // 判断是否启用debug模式
                    LOGGER.debug("获取的响应内容  Content = {}, url = {}", content, url);
                }
                return content;
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return null;
    }

    /**
     * 带参数的get请求
     * 
     * @param url
     * @param params
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     */
    public String doGet(String url, Map<String, String> params) throws ClientProtocolException, IOException,
            URISyntaxException {
        URIBuilder builder = new URIBuilder(url);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.addParameter(entry.getKey(), entry.getValue());
        }
        return this.doGet(builder.build().toString());
    }

    /**
     * 执行post请求
     * 
     * @param url
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public String doPost(String url, Map<String, String> params) throws ClientProtocolException, IOException {
        // 创建http GET请求
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(this.requestConfig);// 设置请求参数
        // 设置请求参数
        if (null != params) {
            List<NameValuePair> list = new ArrayList<NameValuePair>(params.size());
            for (Map.Entry<String, String> entry : params.entrySet()) {
                list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(list);
            // 添加到httpPost中
            httpPost.setEntity(urlEncodedFormEntity);
        }
        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = getHttpClient().execute(httpPost);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return null;
    }
    
    
    /**
     * 执行post请求
     * 
     * @param url
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public String doPostJson(String url, String json) throws ClientProtocolException, IOException {
        // 创建http GET请求
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(this.requestConfig);// 设置请求参数
        // 设置请求参数
        if (null != json) {
            StringEntity stringEntity = new StringEntity(json, ContentType.create(ContentType.APPLICATION_JSON.getMimeType(), "UTF-8"));
            // 添加到httpPost中
            httpPost.setEntity(stringEntity);
        }
        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = getHttpClient().execute(httpPost);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return null;
    }

    /**
     * 执行post请求
     * 
     * @param url
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public String doPost(String url) throws ClientProtocolException, IOException {
        return this.doPost(url, null);
    }

}
