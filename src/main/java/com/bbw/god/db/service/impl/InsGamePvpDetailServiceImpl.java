package com.bbw.god.db.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.bbw.common.DateUtil;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.bbw.god.db.dao.InsGamePvpDetailDao;
import com.bbw.god.db.entity.InsGamePvpDetailEntity;
import com.bbw.god.db.service.InsGamePvpDetailService;

import java.util.Date;
import java.util.List;

@Service
public class InsGamePvpDetailServiceImpl extends ServiceImpl<InsGamePvpDetailDao, InsGamePvpDetailEntity> implements InsGamePvpDetailService {

    @Override
    public List<InsGamePvpDetailEntity> getByUidAndDate(Long uid, Date date) {
        long start = DateUtil.toDateTimeLong(DateUtil.toDate(date, "00:00:00"));
        long end = DateUtil.toDateTimeLong(DateUtil.toDate(date, "23:59:59"));
        EntityWrapper<InsGamePvpDetailEntity> wrapper = new EntityWrapper<>();
        wrapper.between("fight_time",start,end).andNew().eq("user1",uid).or().eq("user2",uid);
        return this.selectList(wrapper);
    }
}
