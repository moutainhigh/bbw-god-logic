package com.bbw.god.questionnaire;

import com.bbw.common.Rst;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author suchaobin
 * @description 问卷调查接口
 * @date 2020/12/4 08:52
 **/
@RestController
public class QuestionnaireCtrl extends AbstractController {
    @Autowired
    private QuestionnaireService questionnaireService;

    @RequestMapping(CR.Questionnaire.QUESTIONNAIRE_JOIN)
    public Rst join() {
        return questionnaireService.join(getUserId());
    }

    @RequestMapping(CR.Questionnaire.QUESTIONNAIRE_HIDE_ICON)
    public Rst hideIcon() {
        return questionnaireService.hideIcon(getUserId());
    }
}
