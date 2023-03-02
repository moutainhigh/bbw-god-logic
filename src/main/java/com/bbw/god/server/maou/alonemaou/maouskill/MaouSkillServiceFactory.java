package com.bbw.god.server.maou.alonemaou.maouskill;

import com.bbw.exception.CoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author suchaobin
 * @description 魔王技能service工厂类
 * @date 2020/8/20 14:50
 **/
@Service
public class MaouSkillServiceFactory {
    @Autowired
    @Lazy
    private List<BaseMaouSkillService> maouSkillServiceList;

    /**
     * 根据id获取对应service
     *
     * @param skillEnum 魔王技能枚举
     * @return
     */
    public BaseMaouSkillService getById(MaouSkillEnum skillEnum) {
        for (BaseMaouSkillService service : maouSkillServiceList) {
            if (skillEnum.getValue() == service.getMyId()) {
                return service;
            }
        }
        return getDefaultService();
    }

    /**
     * 获取默认service
     *
     * @return
     */
    private BaseMaouSkillService getDefaultService() {
        for (BaseMaouSkillService service : maouSkillServiceList) {
            if (MaouSkillEnum.NO.getValue() == service.getMyId()) {
                return service;
            }
        }
        throw CoderException.high(String.format("程序员没有编写id=%s的service", MaouSkillEnum.NO.getValue()));
    }
}
