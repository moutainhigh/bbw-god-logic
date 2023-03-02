package com.bbw.god.mall.processor;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.city.yed.UserAdventure;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 奇遇商店
 * @date 2020/6/2 15:49
 **/
@Service
// @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AdventureMallProcessor extends AbstractMallProcessor {

    @Autowired
    private AwardService awardService;

    AdventureMallProcessor() {
        this.mallType = MallEnum.ADVENTURE;
    }

    @Override
    public RDMallList getGoods(long guId) {
        RDMallList rd = new RDMallList();
        toRdMallList(guId, MallTool.getMallConfig().getAdventureMalls(), false, rd);
        return rd;
    }

    @Override
    public void deliver(long guId, CfgMallEntity mall, int buyNum, RDCommon rd) {
        List<UserAdventure> userAdventureList = gameUserService.getMultiItems(guId, UserAdventure.class).stream()
                .filter(ua -> null != ua.getMallId() && ua.getMallId().intValue() == mall.getId().intValue())
                .collect(Collectors.toList());
        Optional<UserAdventure> optional = userAdventureList.stream().filter(ua -> ua.isValid(ua.getType()))
                .min(Comparator.comparing(UserAdventure::getGenerateTime));
        if (!optional.isPresent()) {
            throw new ExceptionForClientTip("yeD.mall.adventure.not.exist");
        }
        UserAdventure userAdventure = optional.get();
        int num = mall.getNum() * buyNum;
        Award award = new Award(mall.getGoodsId(), AwardEnum.fromValue(mall.getItem()), num);
        String broadcastPrefix = "在" + WayEnum.YD.getName();
        awardService.fetchAward(guId, Arrays.asList(award), WayEnum.YD, broadcastPrefix, rd);
        gameUserService.deleteItem(userAdventure);
    }

    @Override
    protected List<UserMallRecord> getUserMallRecords(long guId) {
        return null;
    }

}
