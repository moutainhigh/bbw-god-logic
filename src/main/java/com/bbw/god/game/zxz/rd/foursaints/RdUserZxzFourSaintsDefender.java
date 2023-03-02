package com.bbw.god.game.zxz.rd.foursaints;

import com.bbw.god.game.zxz.entity.foursaints.UserFourSaintsDefender;
import com.bbw.god.game.zxz.entity.foursaints.UserZxzFourSaintsCardGroupInfo;
import com.bbw.god.game.zxz.entity.foursaints.UserZxzFourSaintsInfo;
import com.bbw.god.game.zxz.rd.RdDefenderCardGroup;
import com.bbw.god.game.zxz.rd.RdZxzUserLeaderCard;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 返回关卡信息
 * @author: hzf
 * @create: 2022-12-28 08:59
 **/
@Data
public class RdUserZxzFourSaintsDefender extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 1366511178569740530L;
    /** 四圣挑战关卡 */
    List<RdFourSaintsDefender> fourSaintsDefenders;
    private RdDefenderCardGroup userCardGroup;
    /** 复活次数 */
    private Integer surviceTimes;
    /** 金创药使用次数 */
    private Integer jinCyTimes;


    public static RdUserZxzFourSaintsDefender instance(UserZxzFourSaintsInfo userZxzFourSaintsInfo, UserZxzFourSaintsCardGroupInfo userCardGroup,String nickname){
        RdUserZxzFourSaintsDefender rd = new RdUserZxzFourSaintsDefender();

        List<RdFourSaintsDefender> rdFourSaintsDefenders = new ArrayList<>();
        List<UserFourSaintsDefender> fourSaintsDefenders = userZxzFourSaintsInfo.getFourSaintsDefenders();
        for (UserFourSaintsDefender fourSaintsDefender : fourSaintsDefenders) {
            RdFourSaintsDefender defender = RdFourSaintsDefender.getInstance(fourSaintsDefender);
            rdFourSaintsDefenders.add(defender);
        }

        RdDefenderCardGroup cardGroup = RdDefenderCardGroup.getInstance(userCardGroup, nickname);
        rd.setJinCyTimes(userZxzFourSaintsInfo.getJinCyTimes());
        rd.setSurviceTimes(userZxzFourSaintsInfo.getSurviceTimes());
        rd.setFourSaintsDefenders(rdFourSaintsDefenders);
        rd.setUserCardGroup(cardGroup);
        return rd;
    }

    @Data
    public static class RdFourSaintsDefender{
        /** 关卡id */
        private Integer defenderId;
        /** 诛仙阵：状态信息：参考 ZxzStatusEnum 枚举类 */
        private Integer status;
        /** 诛仙阵：野怪种类信息：参考 ZxzFourSaintsDefenderKindEnum 枚举类 */
        private Integer kind;

        public static RdFourSaintsDefender getInstance(UserFourSaintsDefender fourSaintsDefender){
            RdFourSaintsDefender defender = new RdFourSaintsDefender();
            defender.setDefenderId(fourSaintsDefender.getDefenderId());
            defender.setStatus(fourSaintsDefender.getStatus());
            defender.setKind(fourSaintsDefender.getKind());
            return defender;
        }
    }

}
