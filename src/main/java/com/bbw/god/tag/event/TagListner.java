package com.bbw.god.tag.event;

import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.config.GameUserConfig;
import com.bbw.god.gameuser.level.EPGuLevelUp;
import com.bbw.god.gameuser.level.GuLevelUpEvent;
import com.bbw.god.tag.TagName;
import com.bbw.god.tag.service.AccountTagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author: suchaobin
 * @createTime: 2019-11-04 17:54
 **/
@Component
@Async
public class TagListner {
    @Autowired
    private GameUserService gameUserService;

    @Autowired
    private AccountTagsService accountTagsService;

    @EventListener
    @Order(2)
    public void addTreasure(GuLevelUpEvent event) {
        EPGuLevelUp ep = event.getEP();
        GameUser gu = gameUserService.getGameUser(ep.getGuId());
        if (ep.getNewLevel() >= GameUserConfig.bean().getSzkUnlockLevel()) {
            //玩家等级超过速战卡开放等级，添加可购买速战标签
            accountTagsService.addTag(gu.getRoleInfo().getUserName(), TagName.ABLE_BUY_FAST_FIGHT);
        }
    }
}
