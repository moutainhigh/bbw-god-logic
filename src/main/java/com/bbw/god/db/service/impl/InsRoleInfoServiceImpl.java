package com.bbw.god.db.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.bbw.db.PageUtils;
import com.bbw.db.Query;
import com.bbw.god.db.dao.InsRoleInfoDao;
import com.bbw.god.db.entity.InsRoleInfoEntity;
import com.bbw.god.db.service.InsRoleInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service("insRoleInfoService")
public class InsRoleInfoServiceImpl extends ServiceImpl<InsRoleInfoDao, InsRoleInfoEntity> implements InsRoleInfoService {
    @Autowired
    private InsRoleInfoDao dao;

    @Override
    public Optional<InsRoleInfoEntity> getUidAtLoginServer(int loginServerId, String username) {
        InsRoleInfoEntity role = selectOne(new EntityWrapper<InsRoleInfoEntity>().eq("origin_sid", loginServerId).eq("username", username));
        if (null == role) {
            return Optional.empty();
        }
        return Optional.ofNullable(role);
    }

    @Override
    public int updateLastLoginDate(long uid, int lastDate) {
        return dao.updateLastLoginDate(uid, lastDate);
    }

    @Override
    public int updateNickname(long uid, String nickname) {
        int i = dao.updateNickname(uid, nickname);
        return i;
    }

    @Override
    public int updateLevel(long uid, int level) {
        return dao.updateLevel(uid, level);
    }

    @Override
    public int updateSid(int oldSid, int newSid) {
        return dao.updateSid(oldSid, newSid);
    }

    @Override
    public List<InsRoleInfoEntity> getByServer(int sid) {
        List<InsRoleInfoEntity> roles = selectList(new EntityWrapper<InsRoleInfoEntity>().eq("sid", sid));
        return roles;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<InsRoleInfoEntity> page = this.selectPage(new Query<InsRoleInfoEntity>(params).getPage(), new EntityWrapper<InsRoleInfoEntity>());
        return new PageUtils(page);
    }

    @Override
    public List<Long> getAllUidsByServer(int sid) {
        return dao.getAllUidsByServer(sid);
    }

    @Override
    public List<Long> getUidsLoginBetween(int sid, int begin_date, int end_date) {
        return dao.getUidsLoginBetween(sid, begin_date, end_date);
    }

    @Override
    public List<Long> getUidsLoginBetween(int begin_date, int end_date) {
        return dao.getUidsLoginBetween(begin_date, end_date);
    }

    @Override
    public List<Long> getUidsLoginAfter(int sid, int dateInt) {
        return dao.getUidsLoginAfter(sid, dateInt);
    }

    @Override
    public List<Long> getUidsLoginAfter(int sid, int cid, int dateInt) {
        return dao.getChannelUidsLoginAfter(sid, cid, dateInt);
    }

    @Override
    public void incPay(Long uid, int pay) {
        try {
            dao.incPay(uid, pay);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public List<InsRoleInfoEntity> getRollingData(String username) {
        EntityWrapper<InsRoleInfoEntity> wrapper = new EntityWrapper<>();
        wrapper.eq("username", username).orderBy("row_update_time", false);
        return dao.selectList(wrapper);
    }

    @Override
    public List<InsRoleInfoEntity> getByUsername(String username) {
        EntityWrapper<InsRoleInfoEntity> wrapper = new EntityWrapper<>();
        wrapper.eq("username", username);
        return dao.selectList(wrapper);
    }

    @Override
    public List<Long> getUidsLevelLess4LoginBetween(int begin_date, int end_date) {
        return dao.getUidsLevelLess4LoginBetween(begin_date, end_date);
    }

    @Override
    public List<Long> getUIdsLevelLarge25LoginBetween(int begin_date, int end_date) {
        return dao.getUIdsLevelLarge25LoginBetween(begin_date, end_date);
    }

    @Override
    public List<Long> getRechargeUids(int sid) {
        EntityWrapper<InsRoleInfoEntity> wrapper = new EntityWrapper<>();
        wrapper.eq("sid", sid).gt("pay", 0);
        return dao.selectList(wrapper).stream().map(InsRoleInfoEntity::getUid).collect(Collectors.toList());
    }
}