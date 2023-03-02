package com.bbw.god.rechargeactivities;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.rechargeactivities.processor.AbstractRechargeActivityProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lwb
 * @date 2020/7/1 16:35
 */
@Service
public class RechargeActivityProcessorFactory {
    @Lazy
    @Autowired
    private List<AbstractRechargeActivityProcessor> rechargeActivityProcessors;

    public List<AbstractRechargeActivityProcessor> getProcessorsByParentType(RechargeActivityEnum parentType){
        return rechargeActivityProcessors.stream().filter(p->p.isMatchByParent(parentType)).collect(Collectors.toList());
    }

    public AbstractRechargeActivityProcessor getProcessorsByItemType(RechargeActivityItemEnum itemEnum){
        for (AbstractRechargeActivityProcessor processor:rechargeActivityProcessors){
            if (processor.isMatch(itemEnum)){
                return processor;
            }
        }
        throw new ExceptionForClientTip("rechargeActivity.not.exist",itemEnum.getType());
    }
}
