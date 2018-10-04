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
package com.tmobile.cso.pacman.inventory.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tmobile.cso.pacman.inventory.InventoryConstants;
import com.tmobile.cso.pacman.inventory.util.Util;


/**
 * The Class DBManager.
 */
public class DBManager {
    
    /** The log. */
    private static Logger log = LogManager.getLogger(DBManager.class);
    
	/**
	 * Instantiates a new DB manager.
	 */
	private DBManager(){}
	
	/** The Constant DBURL. */
	private static final String DBURL = System.getenv("REDSHIFT_URL");
    
    /** The Constant MASTERUSERNAME. */
    private static final String MASTERUSERNAME =  Util.base64Decode(System.getenv("REDSHIFT_INFO")).split(":")[0] ;
    
    /** The Constant MASTERUSERPASSWORD. */
    private static final String MASTERUSERPASSWORD = Util.base64Decode(System.getenv("REDSHIFT_INFO")).split(":")[1];
    
    /**
     * Gets the connection.
     *
     * @return the connection
     * @throws ClassNotFoundException the class not found exception
     * @throws SQLException the SQL exception
     */
    private static Connection getConnection() throws ClassNotFoundException, SQLException {
    	Connection conn = null;
	    Class.forName("com.amazon.redshift.jdbc42.Driver");
	    Properties props = new Properties();
	    
	    props.setProperty("user", MASTERUSERNAME);
	    props.setProperty("password", MASTERUSERPASSWORD);
	    conn = DriverManager.getConnection(DBURL, props);
	    return conn ;
    }
    
    
    /**
     * Execute query.
     *
     * @param query the query
     * @return the list
     */
    public static List<Map<String,String>> executeQuery(String query){
    	List <Map<String,String>> results = new ArrayList<>();
    	Connection conn = null;
    	Statement stmt = null;
    	ResultSet rs = null;
        try{
        	conn = getConnection();
        	stmt = conn.createStatement();
	        rs = stmt.executeQuery(query);
	        ResultSetMetaData rsmd = rs.getMetaData();
	        int columnCount = rsmd.getColumnCount();
	        while(rs.next()){
	        	Map<String,String> data = new LinkedHashMap<>();
	        	for(int i=1;i<=columnCount;i++){
	        		data.put(rsmd.getColumnName(i), rs.getString(i));
	        	}
	        	results.add(data);
        	}
	        rs.close();
	        stmt.close();
	        conn.close();
        }catch(Exception ex){
            log.error(InventoryConstants.ERROR_EXECUTEQUERY,ex);
        }finally{
            try{
                if(rs!=null)
                    rs.close();
            }catch(Exception ex){
                log.error(InventoryConstants.ERROR_EXECUTEQUERY,ex);
         }
        	try{
        		if(stmt!=null)
        			stmt.close();
        	}catch(Exception ex){
        	    log.error(InventoryConstants.ERROR_EXECUTEQUERY,ex);
         }// nothing we can do
         try{
            if(conn!=null)
               conn.close();
         }catch(Exception ex){
             log.error(InventoryConstants.ERROR_EXECUTEQUERY,ex);
         }
      }
      return results;   	
    }
    
    /**
     * Execute update.
     *
     * @param query the query
     */
    public static void executeUpdate(String query){
    	
    	Connection conn = null;
    	Statement stmt = null;
        try{
        	conn = getConnection();
        	stmt = conn.createStatement();
        	stmt.executeUpdate(query);

	        stmt.close();
	        conn.close();
        }catch(Exception ex){
            log.error(InventoryConstants.ERROR_EXECUTEUPDATE,ex);
        }finally{

        	try{
        		if(stmt!=null)
        			stmt.close();
        	}catch(Exception ex){
        	    log.error(InventoryConstants.ERROR_EXECUTEUPDATE,ex);
         }// nothing we can do
         try{
            if(conn!=null)
               conn.close();
         }catch(Exception ex){
             log.error(InventoryConstants.ERROR_EXECUTEUPDATE,ex);
         }
      }
   }
}
