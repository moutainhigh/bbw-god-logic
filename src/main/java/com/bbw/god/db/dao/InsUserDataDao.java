package com.bbw.god.db.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.bbw.god.db.entity.InsUserDataEntity;

/**
 * 玩家相关数据
 * 
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2019-02-27 09:44:18
 */
public interface InsUserDataDao extends BaseMapper<InsUserDataEntity> {
	/**
	 * 根据用户id获取用户所有数据
	 * 
	 * @param uid
	 * @return
	 */
	@Select("SELECT * FROM ins_user_data WHERE uid = #{uid}")
	List<InsUserDataEntity> selectUserData(@Param("uid") Long uid);

	/**
	 * 根据用户id获取用户指定类型的数据
	 * @param uid
	 * @param dataType
	 * @return
	 */
	@Select("SELECT * FROM ins_user_data WHERE uid = #{uid} AND data_type=#{dataType}")
	List<InsUserDataEntity> selectUserDataByType(@Param("uid") Long uid, @Param("dataType") String dataType);

}
