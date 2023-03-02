package com.bbw.god.db.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.bbw.god.db.entity.InsRoleInfoEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Set;

/**
 * 角色信息表
 *
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2019-04-02 10:25:53
 */
public interface InsRoleInfoDao extends BaseMapper<InsRoleInfoEntity> {
	/**
	 * 更新最后一次登录时间
	 *
	 * @param lastDate
	 * @return
	 */
	@Update("UPDATE ins_role_info  set login_times=login_times+1,last_login_date= #{lastDate} WHERE uid=#{uid}")
	int updateLastLoginDate(@Param("uid") long uid, @Param("lastDate") int lastDate);

	/**
	 * 更新昵称
	 *
	 * @param uid
	 * @param nickname
	 * @return
	 */
	@Update("UPDATE ins_role_info  set nickname= #{nickname} WHERE uid=#{uid}")
	int updateNickname(@Param("uid") long uid, @Param("nickname") String nickname);

	/**
	 * 更新等级
	 *
	 * @param uid
	 * @param level
	 * @return
	 */
	@Update("UPDATE ins_role_info  set level= #{level} WHERE uid=#{uid}")
	int updateLevel(@Param("uid") long uid, @Param("level") int level);

	/**
	 * 更新等级
	 *
	 * @param uid
	 * @param level
	 * @return
	 */
	@Update("UPDATE ins_role_info  set pay=pay+#{pay} WHERE uid=#{uid}")
	int incPay(@Param("uid") long uid, @Param("pay") int pay);

	/**
	 * 区服ID
	 *
	 * @param oldSid
	 * @param newSid
	 * @return
	 */
	@Update("UPDATE ins_role_info  set sid= #{newSid} WHERE sid=#{oldSid}")
	int updateSid(@Param("oldSid") int oldSid, @Param("newSid") int newSid);

	/**
	 * 获取区服所有用户
	 *
	 * @param sid
	 * @return
	 */
	@Select("SELECT uid FROM ins_role_info WHERE sid=#{sid}")
	List<Long> getAllUidsByServer(@Param("sid") int sid);

	/**
	 * 获取最后登录时间在指定区间的用户
	 *
	 * @param sid：区服ID
	 * @param begin_date:      开始日期,包含
	 * @param end_date：结束日期，包含
	 * @return
	 */
	@Select("SELECT uid FROM ins_role_info WHERE sid=#{sid} AND last_login_date>=#{begin_date} AND last_login_date<=#{end_date}")
	List<Long> getUidsLoginBetween(@Param("sid") int sid, @Param("begin_date") int begin_date, @Param("end_date") int end_date);

	/**
	 * 获取最后登录时间在指定区间的用户
	 *
	 * @param begin_date:      开始日期,包含
	 * @param end_date：结束日期，包含
	 * @return
	 */
	@Select("SELECT uid FROM ins_role_info WHERE last_login_date>=#{begin_date} AND last_login_date<=#{end_date}")
	List<Long> getUidsLoginBetween(@Param("begin_date") int begin_date, @Param("end_date") int end_date);

	@Select("SELECT uid FROM ins_role_info WHERE level<=4 AND last_login_date>=#{begin_date} AND last_login_date<=#{end_date}")
	List<Long> getUidsLevelLess4LoginBetween(@Param("begin_date") int begin_date, @Param("end_date") int end_date);

	@Select("SELECT uid FROM ins_role_info WHERE level >= 25  AND last_login_date>=#{begin_date} AND last_login_date<=#{end_date}")
	List<Long> getUIdsLevelLarge25LoginBetween(@Param("begin_date") int begin_date, @Param("end_date") int end_date);



	/**
	 * 获取指定日期之后的登录用户
	 *
	 * @param sid
	 * @param dateInt: 日期,包含
	 * @return
	 */
	@Select("SELECT uid FROM ins_role_info WHERE sid=#{sid} AND last_login_date>=#{dateInt} ")
	List<Long> getUidsLoginAfter(@Param("sid") int sid, @Param("dateInt") int dateInt);

	/**
	 * 获取指定日期之后的登录用户
	 *
	 * @param sid
	 * @param cid
	 * @param dateInt: 日期,包含
	 * @return
	 */
	@Select("SELECT uid FROM ins_role_info WHERE sid=#{sid} AND cid=#{cid} AND last_login_date>=#{dateInt} ")
	List<Long> getChannelUidsLoginAfter(@Param("sid") int sid, @Param("cid") int cid, @Param("dateInt") int dateInt);

	/**
	 * 查找某个时间段登录过的玩家(仅包含sid,uid)
	 *
	 * @param sinLastLoginDate
	 * @return
	 */
	@Select("SELECT sid,uid FROM ins_role_info WHERE last_login_date>=#{dateInt} and level>=#{level}")
	List<InsRoleInfoEntity> getUidsWithSid(@Param("dateInt") int sinLastLoginDate, @Param("level") int level);

	/**
	 * 获取某个时间后的达到一定等级的玩家
	 *
	 * @param level
	 * @param dateInt
	 * @return
	 */
	@Select("SELECT uid FROM ins_role_info WHERE level>=#{level} AND last_login_date>=#{dateInt} ")
	List<Long> getUidByLevelAndLastLoginDate(@Param("level") int level, @Param("dateInt") int dateInt);

	/**
	 * 根据玩家nickname查找玩家
	 *
	 * @param nickname
	 * @return
	 */
	@Select("SELECT uid FROM ins_role_info WHERE nickname =#{nickname}")
	List<Long> getUidByNickName(@Param("nickname") String nickname);
}