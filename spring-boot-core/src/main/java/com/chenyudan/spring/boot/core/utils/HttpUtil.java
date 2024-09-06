package com.chenyudan.spring.boot.core.utils;

import com.chenyudan.spring.boot.core.error.BaseError;
import com.chenyudan.spring.boot.domain.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.List;
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

    private static final OkHttpClient httpClient = new OkHttpClient().newBuilder().connectTimeout(10L, TimeUnit.SECONDS).callTimeout(30L, TimeUnit.SECONDS).build();

    public static String get(String url, Map<String, Object> params) {
        return get(url, null, params);
    }

    public static String postForJson(String url, Map<String, Object> params, String body) {
        return postForJson(url, null, params, body);
    }

    public static String get(String url, Map<String, String> headers, Map<String, Object> params) {
        String actualUrl = buildUrl(url, params);
        Request.Builder builder = new Request.Builder().url(actualUrl).get();
        if (headers != null) {
            headers.forEach(builder::addHeader);
        }
        return getResult(builder);
    }

    public static String postForJson(String url, Map<String, String> headers, Map<String, Object> params, String body) {
        String actualUrl = buildUrl(url, params);
        MediaType mediaType = MediaType.get("application/json");
        RequestBody requestBody = RequestBody.create(mediaType, body);
        Request.Builder builder = new Request.Builder().url(actualUrl).post(requestBody);
        if (headers != null) {
            headers.forEach(builder::addHeader);
        }
        return getResult(builder);
    }

    private static String getResult(Builder builder) {
        try (Response response = httpClient.newCall(builder.build()).execute()) {
            AssertUtil.isTrue(response.isSuccessful(), BaseError.CONNECT_ERROR);
            ResponseBody responseBody = response.body();
            return responseBody == null ? null : responseBody.string();
        } catch (IOException e) {
            throw new BaseException(BaseError.CONNECT_ERROR);
        }
    }

    /**
     * 构建完整请求地址
     *
     * @param url    地址
     * @param params 参数
     */
    public static String buildUrl(String url, Map<String, Object> params) {
        String actualUrl = url;
        if (params != null && !params.isEmpty()) {
            List<String> paramList = LambdaUtil.mapToList(params, entry -> entry.getKey() + "=" + entry.getValue());
            String paramsStr = StringUtil.join(paramList, "&");
            actualUrl = actualUrl + "?" + paramsStr;
        }
        log.debug("http请求地址:{}", actualUrl);
        return actualUrl;
    }
}
