package com.wks.calorieapp.factories;

import net.semanticmetadata.lire.DocumentBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

public class DocumentBuilderFactory {

    @Produces
    @ApplicationScoped
    public DocumentBuilder getDocumentBuilder(){
        return net.semanticmetadata.lire.DocumentBuilderFactory.getAutoColorCorrelogramDocumentBuilder();
    }
}
