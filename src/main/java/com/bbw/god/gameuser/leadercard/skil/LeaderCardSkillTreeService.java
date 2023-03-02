package com.bbw.god.gameuser.leadercard.skil;

import com.bbw.common.ID;
import com.bbw.common.ListUtil;
import com.bbw.common.StrUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardSkillTool;
import com.bbw.god.game.config.card.CfgCardSkill;
import com.bbw.god.game.config.card.LeaderCardSkillGroupEnum;
import com.bbw.god.game.config.treasure.CfgSkillScrollLimitEntity;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.leadercard.LeaderCardTool;
import com.bbw.god.gameuser.leadercard.UserLeaderCard;
import com.bbw.god.gameuser.leadercard.event.LeaderCardEventPublisher;
import com.bbw.god.gameuser.leadercard.service.LeaderCardService;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.mall.skillscroll.cfg.SkillScrollTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author liuwenbin
 */
@Service
public class LeaderCardSkillTreeService {
    @Autowired
    private LeaderCardService leaderCardService;
    @Autowired
    private GameUserService gameUserService;
    /**
     * 展示属性对应的所有已经获得的技能
     * 仅显示已激活的技能
     *
     * @param uid
     */
    public RDLeaderCardSkillTree showAllSkill(long uid){
        UserLeaderCard leaderCard = leaderCardService.getUserLeaderCard(uid);
        UserLeaderCardSkillTree[] trees={getUserSkillTree(uid, leaderCard.getProperty()),getUserSkillTree(uid, 60)};
        RDLeaderCardSkillTree rd= RDLeaderCardSkillTree.getShowSkillInstance();
        List<RDLeaderCardSkillTree.SkillState> skillStates=new ArrayList<>();
        for (UserLeaderCardSkillTree tree : trees) {
            if (tree==null){
                continue;
            }
            for (Integer skillId : tree.showAllSkills()) {
                if (skillStates.stream().filter(p->p.getSkillId()==skillId.intValue()).findFirst().isPresent()){
                    continue;
                }
                skillStates.add(RDLeaderCardSkillTree.SkillState.getInstance(skillId));
            }
        }
        sKillClassifyToRd(skillStates,rd);
        return rd;
    }
    /**
     * 技能替换
     * @param uid
     * @param skillId
     * @param pos
     * @return
     */
    public RDLeaderCardSkillTree replaceSkill(long uid,int skillId,int pos){
        UserLeaderCard leaderCard = leaderCardService.getUserLeaderCard(uid);
        UserLeaderCardSkillTree tree = getUserSkillTree(uid, leaderCard.getProperty());
        if (tree==null || !tree.ifOwnSkill(skillId)){
            tree = getUserSkillTree(uid, 60);
            if (tree==null || !tree.ifOwnSkill(skillId)){
                throw new ExceptionForClientTip("leader.card.skill.replace.not.exist");
            }
        }
        if (pos<0 || pos>2){
            //位置错误
            throw new ExceptionForClientTip("leader.card.skill.index.not.exist");
        }
        if (leaderCard.checkCurrentUseSkill(skillId)){
            throw new ExceptionForClientTip("card.useSkillScroll.no.repeat");
        }
        leaderCard.replaceSkill(skillId,pos);
        gameUserService.updateItem(leaderCard);
        return new RDLeaderCardSkillTree();
    }


