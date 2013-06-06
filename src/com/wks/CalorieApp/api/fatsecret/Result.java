package com.wks.CalorieApp.api.fatsecret;

public class Result
{
	private String _signature, _signatureBase, _normalizedUrl, _normalizedRequestParameters, nonce, timestamp;

	public Result()
	{
	}

	public void setSignature(String _signature) {
	    this._signature = _signature;
	}

	public String getSignature() {
	    return _signature;
	}

	public void setSignatureBase(String _signatureBase) {
	    this._signatureBase = _signatureBase;
	}

	public String getSignatureBase() {
	    return _signatureBase;
	}

	public void setNormalizedUrl(String _normalizedUrl) {
	    this._normalizedUrl = _normalizedUrl;
	}

	public String getNormalizedUrl() {
	    return _normalizedUrl;
	}

	public void setNormalizedRequestParameters(String _normalizedRequestParameters) {
	    this._normalizedRequestParameters = _normalizedRequestParameters;
	}

	public String getNormalizedRequestParameters() {
	    return _normalizedRequestParameters;
	}

	public String getNonce() {
	    return nonce;
	}

	public String getTimestamp() {
	    return timestamp;
	}

	public void setNonce(String nonce) {
	    this.nonce = nonce;
	}

	public void setTimestamp(String timestamp) {
	    this.timestamp = timestamp;
	}

	public String getURL() {
	    return getNormalizedUrl() + "?" + getNormalizedRequestParameters() + "&oauth_signature=" + getSignature();
	}

}