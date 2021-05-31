package com.roilago.resourcepollingchecker.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@EnableScheduling
public class PollingService {

    private final ResourceComparatorService comparatorService;

    @Autowired
    public PollingService(ResourceComparatorService comparatorService) {
        this.comparatorService = comparatorService;
    }

    @Scheduled(cron = "${polling-service.cron}")
    public void pollResources() {
        comparatorService.pollResources();
    }

}
