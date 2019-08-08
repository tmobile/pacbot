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
package com.tmobile.cso.pacbot.recommendation.dao;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tmobile.cso.pacbot.recommendation.util.Constants;

/**
 * The Class RDSDBManager.
 */
public class RDSDBManager {

    /** The Constant dbURL. */
    private static final String DB_URL = System.getProperty(Constants.RDS_DB_URL);

    /** The Constant dbUserName. */
    private static final String DB_USER_NAME = System.getProperty(Constants.RDS_USER);

    /** The Constant dbPassword. */
    private static final String DB_PASSWORD = System.getProperty(Constants.RDS_PWD);

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(RDSDBManager.class);
    
    /**
     * Instantiates a new RDSDB manager.
     */
    private RDSDBManager(){
        
    }
    
    /**
     * Gets the connection.
     *
     * @return the connection
     * @throws ClassNotFoundException
     *             the class not found exception
     * @throws SQLException
     *             the SQL exception
     */
    private static Connection getConnection() throws ClassNotFoundException, SQLException {
        Connection conn = null;
        Class.forName("com.mysql.jdbc.Driver");
        Properties props = new Properties();

        props.setProperty("user", DB_USER_NAME);
        props.setProperty("password", DB_PASSWORD);
        conn = DriverManager.getConnection(DB_URL, props);

        return conn;
    }

    /**
     * Execute query.
     *
     * @param query
     *            the query
     * @return the list
     */
    public static List<Map<String, String>> executeQuery(String query) {
        List<Map<String, String>> results = new ArrayList<>();
        try(
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);){
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            Map<String, String> data;
            while (rs.next()) {
                data = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    data.put(rsmd.getColumnName(i), rs.getString(i));
                }
                results.add(data);
            }
        } catch (Exception ex) {
            LOGGER.error("Error Executing Query",ex);
        } 
        return results;
    }

}
