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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.StringReader;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Properties;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import sagex.api.Configuration;
import sagex.api.PluginAPI;
import sagex.api.Utility;

/**
 * @author dbattams
 *
 */
public final class License {
	static private final Logger LOG = Logger.getLogger(License.class);

	static public LicenseResponse isLicensed(String requestor) {
		LOG.info("Received request from '" + requestor + "'");
		return new License(requestor).isLicensed();
	}

	static public void autoConfig(String email, String filePath) {
		if(email != null && email.length() > 0 && "".equals(Configuration.GetServerProperty(Plugin.PROP_EMAIL, "")))
			Configuration.SetServerProperty(Plugin.PROP_EMAIL, email);
		if(filePath != null && filePath.length() > 0 && "".equals(Configuration.GetServerProperty(Plugin.PROP_FILE, "")))
			Configuration.SetServerProperty(Plugin.PROP_FILE, filePath);
	}
	
	static private boolean isPluginEnabled() {
		for(Object plugin : PluginAPI.GetInstalledPlugins()) {
			if(PluginAPI.GetPluginIdentifier(plugin).equals(Plugin.PLUGIN_ID))
				return PluginAPI.IsPluginEnabled(plugin);
		}
		return false;
	}

	private PublicKey key;
	private LicenseResponse resp;
	private String registeredEmail, requestor, filePath;
	private File licenseFile;

	private License(String requestor) {
		this.requestor = requestor;
		filePath = Configuration.GetServerProperty(Plugin.PROP_FILE, "");
		licenseFile = new File(filePath);
		registeredEmail = Configuration.GetServerProperty(Plugin.PROP_EMAIL, "");
		resp = new LicenseResponse();
	}

	public LicenseResponse isLicensed() {
		if(!isPluginEnabled()) {
			String err = "sagetev-addons license plugin is not enabled!  Enable the plugin.";
			LOG.warn(requestor + ": " + err);
			resp.setLicensed(false);
			resp.setMessage(err);
		} else if(filePath.length() == 0) {
			String err = "No license file specified!  Please configure the sagetv-addons license plugin.";
			LOG.warn(requestor + ": " + err);
			resp.setLicensed(false);
			resp.setMessage(err);
		} else if(!Utility.IsFilePath(licenseFile.getAbsolutePath())) {
			String err = "Cannot read specified license file! [" + licenseFile.getAbsolutePath() + "]";
			LOG.warn(requestor + ": " + err);
			resp.setLicensed(false);
			resp.setMessage(err);
		} else if(registeredEmail.length() == 0) {
			String err = "Registered email not specified!  Please configure the sagetv-addons license plugin.";
			resp.setLicensed(false);
			resp.setMessage(err);
		} else {
			try {
				if(!registeredEmail.equals(getLicensedEmail())) {
					String err = "The registered email ['" + registeredEmail + "'] does not match the email in the license file!";
					LOG.warn(requestor + ": " + err);
					resp.setLicensed(false);
					resp.setMessage(err);
				} else {
					resp.setLicensed(true);
					resp.setMessage("OK");
					LOG.info(requestor + ": License verified successfully!");
				}
			} catch(Exception e) {
				String err = "Error while processing license request: " + e.getMessage();
				LOG.warn(requestor + ": Failed", e);
				resp.setLicensed(false);
				resp.setMessage(err);
			}
		}
		return resp;
	}

	private String getLicensedEmail() throws Exception {
		String data = Utility.GetFileAsString(licenseFile);
		if(data == null || data.length() == 0)
			return null;
		initKey();
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, key);
		Charset utf8 = Charset.forName("UTF-8");
		String propsData = new String(cipher.doFinal(Base64.decodeBase64(data.getBytes(utf8))), utf8);
		Properties props = new Properties();
		props.load(new StringReader(propsData));
		return props.getProperty("email");
	}

	private void initKey() throws Exception {
		InputStream publicKey = License.class.getResourceAsStream("/com/google/code/sagetvaddons/license/sagetv-addons.pub");
		if(publicKey != null) {
			ObjectInputStream oin = null;
			BigInteger mod, exp;
			try {
				oin = new ObjectInputStream(new BufferedInputStream(publicKey));
				mod = (BigInteger) oin.readObject();
				exp = (BigInteger) oin.readObject();
				RSAPublicKeySpec keySpec = new RSAPublicKeySpec(mod, exp);
				KeyFactory fact = KeyFactory.getInstance("RSA");
				key = fact.generatePublic(keySpec);
			} finally {
				if(oin != null)
					try { oin.close(); } catch(IOException e) { LOG.error("IOError", e); }
			}
		} else
			throw new RuntimeException("Unable to find public key file!");
	}
}
