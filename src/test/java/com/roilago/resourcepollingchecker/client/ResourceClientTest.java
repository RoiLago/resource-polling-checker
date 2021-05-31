package com.roilago.resourcepollingchecker.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roilago.resourcepollingchecker.client.model.ResourceClientModel;
import com.roilago.resourcepollingchecker.config.ResourceClientConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ResourceClientTest {

    private static final String URL = "base-url";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private RestTemplateBuilder templateBuilder;

    @Mock
    private RestTemplate restTemplate;

    private ResourceClient client;

    @BeforeEach
    void setUp() {
        ResourceClientConfiguration configuration = new ResourceClientConfiguration();
        configuration.setUrl(URL);
        given(templateBuilder.build()).willReturn(restTemplate);

        client = new ResourceClient(templateBuilder, configuration);
    }

    @Test
    void whenRequestIsSuccessfulResponseIsExpected() throws IOException {
        ClassPathResource res = new ClassPathResource("response.json");
        List<ResourceClientModel> clientModel = objectMapper.readValue(res.getFile(), new TypeReference<List<ResourceClientModel>>() {});

        given(restTemplate.exchange(URL, HttpMethod.GET, HttpEntity.EMPTY, new ParameterizedTypeReference<List<ResourceClientModel>>(){}))
                .willReturn(new ResponseEntity<>(clientModel, HttpStatus.OK));

        List<ResourceClientModel> response = client.getResourceData();
        assertThat(response).hasSize(2);
        assertThat(response.get(0).getId()).isEqualTo("PT-LIS-A01");
        assertThat(response.get(0).getName()).isEqualTo("1VG10");
        assertThat(response.get(0).getX()).isEqualTo(0.123456);
        assertThat(response.get(0).getY()).isEqualTo(1.123456);
        assertThat(response.get(0).getLicencePlate()).isEqualTo("AAAAAA");
        assertThat(response.get(0).getRange()).isEqualTo(29);
        assertThat(response.get(0).getBatteryLevel()).isEqualTo(100);
        assertThat(response.get(0).getHelmets()).isEqualTo(2);
        assertThat(response.get(0).getModel()).isEqualTo("Bike");
        assertThat(response.get(0).getResourceImageId()).isEqualTo("image_id");
        assertThat(response.get(0).getRealTimeData()).isTrue();
        assertThat(response.get(0).getResourceType()).isEqualTo("MOPED");
        assertThat(response.get(0).getCompanyZoneId()).isEqualTo(1);
    }

    @Test
    void whenRequestReturnsClientExceptionErrorIsThrown() {
        given(restTemplate.exchange(URL, HttpMethod.GET, HttpEntity.EMPTY, new ParameterizedTypeReference<List<ResourceClientModel>>(){}))
                .willThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        List<ResourceClientModel> response = client.getResourceData();
        assertThat(response).isEmpty();
    }

    @Test
    void whenRequestReturnsServerExceptionErrorIsThrown() {
        given(restTemplate.exchange(URL, HttpMethod.GET, HttpEntity.EMPTY, new ParameterizedTypeReference<List<ResourceClientModel>>(){}))
                .willThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        List<ResourceClientModel> response = client.getResourceData();
        assertThat(response).isEmpty();
    }

    @Test
    void whenRequestReturnsRestExceptionErrorIsThrown() {
        given(restTemplate.exchange(URL, HttpMethod.GET, HttpEntity.EMPTY, new ParameterizedTypeReference<List<ResourceClientModel>>(){}))
                .willThrow(new RestClientException("error"));

        List<ResourceClientModel> response = client.getResourceData();
        assertThat(response).isEmpty();
    }

    @Test
    void whenResponseIsEmptyEmptyListIsReturned() {
        given(restTemplate.exchange(URL, HttpMethod.GET, HttpEntity.EMPTY, new ParameterizedTypeReference<List<ResourceClientModel>>(){}))
                .willReturn(new ResponseEntity<>(null, HttpStatus.OK));

        List<ResourceClientModel> response = client.getResourceData();
        assertThat(response).isEmpty();
    }

}