package net.leadingsoft.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class NotificationController {

  @Autowired
  RestTemplate restTemplate;

  @Autowired
  ObjectMapper objectMapper;

  @Value("${slack.url}")
  String slackUrl;

  @RequestMapping(value = {"", "/", "/{topic}", "/{topic}/{queue}"} )
  public ResponseEntity<Void> callback(HttpServletRequest request, @RequestBody(required = false) String payload) throws Exception {
    Map<String, String> bodyMap = new HashMap<>();
    bodyMap.put("channel", "#callback");
    bodyMap.put("username", "callback");
    bodyMap.put("text", "```" + requestToString(request) + "\n" + payload + "```");

    MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
    param.add("payload", objectMapper.writeValueAsString(bodyMap));

    RequestEntity requestEntity = RequestEntity.post(URI.create(slackUrl))
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .accept(MediaType.APPLICATION_FORM_URLENCODED)
        .body(param);
    restTemplate.exchange(requestEntity, String.class).getBody();

    return new ResponseEntity<>(HttpStatus.OK);
  }

  private String requestToString(HttpServletRequest request) {
    StringBuilder sb = new StringBuilder();
    sb.append("Request Method = [" + request.getMethod() + "]").append("\n");

    String headers =
        Collections.list(request.getHeaderNames()).stream()
            .map(headerName -> headerName + " : " + Collections.list(request.getHeaders(headerName)))
            .collect(Collectors.joining("\n"));
    if (!headers.isEmpty()) {
      sb.append("Request headers: [" + headers + "]").append("\n");
    }

    String parameters =
        Collections.list(request.getParameterNames()).stream()
            .map(p -> p + " : " + Arrays.asList(request.getParameterValues(p)))
            .collect(Collectors.joining("\n"));
    if (!parameters.isEmpty()) {
      sb.append("Request parameters: [" + parameters + "]").append("\n");
    }

    return sb.toString();
  }

}
