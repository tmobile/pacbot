/*
 * 
 */
package com.tmobile.cso.pacman.qualys.dao;

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

import com.tmobile.cso.pacman.qualys.dao.ResultSetMapper;
import com.tmobile.cso.pacman.qualys.util.Util;


/**
 * The Class DBManager.
 */
public class DBManager {

    /** The Constant dbURL. */
    private static final String DBURL = System.getenv("REDSHIFT_DB_URL");

    /** The Constant MasterUsername. */
    private static final String USER_NAME = Util.base64Decode(System.getenv("redshiftinfo")).split(":")[0];

    /** The Constant MasterUserPassword. */
    private static final String PASSWORD = Util.base64Decode(System.getenv("redshiftinfo")).split(":")[1];

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DBManager.class);

    /**
     * Instantiates a new DB manager.
     */
    private DBManager() {

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

        props.setProperty("user", USER_NAME);
        props.setProperty("password", PASSWORD);
        conn = DriverManager.getConnection(DBURL, props);
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

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);) {
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
            LOGGER.error("Error fetching table info" , ex);
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

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);) {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            while (rs.next()) {
                Map<String, String> data = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    data.put(rsmd.getColumnName(i), rs.getString(i));
                }
                results.add(data);
            }
        } catch (Exception ex) {
            LOGGER.error("Error fetching executing query" + query, ex);
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

    /**
     * Execute query.
     *
     * @param query
     *            the query
     * @param rsm
     *            the rsm
     * @return the list
     */
    public static List<Object> executeQuery(String query, ResultSetMapper rsm) {

        List<Object> results = null;
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);) {
            results = rsm.map(rs);

        } catch (Exception ex) {
            LOGGER.error("Error fetching executing query" + query, ex);
        }
        return results;
    }

    /**
     * Execute update.
     *
     * @param query
     *            the query
     */
    public static void executeUpdate(String query) {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement();) {
            stmt.executeUpdate(query);

        } catch (Exception ex) {
            LOGGER.error("Error Updating  :" + query, ex);
        }
    }
}
