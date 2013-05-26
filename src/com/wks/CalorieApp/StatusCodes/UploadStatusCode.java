package com.wks.CalorieApp.StatusCodes;

public enum UploadStatusCode {
	UPLOAD_SUCCESSFUL("File uploaded successfully."),
	UPLOAD_FAILED("Upload failed."),
	NO_FILE_PROVIDED("No file to uplaod."),
	FILE_NOT_FOUND("Uploaded content does not contain a file."),
	INVALID_TYPE("File type invalid");
	
	private final String description;
	
	private UploadStatusCode(String description)
	{
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	@Override
	public String toString() {
		return this.getDescription();
	}
	
}
