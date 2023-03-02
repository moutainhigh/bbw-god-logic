package com.bbw.god.gm;

import com.bbw.common.Rst;
import com.bbw.common.StrUtil;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.card.equipment.UserCardXianJueService;
import com.bbw.god.gameuser.card.equipment.cfg.CardEquipmentAddition;
import com.bbw.god.gameuser.card.equipment.data.UserCardZhiBao;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.server.ServerUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.function.Consumer;

/**
 * 玩家卡牌至宝数据相关的操作
 *
 * @author: huanghb
 * @date: 2022/9/21 10:09
 */
@RestController
@RequestMapping("/gm")
public class GMUserCardZhiBaoCtrl extends AbstractController {
    @Autowired
    private UserGmService userGmService;
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private ServerUserService serverUserService;

    @Autowired
    private UserCardXianJueService userCardXianJueService;


    /**
     * 添加法宝
     *
     * @param sId
     * @param nickname
     * @param zhiBaoInfo
     * @return
     */
    @RequestMapping("user!addCardZhiBao")
    public Rst addTreasures(int sId, String nickname, String zhiBaoInfo) {
        if (StrUtil.isBlank(zhiBaoInfo)) {
            return null;
        }
        return addCardZhiBao(sId, nickname, zhiBaoInfo);
    }

    /**
     * 管理玩家数据
     *
     * @param sId
     * @param nickname
     * @param consumer
     * @return
     */
    private Rst manageGU(int sId, String nickname, Consumer<GameUser> consumer) {
        Optional<Long> guId = this.serverUserService.getUidByNickName(sId, nickname);
        if (!guId.isPresent()) {
            return Rst.businessFAIL("无效的账号或者区服");
        }
        GameUser gu = this.gameUserService.getGameUser(guId.get());
        consumer.accept(gu);
        return Rst.businessOK();
    }

    /**
     * 添加至宝
     *
     * @param sId
     * @param nickname
     * @param cardZhiBaoInfos 格式 "zhiBaoId,1010;property,10;10,1;20,2;30,1;40,4;skill0,0;skill1,0"
     * @return
     */
    public Rst addCardZhiBao(int sId, String nickname, String cardZhiBaoInfos) {
        return manageGU(sId, nickname, (gu) -> {
            String[] cardZhiBaosStr = cardZhiBaoInfos.split(";");
            Map<String, Integer> cardZhiBaos = new HashMap<>();
            for (String cardZhiBaoInfo : cardZhiBaosStr) {
                String[] cardZhiBao = cardZhiBaoInfo.split(",");
                cardZhiBaos.put(cardZhiBao[0], Integer.valueOf(cardZhiBao[1]));
            }
            Integer zhiBaoId = cardZhiBaos.get("zhiBaoId");
            UserCardZhiBao userCardZhiBao = UserCardZhiBao.getInstance(gu.getId(), zhiBaoId);
            Integer property = cardZhiBaos.get("property");
            userCardZhiBao.setProperty(property);
            List<CardEquipmentAddition> cardEquipmentAdditions = new ArrayList<>();
            for (int i = 1; i <= 6; i++) {
                int type = i * 10;

                int value = cardZhiBaos.getOrDefault(type + "", 0);
                if (0 == value) {
                    continue;
                }
                CardEquipmentAddition cardEquipmentAddition = new CardEquipmentAddition(type, value);
                cardEquipmentAdditions.add(cardEquipmentAddition);
            }
            userCardZhiBao.updateAdditions(cardEquipmentAdditions);
            Integer[] skillGroup = new Integer[]{0, 0};
            skillGroup[0] = cardZhiBaos.get("skill0");
            skillGroup[1] = cardZhiBaos.get("skill1");
            userCardZhiBao.setSkillGroup(skillGroup);
//            TreasureEventPublisher.pubTAddEvent(gu.getId(), evTreasures, WayEnum.NONE, rd);
            gameUserService.addItem(gu.getId(), userCardZhiBao);

        });


    }
}
