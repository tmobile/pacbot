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
package com.tmobile.pacman.api.asset.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.tmobile.pacman.api.asset.AssetConstants;

/**
 * The Class PacmanRedshiftRepository connects to Redshift database for CRUD operations.
 */
@Repository
public class PacmanRedshiftRepository {

    @Value("${redshift.url:}")
    private String dbURL;
    
    @Value("${redshift.userName:}")
    private String userName;
    
    @Value("${redshift.password:}")
    private String password;

    private static final Log LOGGER = LogFactory.getLog(PacmanRedshiftRepository.class);

    /**
     * Gets the connection.
     *
     * @return the connection
     * @throws ClassNotFoundException the class not found exception
     * @throws SQLException the SQL exception
     */
    private Connection getConnection() throws ClassNotFoundException, SQLException {
        Connection conn = null;
        Class.forName("com.amazon.redshift.jdbc42.Driver");
        Properties props = new Properties();

        props.setProperty("user", userName);
        props.setProperty("password", password);
        conn = DriverManager.getConnection(dbURL, props);
        return conn;
    }

    /**
     * Gets the Count.
     *
     * @param query the query
     * @return the int
     */
    public int count(String query) {
        int result = 0;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                result = rs.getInt("count");
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception ex) {
            LOGGER.error(AssetConstants.ERROR_COUNT , ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                LOGGER.error(AssetConstants.ERROR_COUNT , ex);
            }
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception ex) {
                LOGGER.error(AssetConstants.ERROR_COUNT , ex);
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ex) {
                LOGGER.error(AssetConstants.ERROR_COUNT , ex);
            }
        }
        return result;
    }

    /**
     * Execute query.
     *
     * @param query the query
     * @return the list
     */
    public List<Map<String, String>> executeQuery(String query) {
        List<Map<String, String>> results = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                Map<String, String> value = new HashMap<>();
                value.put("resourceType", rs.getString("resourceType"));
                value.put("_resourceid", rs.getString("_resourceid"));
                value.put("fieldName", rs.getString("fieldname"));
                results.add(value);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception ex) {
            LOGGER.error(AssetConstants.ERROR_EXEQUTEQUERY , ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                LOGGER.error(AssetConstants.ERROR_EXEQUTEQUERY , ex);
            }

            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception ex) {
                LOGGER.error(AssetConstants.ERROR_EXEQUTEQUERY , ex);
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ex) {
                LOGGER.error(AssetConstants.ERROR_EXEQUTEQUERY , ex);
            }
        }
        return results;
    }

    /**
     * Batch update.
     *
     * @param queries the queries
     * @return the int[]
     */
    public int[] batchUpdate(List<String> queries) {

        int[] result = new int[queries.size()];
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            conn.setAutoCommit(false);
            int i = 0;
            for (String query : queries) {
                result[i++] = stmt.executeUpdate(query);
            }
            conn.commit();
            stmt.close();
            conn.close();
        } catch (Exception ex) {
            LOGGER.error(AssetConstants.ERROR_BATCHUPDATE , ex);
            try {
                if(conn != null) {
                    conn.rollback();
                }
            } catch (SQLException e) {
                LOGGER.error(AssetConstants.ERROR_BATCHUPDATE , e);
            }
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception ex) {
                LOGGER.error(AssetConstants.ERROR_BATCHUPDATE , ex);
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ex) {
                LOGGER.error(AssetConstants.ERROR_BATCHUPDATE , ex);
            }
        }
        return result;
    }
}
