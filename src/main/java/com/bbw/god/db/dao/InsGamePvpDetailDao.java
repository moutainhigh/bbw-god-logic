package com.bbw.god.db.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.bbw.god.db.entity.InsGamePvpDetailEntity;
import com.bbw.god.db.split.TableSplit;

/**
 * 玩家竞技明细
 * 
 * @author suhq
 * @date 2019-07-25 11:49:44
 */
@TableSplit(tableName = "ins_game_pvp_detail", strategy = "tableName.split.month")
public interface InsGamePvpDetailDao extends BaseMapper<InsGamePvpDetailEntity> {

}
