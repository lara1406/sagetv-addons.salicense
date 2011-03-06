/*
 *      Copyright 2011 Battams, Derek
 *       
 *       Licensed under the Apache License, Version 2.0 (the "License");
 *       you may not use this file except in compliance with the License.
 *       You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *       Unless required by applicable law or agreed to in writing, software
 *       distributed under the License is distributed on an "AS IS" BASIS,
 *       WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *       See the License for the specific language governing permissions and
 *       limitations under the License.
 */
package com.google.code.sagetvaddons.license;

/**
 * @author dbattams
 *
 */
public final class LicenseResponse {

	private boolean isLicensed;
	private String message;
	/**
	 * @return the isLicensed
	 */
	public boolean isLicensed() {
		return isLicensed;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param isLicensed the isLicensed to set
	 */
	void setLicensed(boolean isLicensed) {
		this.isLicensed = isLicensed;
	}
	/**
	 * @param message the message to set
	 */
	void setMessage(String message) {
		this.message = message;
	}
}
