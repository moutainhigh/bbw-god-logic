package com.bbw.god.gameuser.guide.v3.god;

import com.bbw.god.rd.RDAdvance;
import com.bbw.god.server.god.RDAttachGod;
import com.bbw.god.server.god.processor.AbstractGodProcessor;

/**
 * @author suchaobin
 * @description 基础新手引导神仙处理器
 * @date 2020/12/11 17:52
 **/
public abstract class BaseNewerGuideGodProcessor extends AbstractGodProcessor {

    public boolean isNewerGuideGodProcessor() {
        return true;
    }

    protected void setAttachGod(RDAdvance rdAdvance) {
        // 有神仙附体
        if (rdAdvance.getAttachedGod() != null) {
            RDAttachGod rdAttachGod = RDAttachGod.fromRDCommon(rdAdvance);
            rdAdvance.setAttachGod(rdAttachGod);
        }
        // 兼容旧版本
        rdAdvance.setGodRemainStep(rdAdvance.getGodRemainCell());
    }
}
