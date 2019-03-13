package com.tmobile.cso.pacman.datashipper.dao;

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

import com.tmobile.cso.pacman.datashipper.util.Constants;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(RDSDBManager.class);
    
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
