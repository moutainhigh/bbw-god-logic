package com.bbw.god.gm;

import com.bbw.god.activityrank.game.guess.GuessCompetitionRankService;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.mall.skillscroll.SkillScrollService;
import com.bbw.god.rd.RDCommon;
import com.bbw.page.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author longwh
 * @date 2022/12/14 14:23
 */
@RestController
@RequestMapping("/gm")
public class GMTestCtrl extends AbstractController {

    @Autowired
    private SkillScrollService skillScrollService;
    @Autowired
    private GuessCompetitionRankService rankService;

    @GetMapping(CR.SkillScroll.SYNTHESIS_SKILL_SCROLL)
    public RDCommon synthesis(Long uid, String ids, int type, Integer targetId) {

        String[] split = ids.split(",");
        List<Integer> treasureIds = Arrays.stream(split).map(Integer::parseInt).collect(Collectors.toList());
        return skillScrollService.synthesis(uid, treasureIds, type, targetId);
    }

    @GetMapping("/guessRank!add")
    public RDCommon addGuessRank(Long uid, Integer value) {

        rankService.addGuessingValue(uid, value);
        return new RDCommon();
    }
}