package com.roilago.resourcepollingchecker.service;

import com.roilago.resourcepollingchecker.client.ResourceClient;
import com.roilago.resourcepollingchecker.client.model.ResourceClientModel;
import com.roilago.resourcepollingchecker.service.model.ResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ResourceComparatorService {

    private List<String> storedIdList = new ArrayList<>();

    private final ResourceClient client;

    @Autowired
    public ResourceComparatorService(ResourceClient client) {
        this.client = client;
    }

    public Optional<ResponseModel> pollResources() {
        List<ResourceClientModel> resourceData = client.getResourceData();

        if (CollectionUtils.isEmpty(resourceData)) {
            log.error("Data endpoint returned no response");
            return Optional.empty();
        }

        List<String> returnedIds = resourceData
                .stream()
                .map(ResourceClientModel::getId)
                .collect(Collectors.toList());

        List<String> vehiclesNotAvailable = new ArrayList<>(storedIdList);
        vehiclesNotAvailable.removeAll(returnedIds);

        List<String> vehiclesNowAvailable = new ArrayList<>(returnedIds);
        vehiclesNowAvailable.removeAll(storedIdList);

        log.info("Vehicles that are not available since the previous polling={}", vehiclesNotAvailable);
        log.info("Newly available vehicles={}", vehiclesNowAvailable);

        storedIdList = returnedIds;

        return Optional.of(new ResponseModel(vehiclesNowAvailable, vehiclesNotAvailable));
    }

}
