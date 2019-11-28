//****************************************************************************//
// システム         : Lockey
//----------------------------------------------------------------------------//
//                (c)Copyright 2018 SoftBank All rights reserved.
//============================================================================//
package net.leadingsoft.rest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

/**
 * <pre>
 *
 * MVCに関す共通設定
 *
 * </pre>
 */
@Configuration
public class WebMvcConfigurer implements org.springframework.web.servlet.config.annotation.WebMvcConfigurer {

  /**
   * RestTemplate DI 設定
   *
   * @return RestTemplate
   */
  @Bean
  public RestTemplate getRestClient() {
    HttpComponentsClientHttpRequestFactory requestFactory = //
        new HttpComponentsClientHttpRequestFactory();
    // 接続時Timeout時間設定(20秒)
    requestFactory.setConnectTimeout(20000);
    requestFactory.setReadTimeout(20000);
    RestTemplate restClient = //
        new RestTemplate(new BufferingClientHttpRequestFactory(requestFactory));

    restClient.setErrorHandler(new DefaultResponseErrorHandler() {
      /**
       * エラーかどうか
       */
      @Override
      public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is5xxServerError();
      }

      /**
       * Exceptionスローしない
       */
      @Override
      public void handleError(ClientHttpResponse response) throws IOException {
        // 処理しない
      }
    });

    return restClient;
  }

}