    /**
     * 技海 列表显示
     * ①　已激活的技能图标为常态显示，未激活的的技能需置灰
     * ②　可激活时，需有闪动提示。（点亮前置条件，且背包有技能卷轴可使用时，视为可激活）
     * @param uid
     * @param property
     * @return
     */
    public RDLeaderCardSkillTree listSkillTree(long uid ,int property){
        if (property==60){
            return listExclusiveSkills(uid);
        }
        RDLeaderCardSkillTree rd=RDLeaderCardSkillTree.getShowSkillInstance();
        UserLeaderCardSkillTree userSkillTree = getUserSkillTree(uid, property);
        //专属
        UserLeaderCardSkillTreeDetail detail = getSkillTreeDetail(uid);
        CfgLeaderCardSkill cfgLeaderCardSkill = LeaderCardTool.getCfgLeaderCardSkill();
        List<CfgLeaderCardSkill.SkillTree> propertyTrees = cfgLeaderCardSkill.getSkillTrees().stream().filter(p -> p.getProperty() == property).collect(Collectors.toList());
        List<RDLeaderCardSkillTree.SkillState> rdList=new ArrayList<>();
        for (CfgLeaderCardSkill.SkillTree skillTree : propertyTrees) {
            UserLeaderCardSkillTree.SkillPage skillPage=userSkillTree.getPage(skillTree.getPage());
            List<RDLeaderCardSkillTree.SkillState> list = buildSkillStateList(detail, skillPage, skillTree);
            rdList.addAll(list);
        }
        sKillClassifyToRd(rdList,rd);
        return rd;
    }

    /**
     * 专属技能列表
     * @param uid
     * @return
     */
    public RDLeaderCardSkillTree listExclusiveSkills(long uid){
        RDLeaderCardSkillTree rd=RDLeaderCardSkillTree.getShowSkillInstance();
        UserLeaderCardSkillTree userSkillTree = getUserSkillTree(uid, 60);
        CfgLeaderCardSkill cfgLeaderCardSkill = LeaderCardTool.getCfgLeaderCardSkill();
        List<RDLeaderCardSkillTree.SkillState> rdList=new ArrayList<>();
        for (int skillId : userSkillTree.showAllSkills()) {
            rdList.add(RDLeaderCardSkillTree.SkillState.getActiveInstance(skillId));
        }
        for (String exclusiveSkill : cfgLeaderCardSkill.getExclusiveSkills()) {
            CfgCardSkill cardSkill = CardSkillTool.getCardSkillByName(exclusiveSkill);
            if (userSkillTree.ifOwnSkill(cardSkill.getId())){
                continue;
            }
            CfgSkillScrollLimitEntity scroll = SkillScrollTool.getExclusiveSkillScroll(cardSkill.getId());
            SkillStateEnum state=SkillStateEnum.INACTIVATED;
            if (TreasureChecker.hasTreasure(uid,scroll.getId())){
                state=SkillStateEnum.CAN_BE_ACTIVATED;
            }
            rdList.add(RDLeaderCardSkillTree.SkillState.getInstance(cardSkill.getId(),state,0,0,scroll.getId()));
        }
        sKillClassifyToRd(rdList,rd);
        return rd;
    }

    /**
     * 根据技能ID跳转 技能树页
     *
     * 优先跳转到第一个已经激活技能的页
     * @param uid
     * @param skillId
     * @param property 属性
     * @return
     */
    public RDLeaderCardSkillTree getSkillTreeInfoBySkillID(long uid,int property,int skillId){
        UserLeaderCardSkillTree tree = getUserSkillTree(uid, property);
        String skillName = CardSkillTool.getSkillNameBySkillId(skillId);
        UserLeaderCardSkillTree.SkillPage skillPage = tree.firstActivePage(skillId);
        CfgLeaderCardSkill.SkillTree cfgTree=null;
        if (skillPage==null){
            //没有已经激活的 找到第一个出现的页
            CfgLeaderCardSkill cfgLeaderCardSkill = LeaderCardTool.getCfgLeaderCardSkill();
            for (CfgLeaderCardSkill.SkillTree skillTree : cfgLeaderCardSkill.getSkillTrees()) {
                if (skillTree.getProperty()!=property) {
                    continue;
                }
                if (skillTree.getTree().get(skillName)!=null){
                    cfgTree=skillTree;
                    break;
                }
            }
            if (cfgTree==null){
                //没有技能树
                throw new ExceptionForClientTip("leader.card.skill.tree.not.find.skill",property,skillId);
            }
            skillPage=tree.getPage(cfgTree.getPage());
        }else {
            cfgTree=LeaderCardTool.getSkillTreeConfig(property,skillPage.getPage());
        }
        return buildPageSkillTreeRdInfo(uid,skillPage,cfgTree);
    }

