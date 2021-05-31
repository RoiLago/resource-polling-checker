package com.roilago.resourcepollingchecker.client.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@ToString
@EqualsAndHashCode
public class ResourceClientListModel {

    private List<ResourceClientModel> resourceList;

    public ResourceClientListModel(List<ResourceClientModel> resourceList) {
        this.resourceList = resourceList;
    }

    public List<ResourceClientModel> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<ResourceClientModel> resourceList) {
        this.resourceList = resourceList;
    }
}
