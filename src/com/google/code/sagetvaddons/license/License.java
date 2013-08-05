/*
 *      Copyright 2011-2013 Battams, Derek
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
public final class License {
	static private final LicenseResponse FREE_LICENSE = new License().isLicensed();

	static public LicenseResponse isLicensed(String pluginId) {
		return FREE_LICENSE;
	}

	static public void autoConfig(String email, String filePath) {}
	
	final private LicenseResponse resp;

	private License() {
		resp = new LicenseResponse();
	}

	public LicenseResponse isLicensed() {
		resp.setLicensed(true);
		resp.setMessage("OK");
		return resp;
	}
}
