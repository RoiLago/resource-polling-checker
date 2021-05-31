package com.roilago.resourcepollingchecker.service;

import com.roilago.resourcepollingchecker.client.ResourceClient;
import com.roilago.resourcepollingchecker.client.model.ResourceClientModel;
import com.roilago.resourcepollingchecker.service.model.ResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ResourceComparatorServiceTest {

    private static final String ID_1 = "ID1";
    private static final String ID_2 = "ID2";
    private static final String ID_3 = "ID3";

    @Mock
    private ResourceClient client;

    private ResourceComparatorService pollingService;

    @BeforeEach
    void setUp() {
        pollingService = new ResourceComparatorService(client);
    }

    @Test
    void whenInitialListIsEmptyOnlyNewVehiclesAreLogged() {
        given(client.getResourceData())
                .willReturn(Arrays.asList(buildClientModel(ID_1), buildClientModel(ID_2)));

        Optional<ResponseModel> responseModel = pollingService.pollResources();

        assertThat(responseModel).isPresent();
        assertThat(responseModel.get().getRemovedVehicles()).isEmpty();
        assertThat(responseModel.get().getNewVehicles()).containsOnly(ID_1, ID_2);
    }

    @Test
    void whenOneIsAddedAndOneIsRemovedTheyAreOnTheirRespectiveList() {
        given(client.getResourceData())
                .willReturn(Arrays.asList(buildClientModel(ID_1), buildClientModel(ID_2)))
                .willReturn(Arrays.asList(buildClientModel(ID_1), buildClientModel(ID_3)));

        pollingService.pollResources();
        Optional<ResponseModel> responseModel = pollingService.pollResources();

        assertThat(responseModel).isPresent();
        assertThat(responseModel.get().getRemovedVehicles()).containsOnly(ID_2);
        assertThat(responseModel.get().getNewVehicles()).containsOnly(ID_3);
    }

    @Test
    void whenTheRequestToTheClientFailsTheCacheIsNotEmptied() {
        given(client.getResourceData())
                .willReturn(Arrays.asList(buildClientModel(ID_1), buildClientModel(ID_2)))
                .willReturn(Collections.emptyList())
                .willReturn(Arrays.asList(buildClientModel(ID_1), buildClientModel(ID_3)));

        pollingService.pollResources();
        Optional<ResponseModel> emptyResponse = pollingService.pollResources();
        assertThat(emptyResponse).isEmpty();

        Optional<ResponseModel> responseModel = pollingService.pollResources();

        assertThat(responseModel).isPresent();
        assertThat(responseModel.get().getRemovedVehicles()).containsOnly(ID_2);
        assertThat(responseModel.get().getNewVehicles()).containsOnly(ID_3);
    }

    private ResourceClientModel buildClientModel(String id) {
        ResourceClientModel clientModel = new ResourceClientModel();
        clientModel.setId(id);
        return clientModel;
    }

}