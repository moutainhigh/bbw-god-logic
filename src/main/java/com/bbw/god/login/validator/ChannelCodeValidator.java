package com.bbw.god.login.validator;

import com.bbw.common.StrUtil;
import com.bbw.god.db.entity.CfgChannelEntity;
import com.bbw.god.game.config.Cfg;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Optional;

/**
 * 客户端渠道编码验证器
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-12-05 10:10
 */
public class ChannelCodeValidator implements ConstraintValidator<CheckChannelCode, String> {

    @Override
    public boolean isValid(String channelCode, ConstraintValidatorContext context) {
        if (StrUtil.isNull(channelCode)) {
            context.buildConstraintViolationWithTemplate("channel.code.need").addConstraintViolation();
            return false;
        }
        List<CfgChannelEntity> channels = Cfg.I.get(CfgChannelEntity.class);
        Optional<CfgChannelEntity> entity = channels.stream().filter(value -> value.getPlatCode().equals(channelCode)).findFirst();
        return entity.isPresent();
    }

}
