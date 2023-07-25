package com.wks.calorieapp.api.fatsecret.entities;

/** CITATION
 * 
 * @author FatSecret 
 * FatSecret, FatSecret Platform API Java, http://platform.fatsecret.com/api/static/libraries/java.tar.gz
 *
 *Modified to add getters and setters for Nonce, Timestamp and URL.
 *
 */
public class Result
{
	private String _signature, _signatureBase, _normalizedUrl, _normalizedRequestParameters, _nonce, _timestamp;

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
	    return _nonce;
	}

	public String getTimestamp() {
	    return _timestamp;
	}

	public void setNonce(String _nonce) {
	    this._nonce = _nonce;
	}

	public void setTimestamp(String _timestamp) {
	    this._timestamp = _timestamp;
	}

	public String getURL() {
	    return getNormalizedUrl() + "?" + getNormalizedRequestParameters() + "&oauth_signature=" + getSignature();
	}

}