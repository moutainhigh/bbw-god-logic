package com.bbw.god.gameuser.leadercard;

import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.gameuser.leadercard.service.LeaderCardService;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 分身卡接口
 * @author：lwb
 * @date: 2021/3/22 13:54
 * @version: 1.0
 */
@Api(tags = {"主角卡接口"})
@RestController
public class LeaderCardController extends AbstractController {
    @Autowired
    private LeaderCardService leaderCardService;
    /**
     * 合成
     * @return
     */
    @RequestMapping(CR.LeaderCard.SYNTHESIS)
    public RDLeaderCard synthesis(){
        return leaderCardService.synthesis(getUserId());
    }

    /**
     * 随机选择一个技能
     * @return
     */
    @RequestMapping(CR.LeaderCard.GET_RANDOM_SKILL)
    public RDLeaderCard getRandomSkill(){
        return leaderCardService.getRandomSkill(getUserId());
    }

    /**
     * 替换随机技能
     * @return
     */
    @RequestMapping(CR.LeaderCard.SET_RANDOM_SKILL)
    public RDLeaderCard setRandomSkill(){
        return leaderCardService.setInitRandomSkill(getUserId());
    }

    /**
     * 进入主页
     * @return
     */
    @RequestMapping(CR.LeaderCard.MAIN_INFO)
    public RDLeaderCard getMainInfo(){
        return leaderCardService.getMainPageInfo(getUserId());
    }

    /**
     * 进入升级界面
     * @return
     */
    @RequestMapping(CR.LeaderCard.GET_UP_LV_INFO)
    public RDLeaderCard getUpLvInfo(){
        return leaderCardService.getUpLvInfo(getUserId());
    }

    /**
     * 重置加点
     * @return
     */
    @RequestMapping(CR.LeaderCard.ADD_POINT_RESET)
    public RDLeaderCard addPointReset(){
        return leaderCardService.restAddPoint(getUserId());
    }

    /**
     * 属性加点
     * @param atk  攻击力加点数量
     * @param hp 防御力加点数量
     * @return
     */
    @RequestMapping(CR.LeaderCard.ADD_POINT)
    public RDLeaderCard addPoint(Integer atk,Integer hp){
        return leaderCardService.addPoint(getUserId(),atk,hp);
    }


    /**
     * 获取当前阶级情况
     * @return
     */
    @RequestMapping(CR.LeaderCard.HV_INFO)
    public RDLeaderCard getHvInfo(){
        return leaderCardService.getHvInfo(getUserId());
    }
    /**
     * 升阶
     * @return
     */
    @RequestMapping(CR.LeaderCard.UP_HV)
    public RDLeaderCard upHv(){
        return leaderCardService.upHv(getUserId());
    }

    /**
     * 升星
     * @return
     */
    @RequestMapping(CR.LeaderCard.UP_STAR)
    public RDLeaderCard upStar(String cards){
        return leaderCardService.upStar(getUserId(),cards);
    }

    /**
     * 获取已经激活的属性
     * @return
     */
    @RequestMapping(CR.LeaderCard.LIST_PROPERTY)
    public RDLeaderCard listProperty(){
        return leaderCardService.listOwnProperty(getUserId());
    }
    /**
     * 激活属性
     * @param property
     * @return
     */
    @RequestMapping(CR.LeaderCard.ACTIVE_PROPERTY)
    public RDLeaderCard activeProperty(Integer property){
        return leaderCardService.activeProperty(getUserId(),property);
    }

    /**
     * 改属性
     * @param property
     * @return
     */
    @RequestMapping(CR.LeaderCard.CHANGE_PROPERTY)
    public RDLeaderCard changeProperty(Integer property){
        return leaderCardService.changeProperty(getUserId(),property);
    }

    /**
     * 获得技能组列表
     * @return
     */
    @ApiOperation(value = "获得技能组列表")
    @GetMapping(CR.LeaderCard.GET_SKILLS_GROUP)
    public RDLeaderCardSkills getSkillsGroup(){
        return leaderCardService.getLeaderSkillsGroup(getUserId());
    }

    /**
     * 切换技能组
     * @param index
     * @return
     */
    @ApiOperation(value = "切换技能组")
    @GetMapping(CR.LeaderCard.CHANGE_SKILLS_GROUP)
    public RDSuccess changeLeaderSkillsGroup(int index){
        return leaderCardService.changeLeaderSkillsGroup(getUserId(), index);
    }

    /**
     * 激活技能组
     * @return
     */
    @ApiOperation(value = "激活技能组")
    @GetMapping(CR.LeaderCard.ACTIVATION_SKILLS_GROUP)
    public RDCommon activationSkillsGroup(){
        return leaderCardService.activationSkillsGroup(getUserId());
    }
}
