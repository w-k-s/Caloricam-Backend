package com.wks.CalorieApp.StatusCodes;

public enum IdentifyStatusCodes {
    TOO_FEW_ARGS("Insufficient parameters provided.Service: identify/{FileName}/{MaxHits(optional)}"),
    INVALID_MAX_HITS("Invalid value provided for maximum number of hits. Value must be an integer."), 
    IO_ERROR("Error reading files while retrieving similar images"),
    FILE_NOT_FOUND("Search image not found on server. The file ma have failed to upload or may have been deleted.");

    
    private final String description;
    
    private IdentifyStatusCodes(String description)
    {
	this.description = description;
    }
    
    public String getDescription() {
	return description;
    }
}
