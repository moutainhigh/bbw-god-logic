package com.bbw.god.mall.skillscroll;

import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.mall.skillscroll.cfg.CfgDesignateSkillScroll;
import com.bbw.god.mall.skillscroll.cfg.SkillScrollTool;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 技能卷轴接口
 * @date 2021/2/2 16:43
 **/
@RestController
public class SkillScrollCtrl extends AbstractController {
    @Autowired
    private SkillScrollService skillScrollService;


    @GetMapping(CR.SkillScroll.SYNTHESIS_SKILL_SCROLL)
    public RDCommon synthesis(String ids, int type, Integer targetId) {
        String[] split = ids.split(",");
        List<Integer> treasureIds = Arrays.stream(split).map(Integer::parseInt).collect(Collectors.toList());
        return skillScrollService.synthesis(getUserId(), treasureIds, type, targetId);
    }

    @GetMapping(CR.SkillScroll.LIST_ABLE_SYNTHESIS_SKILL_SCROLLS)
    public RDDesignateInfoList listAbleSynthesis() {
        List<CfgDesignateSkillScroll> list = SkillScrollTool.getDesignateSkillScrolls();
        List<RDDesignateInfoList.RDDesignateInfo> infos = new ArrayList<>();
        for (CfgDesignateSkillScroll cfgDesignateSkillScroll : list) {
            Integer chapter = cfgDesignateSkillScroll.getChapter();
            List<Award> awards = cfgDesignateSkillScroll.getAbleDesignateIds().stream().map(tmp -> new Award(tmp, AwardEnum.FB, 1)).collect(Collectors.toList());
            infos.add(new RDDesignateInfoList.RDDesignateInfo(chapter, awards));
        }
        return new RDDesignateInfoList(infos);
    }
}
