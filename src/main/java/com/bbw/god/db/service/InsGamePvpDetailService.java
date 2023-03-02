package com.bbw.god.db.service;

import com.baomidou.mybatisplus.service.IService;
import com.bbw.god.db.entity.InsGamePvpDetailEntity;

import java.util.Date;
import java.util.List;

/**
 * 玩家竞技明细
 * 
 * @author suhq
 * @date 2019-07-25 11:46:11
 */
public interface InsGamePvpDetailService extends IService<InsGamePvpDetailEntity> {
    /**
      * 根据玩家id和日期获取对应数据
      * @param uid  玩家id
      * @param date 日期
      * @return: java.util.List<com.bbw.god.db.entity.InsGamePvpDetailEntity>
      * @author suchaobin
      * @date 2019/11/14 9:50
      **/
    List<InsGamePvpDetailEntity> getByUidAndDate(Long uid, Date date);
}
