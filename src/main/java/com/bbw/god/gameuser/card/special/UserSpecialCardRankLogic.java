package com.bbw.god.gameuser.card.special;

import com.bbw.common.JSONUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.db.entity.UserSpecialCardRankEntity;
import com.bbw.god.detail.async.UserSpecialCardRankAsyncHandler;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.wanxianzhen.WanXianCard;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.leadercard.LeaderCardTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：lwb
 * @date: 2020/12/2 14:32
 * @version: 1.0
 */
@Slf4j
@Service
public class UserSpecialCardRankLogic {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserSpecialCardRankAsyncHandler userSpecialCardRankAsyncHandler;

    /**
     * 记录改过技能的卡牌在万仙阵中的名次
     *
     * @param uid
     * @param rank
     * @param type
     * @param cards
     */
    public void logSpecialCardRankByWanXian(Long uid, int rank, int type, List<WanXianCard> cards) {
        try {
            if (uid < 0 || ListUtil.isEmpty(cards)) {
                return;
            }
            List<SpecialCardVO> list=new ArrayList<>();
            for (WanXianCard wanXianCard : cards) {
                if (wanXianCard==null){
                    return;
                }
                list.add(SpecialCardVO.instanceByWanXianCard(wanXianCard));
            }
            GameUser gu = gameUserService.getGameUser(uid);
            String cardGroupJson= JSONUtil.toJson(list);
            for (SpecialCardVO cardVO : list) {
                if (cardVO.getIsUseSkillScroll()!=null && cardVO.getIsUseSkillScroll()==1){
                    //换过技能
                    log(gu,rank,cardVO,type,cardGroupJson);
                }
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }

    private void log( GameUser gu,int rank,SpecialCardVO vo,int type,String cardGroupJson){
        if (LeaderCardTool.getLeaderCardId() == vo.getId()){
            return;
        }
        CfgCardEntity cfgCardEntity = CardTool.getCardById(vo.getId());
        int skill0=vo.getSkill0().equals(cfgCardEntity.getZeroSkill())?0:vo.getSkill0();
        int skill5=vo.getSkill5().equals(cfgCardEntity.getFiveSkill())?0:vo.getSkill5();
        int skill10=vo.getSkill10().equals(cfgCardEntity.getTenSkill())?0:vo.getSkill10();
        if (skill0==0 && skill5==0 && skill10==0){
            return;
        }
        UserSpecialCardRankEntity entity=UserSpecialCardRankEntity.instance(vo.getId(),gu);
        entity.setCardName(cfgCardEntity.getName());
        entity.setFightRank(rank);
        entity.setSkill0(skill0);
        entity.setSkill5(skill5);
        entity.setSkill10(skill10);
        entity.setCardGroup(cardGroupJson);
        entity.setFightType(type);
        userSpecialCardRankAsyncHandler.log(entity);
    }
}