    /**
     * 技能树翻页
     *
     * @param uid
     * @return
     */
    public RDLeaderCardSkillTree turnPageSkillTreeInfo(long uid,int property,int page){
        UserLeaderCardSkillTree tree = getUserSkillTree(uid,property);
        UserLeaderCardSkillTree.SkillPage skillPage = tree.getPage(page);
        CfgLeaderCardSkill.SkillTree cfgTree=LeaderCardTool.getSkillTreeConfig(property,page);
        return buildPageSkillTreeRdInfo(uid,skillPage,cfgTree);
    }

    /**
     * 构造技海树图返回的数据
     * @param skillPage
     * @return
     */
    private RDLeaderCardSkillTree buildPageSkillTreeRdInfo(long uid,UserLeaderCardSkillTree.SkillPage skillPage,CfgLeaderCardSkill.SkillTree skillTree){
        RDLeaderCardSkillTree rd=new RDLeaderCardSkillTree();
        UserLeaderCardSkillTreeDetail detail = getSkillTreeDetail(uid);
        List<RDLeaderCardSkillTree.SkillState> rdList= buildSkillStateList(detail,skillPage,skillTree);
        rd.setSkillTree(rdList);
        rd.setPage(skillTree.getPage());
        rd.setProperty(skillTree.getProperty());
        return rd;
    }

    /**
     * 构造返回参数：既划分技能分类和封装技能状态
     * @param detail
     * @param skillPage
     * @param skillTree
     * @return
     */
    private List<RDLeaderCardSkillTree.SkillState> buildSkillStateList(UserLeaderCardSkillTreeDetail detail,UserLeaderCardSkillTree.SkillPage skillPage,CfgLeaderCardSkill.SkillTree skillTree) {
        long uid=detail.getGameUserId();
        List<RDLeaderCardSkillTree.SkillState> rdList=new ArrayList<>();
        for (Map.Entry<String, String> entry : skillTree.getTree().entrySet()) {
            CfgCardSkill cfgSkill=CardSkillTool.getCardSkillByName(entry.getKey());
            int skillId=cfgSkill.getId();
            Optional<RDLeaderCardSkillTree.SkillState> optional = rdList.stream().filter(p -> p.getSkillId() == skillId && p.hasActive()).findFirst();
            if (optional.isPresent()){
                continue;
            }
            if (skillPage.ifOwnSkill(skillId)) {
                //已激活
                rdList.add(RDLeaderCardSkillTree.SkillState.getActiveInstance(skillId));
                continue;
            }
            //未激活
            CfgSkillScrollLimitEntity scrollEntity = SkillScrollTool.getSkillScrollBySkillId(skillId, skillTree.getProperty());
            int scrollId=scrollEntity.getId();
            //点亮前置条件，且背包有技能卷轴可使用(或已经使用过该卷轴)，视为可激活
            boolean usedScroll = detail.ifUseScroll(scrollEntity.getId());
            boolean hasScroll = TreasureChecker.hasTreasure(uid,scrollEntity.getId());
            //卷轴条件 满足
            boolean scrollOk= usedScroll || hasScroll;
            boolean canBeActive=scrollOk;
            int nodes = 0;
            int activeNodes= 0;
            if (StrUtil.isNotNull(entry.getValue())){
                //需要前置条件 则判断前置条件
                String[] skillCondition = entry.getValue().split(",");
                for (String name : skillCondition) {
                    CfgCardSkill cfgCardSkill = CardSkillTool.getCardSkillByName(name);
                    if (!skillPage.ifOwnSkill(cfgCardSkill.getId())){
                        //前置条件不满足
                        canBeActive=false;
                    }else {
                        activeNodes++;
                    }
                }
                nodes = skillCondition.length;
            }
            SkillStateEnum stateEnum=canBeActive?SkillStateEnum.CAN_BE_ACTIVATED:SkillStateEnum.INACTIVATED;
            rdList.add(RDLeaderCardSkillTree.SkillState.getInstance(skillId,stateEnum, nodes, activeNodes, usedScroll ? 0 : scrollId));
        }
        return rdList;
    }

