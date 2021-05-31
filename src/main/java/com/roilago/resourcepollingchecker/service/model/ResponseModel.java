package com.roilago.resourcepollingchecker.service.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@ToString
@EqualsAndHashCode
public class ResponseModel {

    private List<String> newVehicles;
    private List<String> removedVehicles;

    public ResponseModel(List<String> newVehicles, List<String> removedVehicles) {
        this.newVehicles = newVehicles;
        this.removedVehicles = removedVehicles;
    }

    public List<String> getNewVehicles() {
        return newVehicles;
    }

    public void setNewVehicles(List<String> newVehicles) {
        this.newVehicles = newVehicles;
    }

    public List<String> getRemovedVehicles() {
        return removedVehicles;
    }

    public void setRemovedVehicles(List<String> removedVehicles) {
        this.removedVehicles = removedVehicles;
    }
}
