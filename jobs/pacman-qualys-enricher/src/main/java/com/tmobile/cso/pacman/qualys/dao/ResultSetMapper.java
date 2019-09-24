package com.tmobile.cso.pacman.qualys.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


/**
 * The Interface ResultSetMapper.
 */
public interface ResultSetMapper {

    /**
     * Map.
     *
     * @param rs the rs
     * @return the list
     * @throws SQLException the SQL exception
     */
    public List<Object> map(ResultSet rs) throws SQLException;

}
