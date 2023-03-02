package com.bbw.god.login.validator;

import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.Cfg;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 区服ID验证器
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-12-05 10:10
 */
@Service
public class ServerIdValidator implements ConstraintValidator<CheckServerId, Integer> {

    @Override
    public boolean isValid(Integer serverId, ConstraintValidatorContext context) {
        if (null == serverId || 0 == serverId.intValue()) {
            return false;
        }
        CfgServerEntity server = Cfg.I.get(serverId, CfgServerEntity.class);
        return null != server;
    }

}