        /**
         * 激活技能
         * 步骤：先判断技能激活的前置条件=》判断是否需要消耗卷轴=》需要则判断是否消耗成功对应的卷轴=》完成
         * @param uid
         * @param property 属性
         * @param skillId 技能ID
         * @return
         */
    public RDLeaderCardSkillTree activeSkill(long uid,int property,Integer page,int skillId){
        if (property==60){
            return activeExclusiveSkill(uid,skillId);
        }
        CfgLeaderCardSkill.SkillTree treeConfig = LeaderCardTool.getSkillTreeConfig(property, page);
        String skillName = CardSkillTool.getSkillNameBySkillId(skillId);
        String condition = treeConfig.getTree().get(skillName);
        UserLeaderCardSkillTree tree = getUserSkillTree(uid, property);
        if (tree.ifOwnSkill(skillId,page)){
            //激活过了
            throw new ExceptionForClientTip("leader.card.skill.exist");
        }
        if (!StrUtil.isNull(condition)){
            //需要前置条件
            String[] skillCondition = condition.split(",");
            for (String name : skillCondition) {
                CfgCardSkill cfgCardSkill = CardSkillTool.getCardSkillByName(name);
                if (!tree.ifOwnSkill(page,cfgCardSkill.getId())){
                    throw new ExceptionForClientTip("leader.card.skill.not.active.pre.skill",name);
                }
            }
        }
        CfgSkillScrollLimitEntity scroll = SkillScrollTool.getSkillScrollBySkillIdAndProperty(skillId, property);
        if (scroll==null){
            scroll=SkillScrollTool.getSecretSkillScrollBySkillId(skillId);
        }
        //判断是否需要消耗卷轴：既卷轴是否已经使用过
        RDLeaderCardSkillTree rd = new RDLeaderCardSkillTree();
        UserLeaderCardSkillTreeDetail detail = getSkillTreeDetail(uid);
        if (!detail.ifUseScroll(scroll.getId())){
            //未使用过 则需要消耗卷轴
            TreasureChecker.checkIsEnough(scroll.getId(),1,uid);
            TreasureEventPublisher.pubTDeductEvent(uid, scroll.getId(), 1, WayEnum.LEADER_CARD_ACTIVE_SKILL,rd);
            // 激活成功 并记录
            detail.addUseScroll(scroll.getId());
            gameUserService.updateItem(detail);
        }
        tree.addSkill(page,skillId);
        gameUserService.updateItem(tree);
        if (treeConfig.getTree().size()==tree.getPage(page).getOwnSkills().size()){
            //激活完整的技能树
            LeaderCardEventPublisher.pubLeaderCardActiveSkillTreeEvent(new BaseEventParam(uid));
        }
        return rd;
    }

    /**
     * 激活专属技能
     * @param uid
     * @param skillId
     * @return
     */
    public RDLeaderCardSkillTree activeExclusiveSkill(long uid,int skillId){
        UserLeaderCardSkillTree userSkillTree = getUserSkillTree(uid, 60);
        if (userSkillTree.ifOwnSkill(skillId)){
            //激活过了
            throw new ExceptionForClientTip("leader.card.skill.exist");
        }
        CfgSkillScrollLimitEntity scroll = SkillScrollTool.getExclusiveSkillScroll(skillId);
        //判断是否需要消耗卷轴：既卷轴是否已经使用过
        RDLeaderCardSkillTree rd = new RDLeaderCardSkillTree();
        UserLeaderCardSkillTreeDetail detail = getSkillTreeDetail(uid);
        TreasureChecker.checkIsEnough(scroll.getId(),1,uid);
        TreasureEventPublisher.pubTDeductEvent(uid, scroll.getId(), 1, WayEnum.LEADER_CARD_ACTIVE_SKILL,rd);
        // 激活成功 并记录
        detail.addUseScroll(scroll.getId());
        gameUserService.updateItem(detail);
        userSkillTree.addSkill(0,skillId);
        gameUserService.updateItem(userSkillTree);
        return rd;
    }
    /**
     * 获取属性对应的技能树
     * @param uid
     * @param property
     * @return
     */
    public UserLeaderCardSkillTree getUserSkillTree(long uid,int property){
        List<UserLeaderCardSkillTree> trees = getAllUserSkillTrees(uid);
        if (ListUtil.isNotEmpty(trees)){
            Optional<UserLeaderCardSkillTree> optional = trees.stream().filter(p -> p.getProperty() == property).findFirst();
            if (optional.isPresent()){
                return optional.get();
            }
        }
        UserLeaderCardSkillTree skillTree=new UserLeaderCardSkillTree();
        skillTree.setGameUserId(uid);
        skillTree.setProperty(property);
        skillTree.setId(ID.INSTANCE.nextId());
        gameUserService.addItem(uid,skillTree);
        return skillTree;
    }

