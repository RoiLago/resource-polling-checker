package com.roilago.resourcepollingchecker.client;

import com.roilago.resourcepollingchecker.client.model.ResourceClientModel;
import com.roilago.resourcepollingchecker.config.ResourceClientConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class ResourceClient {

    private static final String SERVICE_NAME = "resource-data-client";

    private final RestTemplate restTemplate;
    private final ResourceClientConfiguration clientConfiguration;

    @Autowired
    public ResourceClient(RestTemplateBuilder restTemplateBuilder,
                          ResourceClientConfiguration clientConfiguration) {
        this.restTemplate = restTemplateBuilder.build();
        this.clientConfiguration = clientConfiguration;
    }

    public List<ResourceClientModel> getResourceData() {
        try {
            ResponseEntity<List<ResourceClientModel>> response = restTemplate.exchange(
                    clientConfiguration.getUrl(), HttpMethod.GET, HttpEntity.EMPTY, new ParameterizedTypeReference<List<ResourceClientModel>>(){});

            log.debug("Returned response from data resource endpoint={}", response);
            List<ResourceClientModel> responseBody = response.getBody();
            if (CollectionUtils.isEmpty(responseBody)) {
                log.warn("Response is null or empty. Response={}", responseBody);
                return Collections.emptyList();
            }
            return responseBody;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Error in client={}: [Status code={}, response={}]", SERVICE_NAME, e.getStatusCode(), e.getResponseBodyAsString());
            return Collections.emptyList();
        } catch (RestClientException e) {
            log.error("I/O error calling client with name={} and url={}",
                    SERVICE_NAME, clientConfiguration.getUrl());
            return Collections.emptyList();
        }
    }
}
