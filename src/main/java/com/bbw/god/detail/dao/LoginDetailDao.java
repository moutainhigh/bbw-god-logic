package com.bbw.god.detail.dao;

import com.bbw.god.detail.entity.LoginDetailEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 充值明细表
 *
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2018-08-26 12:06:02
 */
public interface LoginDetailDao extends BaseMapper<LoginDetailEntity> {
	@Select({
			"<script>",
			"select * from god_detail.login_detail where op_datetime between #{start} and #{end}",
			"<when test = 'startDate != null'>",
			"or op_datetime between #{startDate} and #{endDate}",
			"</when>",
			"<when test = 'sid != null'>",
			"and serverid = #{sid}",
			"</when>",
			"group by uid",
			"</script>",
	})
	List<LoginDetailEntity> getListBetweenTime(@Param("start") String start, @Param("end") String end, @Param("startDate") String startDate, @Param("endDate") String endDate,@Param("sid")Integer sid);
}
