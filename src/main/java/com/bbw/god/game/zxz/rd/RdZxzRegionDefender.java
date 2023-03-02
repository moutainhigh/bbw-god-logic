package com.bbw.god.game.zxz.rd;


import com.bbw.god.game.zxz.entity.UserZxzCard;
import com.bbw.god.game.zxz.entity.UserZxzCardGroupInfo;
import com.bbw.god.game.zxz.entity.UserZxzRegionInfo;
import com.bbw.god.rd.RDSuccess;
import com.bbw.god.game.zxz.entity.UserZxzRegionDefender;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 返回关卡数据
 * @author: hzf
 * @create: 2022-09-17 10:26
 **/
@Data
public class RdZxzRegionDefender extends RDSuccess {

     List<RdUserZxzRegionDefender> regionDefenders;
     private RdDefenderCardGroup userCardGroup;
     private Integer allPassedAwarded;


     public static RdZxzRegionDefender getInstance(UserZxzRegionInfo userRegion, UserZxzCardGroupInfo userCardGroup,Integer allPassedAwarded,String nickname) {
          RdZxzRegionDefender rd = new RdZxzRegionDefender();
          List<RdZxzRegionDefender.RdUserZxzRegionDefender> defenders = new ArrayList<>();
          for (UserZxzRegionDefender regionDefender : userRegion.getRegionDefenders()) {
              RdUserZxzRegionDefender defender = RdUserZxzRegionDefender.getInstance(regionDefender);
              defenders.add(defender);
          }

         RdDefenderCardGroup cardGroup = RdDefenderCardGroup.getInstance(userCardGroup,nickname);

          rd.setRegionDefenders(defenders);
          rd.setUserCardGroup(cardGroup);
          rd.setAllPassedAwarded(allPassedAwarded);
          return rd;
     }

     /**
      *
      */
     @Data
     public static class RdUserZxzRegionDefender{
          /** 诛仙阵：野怪种类信息：参考 ZxzDefenderKindEnum 枚举类 */
          private Integer kind;
          /** 关卡id */
          private String defenderId;
          /** 诛仙阵：状态信息：参考 ZxzStatusEnum 枚举类 */
          private Integer status;
          /** 是否领取宝箱 */
          private Integer awarded;

          public static  RdUserZxzRegionDefender getInstance(UserZxzRegionDefender regionDefender){
              RdUserZxzRegionDefender defender = new RdUserZxzRegionDefender();
              defender.setDefenderId(regionDefender.getDefenderId());
              defender.setKind(regionDefender.getKind());
              defender.setStatus(regionDefender.getStatus());
              defender.setAwarded(regionDefender.gainAwarded());
              return defender;
          }
     }
}
