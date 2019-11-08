package com.tmobile.pacman.commons.azure.sqlserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.util.StringUtils;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.utils.CommonUtils;

/**
 * @author Raghavendra
 *
 */
public class CloudInsightSqlServer {

	private static final Logger logger = LoggerFactory.getLogger(CloudInsightSqlServer.class);

	public static Connection getDBConnection() throws SQLException {
		String hostName = getClouldInsightSqlServer();
		String dbName = "cloudinsightbillingdb";
		String user = getClouldInsightUser();
		String password = getClouldInsightPassWord();

		if (StringUtils.isNullOrEmpty(hostName) || StringUtils.isNullOrEmpty(user)
				|| StringUtils.isNullOrEmpty(password)) {
			throw new RuntimeException(
					" Cloud insight server mandatory configuration CLOUD_INSIGHT_SQL_SERVER/CLOUD_INSIGHT_USER/CLOUD_INSIGHT_PASSWORD ");
		}
		String url = String.format(
				"jdbc:sqlserver://%s:1433;database=%s;user=%s;password=%s;encrypt=true;"
						+ "hostNameInCertificate=*.database.windows.net;loginTimeout=30;",
				hostName, dbName, user, password);
		Connection connection = null;

		connection = DriverManager.getConnection(url);
		return connection;
	}

	public static String getClouldInsightSqlServer() {
		return CommonUtils.getEnvVariableValue(PacmanSdkConstants.CLOUD_INSIGHT_SQL_SERVER);
	}

	public static String getClouldInsightUser() {
		return CommonUtils.getEnvVariableValue(PacmanSdkConstants.CLOUD_INSIGHT_USER);
	}

	public static String getClouldInsightPassWord() {
		return CommonUtils.getEnvVariableValue(PacmanSdkConstants.CLOUD_INSIGHT_PASSWORD);
	}

	/**
	 * @param appTag
	 * @return
	 */
	public static String getValidAppTag(String appTag) {
		Connection connection = null;
		try {
			connection = getDBConnection();
		} catch (SQLException ex) {
			logger.error("exception while getting connection ", ex);
			return null;
		}
		String validAppTag = null;
		String userAppTag = null;
		String selectSql = "SELECT * FROM DimAppAlias where UserApplication='" + appTag + "'";
		try (Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(selectSql)) {
			if (resultSet.next()) {
				userAppTag = resultSet.getString(1);
				validAppTag = resultSet.getString(2);
			}
			if (userAppTag != null && userAppTag.equals(appTag)) {
				logger.debug("apptag is not valid current tag : {} correct tag : {}",appTag,validAppTag);
				return validAppTag;
			}			
		} catch (Exception e) {
			logger.error("exception while executing query ", e);
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
			}
		}		
		return null;
	}
}
