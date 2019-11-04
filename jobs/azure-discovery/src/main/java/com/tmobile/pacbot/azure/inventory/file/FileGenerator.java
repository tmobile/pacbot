/*******************************************************************************
 * Copyright 2018 T Mobile, Inc. or its affiliates. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.tmobile.pacbot.azure.inventory.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.tmobile.pacbot.azure.inventory.vo.AzureVH;

/**
 * The Class FileGenerator.
 */
public class FileGenerator {
    
    /**
     * Instantiates a new file generator.
     */
    private FileGenerator() {
        
    }
	
	/** The folder name. */
	protected static String folderName ;
	
	/** The Constant DELIMITER. */
	public static final String DELIMITER ="`";
	
	/** The Constant LINESEPARATOR. */
	public static final String LINESEPARATOR ="\n";
	
	public static final String COMMA =",";	
	
	/** The current date. */
	protected static String discoveryDate =  new SimpleDateFormat("yyyy-MM-dd HH:00:00Z").format(new java.util.Date());
	
	/** The log. */
	private static Logger log = LoggerFactory.getLogger(FileGenerator.class);

	/**
	 * Write to file.
	 *
	 * @param filename the filename
	 * @param data the data
	 * @param appendto the appendto
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void  writeToFile(String filename ,String data,boolean appendto) throws IOException{
		log.debug("Write to File :"+filename );
		BufferedWriter bw = null ;
		try {
		    bw = new BufferedWriter(new FileWriter(folderName+File.separator+filename,appendto));
			bw.write(data);
			bw.flush();
			bw.close();
		} catch (IOException e) {
			log.error("Write to File :{} failed",filename,e);
			throw e;
		}
		finally {
		    if(bw != null) {
		        bw.close();
		    }
		}
	}
	
	
	/**
	 * Gets the line data.
	 *
	 * @param fieledNames the fieled names
	 * @param obj the obj
	 * @return the line data
	 */

	
	protected static  boolean  generateJson(List<?extends AzureVH> assetList,String fileName ){
		
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		StringBuilder sb = new StringBuilder();
	
		for(AzureVH asset : assetList) {	
			asset.setDiscoverydate(discoveryDate);
			try {
				if(sb.length() == 0 && new File(folderName+File.separator+fileName).length() < 2) {
					sb.append(objectMapper.writeValueAsString(asset));
				} else {
					sb.append(COMMA+LINESEPARATOR+objectMapper.writeValueAsString(asset));
				}
	        } catch (Exception e) {
	        	log.error("Error in generateJson ",e);
				return false;
	        }
		}
		
		try {
			writeToFile(fileName, sb.toString(), true);
		} catch (IOException e) {
			log.error("Error in generateJson ",e);
			return false;
		}
		return true;
	}
	
}
