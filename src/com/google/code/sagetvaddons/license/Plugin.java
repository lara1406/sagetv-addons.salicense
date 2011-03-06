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

import org.apache.log4j.PropertyConfigurator;

import sage.SageTVPlugin;
import sage.SageTVPluginRegistry;
import sagex.api.Utility;
import sagex.plugin.AbstractPlugin;
import sagex.plugin.IPropertyValidator;
import sagex.plugin.PluginProperty;
import sagex.plugin.ServerPropertyPersistence;

/**
 * @author dbattams
 *
 */
public final class Plugin extends AbstractPlugin {
	static final String PLUGIN_ID = "salicense";
	static final String PROP_EMAIL = "sagetvaddons/license/email";
	static final String PROP_FILE = "sagetvaddons/licesne/file";
	
	static {
		PropertyConfigurator.configure("plugins/salicense/salicense.log4j.properties");
	}

	static private class LicenseFileValidator implements IPropertyValidator {

		@Override
		public void validate(String prop, String val) throws Exception {
			if(prop.equals(PROP_FILE) && !Utility.IsFilePath(val))
				throw new IllegalArgumentException("File '" + val + "' does not exist on the server!");
		}		
	}
	
	public Plugin(SageTVPluginRegistry registry) {
		super(registry);
		PluginProperty prop = new PluginProperty(SageTVPlugin.CONFIG_TEXT, PROP_EMAIL, "", "Registered Email Address", "The email address associated with your sagetv-addons license file.  Changes to this value are immediate.");
		prop.setPersistence(new ServerPropertyPersistence());
		addProperty(prop);
		prop = new PluginProperty(SageTVPlugin.CONFIG_FILE, PROP_FILE, "", "Location of License File", "The location of your sagetv-addons license file.  The file path is on your SageTV server.  You probably DO NOT want to change this from a SageClient installation.  Changes to this value are immediate.");
		prop.setPersistence(new ServerPropertyPersistence());
		prop.setValidator(new LicenseFileValidator());
		addProperty(prop);
	}
}
