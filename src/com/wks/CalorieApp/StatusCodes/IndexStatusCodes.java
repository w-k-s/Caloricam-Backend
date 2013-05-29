package com.wks.CalorieApp.StatusCodes;

public enum IndexStatusCodes {
	INDEXING_SUCCESSFUL("File Indexed Successfully."),
	TOO_FEW_ARGS("Insufficient parameters provided.Service: index/{FileName}"),
	IO_ERROR(""),
	INDEX_ERROR(""),
	IO_INDEX_ERROR("Error occured while reading or indexing image"),
	FILE_NOT_FOUND("Indexing failed because file not found."),
	DB_INTEGRITY_VIOLATION("Database Integrity Violation."),
	DB_INSERT_FAILED("Image could not be added to database.");
	
	private final String description;
	
	private IndexStatusCodes(String description)
	{
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
}