    /**
     * 获取所有技能树
     * @param uid
     * @return
     */
    public List<UserLeaderCardSkillTree> getAllUserSkillTrees(long uid){
        return gameUserService.getMultiItems(uid,UserLeaderCardSkillTree.class);
    }

    /**
     * 获取技能书消耗
     * @param uid
     * @return
     */
    public UserLeaderCardSkillTreeDetail getSkillTreeDetail(long uid){
        UserLeaderCardSkillTreeDetail detail = gameUserService.getSingleItem(uid,UserLeaderCardSkillTreeDetail.class);
        if (detail == null){
            detail=new UserLeaderCardSkillTreeDetail();
            detail.setGameUserId(uid);
            detail.setId(ID.INSTANCE.nextId());
            gameUserService.addItem(uid,detail);
        }
        return detail;
    }

    /**
     * 技能分类
     * 上场技能，法术技能，死亡技能、防御技能和特殊技能（如：飞行，疾驰、影随等）。
     * @param rd
     * @param list
     */
    private void sKillClassifyToRd(List<RDLeaderCardSkillTree.SkillState> list,RDLeaderCardSkillTree rd){
        Map<LeaderCardSkillGroupEnum, List<Integer>> skillGroups = CardSkillTool.getLeaderCardSkillGroups();
        for (Map.Entry<LeaderCardSkillGroupEnum, List<Integer>> entry : skillGroups.entrySet()) {
            switch (entry.getKey()){
                case INTO_PLAYING:
                    rd.setDeploySkills(list.stream().filter(p->entry.getValue().contains(p.getSkillId())).collect(Collectors.toList()));
                    break;
                case MAGIC:
                    rd.setMagicSkill(list.stream().filter(p->entry.getValue().contains(p.getSkillId())).collect(Collectors.toList()));
                    break;
                case ATTACK:
                    rd.setAttackSkill(list.stream().filter(p->entry.getValue().contains(p.getSkillId())).collect(Collectors.toList()));
                    break;
                case DEFENSE:
                    rd.setDefenseSkill(list.stream().filter(p->entry.getValue().contains(p.getSkillId())).collect(Collectors.toList()));
                    break;
                case DEAD:
                    rd.setDieSkill(list.stream().filter(p->entry.getValue().contains(p.getSkillId())).collect(Collectors.toList()));
                    break;
                default:
                    rd.setOtherSkill(list.stream().filter(p->entry.getValue().contains(p.getSkillId())).collect(Collectors.toList()));
                    break;
            }
        }
    }

    /**
     * 加入初始化的专属技能
     * @param uid
     * @param skillId
     */
    public void addInitExclusiveSkill(long uid,int skillId){
        UserLeaderCardSkillTree skillTree = getUserSkillTree(uid, 60);
        UserLeaderCardSkillTree.SkillPage skillPage=new UserLeaderCardSkillTree.SkillPage(0,new ArrayList<>());
        if (skillTree.getOwnSkillPages().size()!=0){
            skillPage=skillTree.getOwnSkillPages().get(0);
        }else {
            skillTree.getOwnSkillPages().add(skillPage);
        }
        if (skillPage.getOwnSkills().isEmpty()){
            skillPage.getOwnSkills().add(skillId);
        }else {
            skillPage.getOwnSkills().set(0,skillId);
        }
        gameUserService.updateItem(skillTree);
    }
}
