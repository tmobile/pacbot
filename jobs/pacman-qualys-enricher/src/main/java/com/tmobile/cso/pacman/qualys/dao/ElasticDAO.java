package com.tmobile.cso.pacman.qualys.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.tmobile.cso.pacman.qualys.dao.DBManager;


/**
 * The Class ElasticDAO.
 */
public class ElasticDAO {
    
    /**
     * Gets the vuln info.
     *
     * @return the vuln info
     */
    public List<Object> getVulnInfo() {
        String query = "select qid,vulntype,severitylevel,title,category,patchable,pciflag from kb_vuln";
        return DBManager.executeQuery(query, new ResultSetMapper() {

            @Override
            public List<Object> map(ResultSet rs) throws SQLException {
                List<Object> __result = new ArrayList<>();
                Map<Long, Map<String, Object>> _results = new HashMap<>();
                Map<String, Object> vulnInfo;
                while (rs.next()) {
                    vulnInfo = new LinkedHashMap<>();
                    vulnInfo.put("severitylevel", rs.getByte("severitylevel"));
                    vulnInfo.put("vulntype", rs.getString("vulntype"));
                    vulnInfo.put("title", rs.getString("title"));
                    vulnInfo.put("category", rs.getString("category"));
                    vulnInfo.put("qid", rs.getString("qid"));
                    vulnInfo.put("patchable", rs.getString("patchable"));
                    vulnInfo.put("pciflag", rs.getString("pciflag"));
                    _results.put(rs.getLong("qid"), vulnInfo);

                }
                __result.add(_results);
                return __result;
            }
        });
    }

}
