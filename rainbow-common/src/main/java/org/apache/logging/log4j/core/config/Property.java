/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */
package org.apache.logging.log4j.core.config;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.PluginValue;
import org.apache.logging.log4j.status.StatusLogger;

/**
 * Represents a key/value pair in the configuration.
 */
@Plugin(name = "property", category = "Core", printObject = true)
public final class Property {
	private static Pattern pat = Pattern.compile("[${][^$]*}");   
    private static final Logger LOGGER = StatusLogger.getLogger();

    private final String name;
    private final String value;

    private Property(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Returns the property name.
     * @return the property name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the property value.
     * @return the value of the property.
     */
    public String getValue() {
        return value;
    }

    /**
     * Create a Property.
     * @param key The key.
     * @param value The value.
     * @return A Property.
     */
    @PluginFactory
    public static Property createProperty(
            @PluginAttribute("name") final String key,
            @PluginValue("value")final String value) {
        if (key == null) {
            LOGGER.error("Property key cannot be null");
        }
        
        if(value.contains("${")){
        	List<String> keys = new ArrayList<String>();
        	List<String> values = new ArrayList<String>();
        	
        	String _value = value;
    		Matcher m = pat.matcher(_value);		
    		while(m.find()){		
    			String _key = m.group();
    			keys.add(_key);
    			values.add(System.getProperty(_key.substring(2, _key.lastIndexOf('}'))));
    		}
    		for(int i = 0; i < keys.size(); i++){
    			_value = _value.replaceAll("[${][^$]["+keys.get(i)+"]*}", values.get(i));
    		}
        	return new Property(key, _value);
        }else{
        	return new Property(key, value);
        }
    }

    @Override
    public String toString() {
        return name + "=" + value;
    }
}
