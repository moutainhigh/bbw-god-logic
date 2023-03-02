package com.bbw.god.db.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.bbw.god.db.dao.InsAccountTagsDao;
import com.bbw.god.db.entity.InsAccountTagsEntity;
import com.bbw.god.db.service.InsAccountTagsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: suchaobin
 * @createTime: 2019-11-04 10:19
 **/
@Service
public class InsAccountTagsServiceImpl extends ServiceImpl<InsAccountTagsDao, InsAccountTagsEntity> implements InsAccountTagsService {
    @Override
    public List<String> getAllTagsByAccount(String account) {
        EntityWrapper<InsAccountTagsEntity> wrapper = new EntityWrapper<>();
        wrapper.eq("account",account);
        List<InsAccountTagsEntity> entities = this.selectList(wrapper);
        List<String> dataList = new ArrayList<>();
        entities.forEach(s->dataList.add(s.getTag()));
        return dataList;
    }
}
