package com.bbw.god.db.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.bbw.god.db.entity.InsGamePveDetailEntity;
import com.bbw.god.db.split.TableSplit;

/**
 * 玩家PVE竞技明细
 * 
 * @author suhq
 * @date 2019-07-25 11:49:44
 */
@TableSplit(tableName = "ins_game_pve_detail", strategy = "tableName.split.month")
public interface InsGamePveDetailDao extends BaseMapper<InsGamePveDetailEntity> {

}
