package com.bbw.god.db.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.bbw.god.db.dao.AccountRoleMapDao;
import com.bbw.god.db.entity.AccountRoleMapEntity;
import com.bbw.god.db.service.AccountRoleMapService;

@Service("accountRoleMapService")
public class AccountRoleMapServiceImpl extends ServiceImpl<AccountRoleMapDao, AccountRoleMapEntity> implements AccountRoleMapService {

}
