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
package com.tmobile.cso.pacman.inventory.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

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
	protected static String currentDate =  new SimpleDateFormat("yyyy-MM-dd HH:00:00Z").format(new java.util.Date());
	
	public static final String CLOUD_TYPE = "_cloudType";	
	public static final String AWS = "Aws";
	
	/** The log. */
	private static Logger log = LoggerFactory.getLogger(FileGenerator.class);
	
	/**
	 * Generate file.
	 *
	 * @param <T> the generic type
	 * @param <U> the generic type
	 * @param fileInfoMap the file info map
	 * @param fieldNames the field names
	 * @param fileName the file name
	 * @return true, if successful
	 */
	protected static <T,U> String generateFile( Map<U,List<T>> fileInfoMap,String fieldNames){	
		Iterator<Entry<U,List<T>>> it= fileInfoMap.entrySet().iterator();
		StringBuilder sb = new StringBuilder();
		while(it.hasNext()){
			Entry<U,List<T>> entry = it.next();
			List<T> fileInfoList = entry.getValue();
			if(fileInfoList != null) {
				for(T fileInfo : fileInfoList){
					String data = getLineData(fieldNames,fileInfo);
					if(data != null && !"".equals(data) && !"".equals(data.replaceAll(DELIMITER,""))){
						String[] dataList = data.split(LINESEPARATOR);
						for(String currentdata : dataList ){
							String[] arrData = currentdata.split(DELIMITER);
							long dataCount = Arrays.asList(arrData).stream().filter(s -> !"".equals(s)).count();
							if(dataCount > 1 || (dataCount == 1 && !fieldNames.contains(DELIMITER))){ // To avoid file where mapping data is empty I'e tags
								currentdata = "".equals(currentDate)?entry.getKey()+DELIMITER+currentdata:currentDate+DELIMITER+entry.getKey()+DELIMITER+currentdata;
								sb.append(currentdata);
								sb.append(LINESEPARATOR);
							}
						}
					}
				}
			}
		}
		return sb.toString();
	}
	
	/**
	 * Write to file.
	 *
	 * @param filename the filename
	 * @param data the data
	 * @param appendto the appendto
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected static void  writeToFile(String filename ,String data,boolean appendto) throws IOException{
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
	 * Gets the value.
	 *
	 * @param fieldName the field name
	 * @param obj the obj
	 * @return the value
	 */
	private static Object getValue(String fieldName, Object obj){
		Object value = null;
		Field field =   getField(obj.getClass(),fieldName);
		if(null != field) {
		    field.setAccessible(true);
		}
			
		try {
		    if(null != field) {
		        value = field.get(obj);
		    }
		} catch (IllegalArgumentException | IllegalAccessException e) {
			log.error("Error in getValue "+e);
		}
		return value;

	}
	
	/**
	 * Gets the line data.
	 *
	 * @param fieledNames the fieled names
	 * @param obj the obj
	 * @return the line data
	 */
	@SuppressWarnings("unchecked")
	protected static String getLineData(String fieledNames, Object obj){
		List<List<Object>> valueHolder = new ArrayList<>();
		String[] fieldNamesList = fieledNames.split("["+DELIMITER+"]+");
		
		for(String fieldName : fieldNamesList){
			String[] fieldNameList  = fieldName.split("[.]");
			if(fieldNameList.length == 1){
				Object object = getValue(fieldName,obj);
				if(object instanceof List ){
					String objString = ((List<Object>) object).stream().map(Object::toString).collect(Collectors.joining(":;"));
					addtoValueHolder(valueHolder,objString,0);
				}else{
					addtoValueHolder(valueHolder,object,0);
				}
			}else{
				String  endFn= fieldNameList[fieldNameList.length-1];
				List<Object> objList = new ArrayList<>(); 
				objList.add(obj);
				for(String fn : fieldNameList){
					int size = objList.size();
					if(size>0){
						for(int i=0;i<size;i++){
							Object object = objList.get(i);
							if(object!=null){
								Object objValue = getValue(fn,object);
								if(fn.equals(endFn)){
									if(objValue instanceof List){
										String objString = ((List<Object>) objValue).stream().map(Object::toString).collect(Collectors.joining(":;"));
										addtoValueHolder(valueHolder,objString,i);
									}else{
										addtoValueHolder(valueHolder,objValue,i);
									}
								}else{
									if(objValue instanceof List){
										objList.clear();
										addtoValueHolder(valueHolder,((List<Object>)objValue).size());
										for(Object _listObj : (List<Object>)objValue){
											objList.add(_listObj);
										}
									}else{
										objList.set(i,objValue);
									}
								}
							}else{
								addtoValueHolder(valueHolder,"",i);
							}
						}
					}else{
						addtoValueHolder(valueHolder,"",0);
					}
				}	
			}
		}
		List<String> returnVal = new ArrayList<>();
		
		for(List<Object> vh : valueHolder ){
			returnVal.add(vh.stream().
				       map(objx -> {  
				    	   			if(objx==null){
				    	   				return "";
				    	   			}else if(objx instanceof java.util.Date) {
				    	   				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
				    	   				return sdf.format(objx);
				    	   			}
				    	   			return massageData(objx.toString());
				       			  }
				       	).
				       collect(Collectors.joining(DELIMITER)));
		}
		
		return returnVal.stream().collect(Collectors.joining(LINESEPARATOR));
		
	}
	
	/**
	 * Addto value holder.
	 *
	 * @param valueHolder the value holder
	 * @param value the value
	 * @param index the index
	 */
	private static void addtoValueHolder(List<List<Object>> valueHolder, Object value,int index){
		if(valueHolder.isEmpty()){
			List<Object> vh = new ArrayList<>();
			valueHolder.add(vh);
		}
		valueHolder.get(index).add(value);
	}
	
	/**
	 * Addto value holder.
	 *
	 * @param valueHolder the value holder
	 * @param size the size
	 */
	private static void addtoValueHolder(List<List<Object>> valueHolder,int size){
		if(valueHolder.isEmpty()){
			List<Object> vh = new ArrayList<>();
			valueHolder.add(vh);
		}
		if(valueHolder.size() != size){
			for(int i=1 ;i<size;i++){
				List<Object> vhList = new ArrayList<>();
				vhList.addAll(valueHolder.get(0));
				valueHolder.add(vhList);
			}
		}
		
	}
	
	/**
	 * Gets the field.
	 *
	 * @param clazz the clazz
	 * @param name the name
	 * @return the field
	 */
	private static Field getField(Class<?> clazz, String name) {
	    Field field = null;
	    while (clazz != null) {
	        try {
	        	Field[] fields = clazz.getDeclaredFields();
	        	for(Field fieldTemp : fields){
	        		if (fieldTemp .getName().equalsIgnoreCase(name)) {
	        		    field  = fieldTemp ;
	        			break;
					}
	        	}
	            
	        } catch (Exception e) {
	        	log.error("Error getting value for {}",name);
	        }
	        clazz = clazz.getSuperclass();
	    }
	    return field;
	}
	
	/**
	 * Massage data.
	 *
	 * @param str the str
	 * @return the string
	 */
	private static String massageData(String str){
		String temp = str;
		temp = temp.replace(DELIMITER, "'");
		temp = temp.replace("\r\n", "[NL]");
		temp = temp.replace("\n", "[NL]");
		return temp;
	}
	
	/**
	 * Gets the folder name.
	 *
	 * @return the folder name
	 */
	public static String getFolderName(){
		return folderName;
	}
	
	protected static <T,U> boolean generateJson( Map<U,List<T>> fileInfoMap,String fieldNames,String fileName, String keys){
		
		ObjectMapper objectMapper = new ObjectMapper();
		StringBuilder sb = new StringBuilder();
		String[] keysList = keys.split("["+DELIMITER+"]+");
		String dataLines = generateFile(fileInfoMap, fieldNames);
		if(dataLines != null && !"".equals(dataLines)){
			String[] dataList = generateFile(fileInfoMap, fieldNames).split(LINESEPARATOR);
			for(String data : dataList) {
				Map<String,String> lineDataMap = new HashMap<>(); 
				String[] lineData = data.split(DELIMITER);
				for(int i=0;i<keysList.length;i++) {
					try {
						lineDataMap.put(keysList[i], lineData[i]);
					} catch (IndexOutOfBoundsException e) {
						lineDataMap.put(keysList[i], "");
					}
				}
				lineDataMap.put(CLOUD_TYPE,AWS);// Added _cloudType as AWS

				try {
					if(sb.length() == 0 && new File(folderName+File.separator+fileName).length() < 2) {
						sb.append(objectMapper.writeValueAsString(lineDataMap));
					} else {
						sb.append(COMMA+LINESEPARATOR+objectMapper.writeValueAsString(lineDataMap));
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
		}
		return true;
	}
	
}
