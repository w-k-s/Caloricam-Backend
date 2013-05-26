package com.wks.CalorieApp.StatusCodes;

public enum IndexStatusCodes {
	INDEXING_SUCCESSFUL("File Indexed Successfully."),
	TOO_FEW_ARGS("Insufficient parameters provided. File name missing."),
	IO_ERROR(""),
	INDEX_ERROR(""),
	IO_INDEX_ERROR("Error occured while reading or indexing image"),
	FILE_NOT_FOUND("Indexing failed because file not found.");
	
	private final String description;
	
	private IndexStatusCodes(String description)
	{
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
}
