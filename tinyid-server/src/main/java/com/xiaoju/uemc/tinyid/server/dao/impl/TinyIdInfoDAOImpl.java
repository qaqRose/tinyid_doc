package com.xiaoju.uemc.tinyid.server.dao.impl;

import com.xiaoju.uemc.tinyid.server.dao.TinyIdInfoDAO;
import com.xiaoju.uemc.tinyid.server.dao.entity.TinyIdInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author du_imba
 * id信息dao
 *
 */
@Repository
public class TinyIdInfoDAOImpl implements TinyIdInfoDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 通过bizType查询tiny_id_info表数据
     * biz_type唯一
     * @param bizType 业务类型
     * @return
     */
    @Override
    public TinyIdInfo queryByBizType(String bizType) {
        String sql = "select id, biz_type, begin_id, max_id," +
                " step, delta, remainder, create_time, update_time, version" +
                " from tiny_id_info where biz_type = ?";
        List<TinyIdInfo> list = jdbcTemplate.query(sql, new Object[]{bizType}, new TinyIdInfoRowMapper());
        if(list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    /**
     * 更新最大值
     * @param id 主键
     * @param newMaxId 新最大值
     * @param oldMaxId 老最大id
     * @param version 乐观锁
     * @param bizType 业务类型
     * @return
     */
    @Override
    public int updateMaxId(Long id, Long newMaxId, Long oldMaxId, Long version, String bizType) {
        String sql = "update tiny_id_info set max_id= ?," +
                " update_time=now(), version=version+1" +
                " where id=? and max_id=? and version=? and biz_type=?";
        return jdbcTemplate.update(sql, newMaxId, id, oldMaxId, version, bizType);
    }


    public static class TinyIdInfoRowMapper implements RowMapper<TinyIdInfo> {

        @Override
        public TinyIdInfo mapRow(ResultSet resultSet, int i) throws SQLException {
            TinyIdInfo tinyIdInfo = new TinyIdInfo();
            tinyIdInfo.setId(resultSet.getLong("id"));
            tinyIdInfo.setBizType(resultSet.getString("biz_type"));
            tinyIdInfo.setBeginId(resultSet.getLong("begin_id"));
            tinyIdInfo.setMaxId(resultSet.getLong("max_id"));
            tinyIdInfo.setStep(resultSet.getInt("step"));
            tinyIdInfo.setDelta(resultSet.getInt("delta"));
            tinyIdInfo.setRemainder(resultSet.getInt("remainder"));
            tinyIdInfo.setCreateTime(resultSet.getDate("create_time"));
            tinyIdInfo.setUpdateTime(resultSet.getDate("update_time"));
            tinyIdInfo.setVersion(resultSet.getLong("version"));
            return tinyIdInfo;
        }
    }
}
