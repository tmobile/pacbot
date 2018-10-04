package com.tmobile.cso.pacman.datashipper.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tmobile.cso.pacman.datashipper.util.Constants;

/**
 * The Class DBManager.
 */
public class DBManager {

    
    /** The Constant dbURL. */
    private static final String DB_URL = System.getenv("REDSHIFT_DB_URL");

    /** The Constant MasterUsername. */
    private static final String USER_ID = System.getProperty(Constants.REDSHIFT_USER);

    /** The Constant MasterUserPassword. */
    private static final String PASSWORD = System.getProperty(Constants.REDSHIFT_PWD);

    private static final Logger LOGGER = LoggerFactory.getLogger(DBManager.class);
    
    private DBManager(){
        
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
        Class.forName("com.amazon.redshift.jdbc42.Driver");
        Properties props = new Properties();

        props.setProperty("user", USER_ID);
        props.setProperty("password", PASSWORD);
        conn = DriverManager.getConnection(DB_URL, props);

        return conn;
    }

    /**
     * Gets the table information.
     *
     * @param datasource
     *            the datasource
     * @return the table information
     */
    public static Map<String, Map<String, String>> getTableInformation(String datasource) {
        String query = "select  tablename,\"column\",type from pg_table_def  where tablename  like '"
                + datasource.toLowerCase() + "_%'";
        Map<String, Map<String, String>> tableInfo = new HashMap<>();
        try(
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);){
            while (rs.next()) {
                String tableName = rs.getString("tablename");
                String column = rs.getString("column");
                String type = rs.getString("type");
                Map<String, String> columnInfo = tableInfo.get(tableName);
                if (columnInfo == null) {
                    columnInfo = new LinkedHashMap<>();
                    tableInfo.put(tableName, columnInfo);
                }
                columnInfo.put(column, type);
            }
           
        } catch (Exception ex) {
            LOGGER.error("Error in getTableInformation",ex);
        }
        return tableInfo;
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

    /**
     * Gets the child table names.
     *
     * @param index
     *            the index
     * @return the child table names
     */
    public static List<String> getChildTableNames(String index) {
        List<String> childTableNames = new ArrayList<>();
        String query = "select  distinct tablename from pg_table_def  where tablename  like '" + index.toLowerCase()
                + "^_%' ESCAPE '^'";

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);) {
            while (rs.next()) {
                String tableName = rs.getString("tablename");
                childTableNames.add(tableName);
            }
        } catch (Exception ex) {
            LOGGER.error("Error fetching child tables for type :" + index, ex);
        }
        return childTableNames;
    }
}