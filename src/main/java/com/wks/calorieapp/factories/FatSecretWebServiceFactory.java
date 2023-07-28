package com.wks.calorieapp.factories;

import com.wks.calorieapp.api.fatsecret.FatSecretWebService;

import javax.annotation.Resource;
import javax.enterprise.inject.Produces;

public class FatSecretWebServiceFactory {

    @Resource(name = "fat-secret/consumer-key")
    private String consumerKey;
    @Resource(name = "fat-secret/consumer-secret")
    private String consumerSecret;

    @Produces
    public FatSecretWebService getFSWebService() {
        return new FatSecretWebService(consumerKey, consumerSecret);
    }
}
