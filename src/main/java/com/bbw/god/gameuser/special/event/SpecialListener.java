package com.bbw.god.gameuser.special.event;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.special.CfgSpecialEntity;
import com.bbw.god.game.config.special.SpecialTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.special.UserPocket;
import com.bbw.god.gameuser.special.UserSpecial;
import com.bbw.god.gameuser.special.UserSpecialService;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 特产各种事件处理器
 *
 * @author suhq
 * @date 2018年10月23日 下午6:29:12
 */
@Component
public class SpecialListener {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserSpecialService userSpecialService;

    @EventListener
    public void addSpecials(SpecialAddEvent event) {
        EPSpecialAdd ep = event.getEP();
        GameUser gameUser = gameUserService.getGameUser(ep.getGuId());
        List<UserSpecial> specialsToAdd = new ArrayList<>();
        ep.getAddSpecials().forEach(ev -> {
            int specialId = ev.getSpecialId();
            int discount = ev.getDiscount();
            CfgSpecialEntity cfgSpecial = SpecialTool.getSpecialById(specialId);
            UserSpecial userSpecial = UserSpecial.fromCfgSpecial(gameUser.getId(), cfgSpecial, discount);
            specialsToAdd.add(userSpecial);
            ep.getRd().addSpecial(userSpecial);
        });
        userSpecialService.addSpecials(specialsToAdd);
    }

    @EventListener
    public void deductSpecials(SpecialDeductEvent event) {
        EPSpecialDeduct ep = event.getEP();
        List<Long> ids = new ArrayList<>();
        List<EPSpecialDeduct.SpecialInfo> specialInfoList = ep.getSpecialInfoList();
        specialInfoList.forEach(s -> ids.add(s.getUserSpecialId()));
        userSpecialService.delSpecials(ep.getGuId(), ids);
        ep.getRd().setReduceSpecial(ids);
    }

    /**
     * 锁定特产（加入口袋）
     *
     * @param event
     */
    @EventListener
    public void lockSpecials(SpecialLockEvent event) {
        EPPocketSpecial ep = event.getEP();
        Long dataId = ep.getLockSpecialId();
        int size = userSpecialService.getPocketEmptySize(ep.getGuId());
        if (size < 1) {
            //空间不足
            throw new ExceptionForClientTip("specail.pocket.full");
        }
        //加入口袋
        Optional<UserSpecial> specialOp = userSpecialService.getOwnSpecialByDataId(ep.getGuId(), dataId);
        if (!specialOp.isPresent()) {
            throw new ExceptionForClientTip("special.not.exist");
        }
        UserPocket pocketSpecial = new UserPocket(dataId, ep.getGuId());
        gameUserService.addItem(ep.getGuId(), pocketSpecial);
    }

    /**
     * 解锁特产（取出口袋）
     *
     * @param event
     */
    @EventListener
    public void unLockSpecials(SpecialUnLockEvent event) {
        EPPocketSpecial ep = event.getEP();
        long guId = ep.getGuId();
        List<Long> dataIdList = ep.getUnLockSpecialIds();
        List<UserPocket> pockets = userSpecialService.getPocketsBydataIds(guId, dataIdList);
        gameUserService.deleteItems(guId, pockets);
    }

    /**
     * 添加特产
     *
     * @param gameUser
     * @param specialId
     * @param discount
     * @param way
     * @param rd
     */
    private void addSpecial(GameUser gameUser, int specialId, int discount, WayEnum way, RDCommon rd) {

    }

}
