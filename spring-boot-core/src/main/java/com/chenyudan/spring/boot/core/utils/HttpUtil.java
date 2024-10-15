package com.chenyudan.spring.boot.core.utils;

import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Description: TODO
 *
 * @author chenyu
 * @since 2023/5/26 13:19
 */
@Slf4j
public class HttpUtil {

    private static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json; charset=utf-8";

    private final OkHttpClient okHttpClient;

    HttpUtil(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    public static HttpUtil create(Long connectTimeout, Long callTimeout) {
        OkHttpClient httpClient = new OkHttpClient().newBuilder().connectTimeout(connectTimeout, TimeUnit.SECONDS).callTimeout(callTimeout, TimeUnit.SECONDS).build();
        return new HttpUtil(httpClient);
    }

    public Response get(String url) throws IOException {
        return this.get(url, new HashMap<>());
    }

    public Response get(String url, Map<String, String> headerMap) throws IOException {
        Headers headers = Headers.of(headerMap);
        Request request = new Builder().url(url).get().headers(headers).build();
        return okHttpClient.newCall(request).execute();
    }

    public Response postForJson(String url, Object body) throws IOException {
        return this.postForJson(url, body, new HashMap<>());
    }

    public Response postForJson(String url, Object body, Map<String, String> headerMap) throws IOException {
        Headers headers = Headers.of(headerMap);
        RequestBody requestBody = RequestBody.create(MediaType.get(APPLICATION_JSON_CHARSET_UTF_8), new GsonBuilder().create().toJson(body));
        Request request = new Request.Builder().url(url).post(requestBody).headers(headers).build();
        return okHttpClient.newCall(request).execute();
    }
}
