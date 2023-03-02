package com.bbw.god.db.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.bbw.god.db.entity.InsUserDetailEntity;
import com.bbw.god.db.split.TableSplit;

/**
 * 玩家资源明细表
 * 
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2019-03-26 20:14:11
 */
@TableSplit(tableName = "ins_user_detail", strategy = "InsUserDetailDao.split.uid")
public interface InsUserDetailDao extends BaseMapper<InsUserDetailEntity> {

}
