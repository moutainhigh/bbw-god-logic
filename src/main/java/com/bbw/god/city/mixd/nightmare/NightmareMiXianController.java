package com.bbw.god.city.mixd.nightmare;

import com.bbw.common.StrUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.city.mixd.nightmare.pos.CengZhuProcessor;
import com.bbw.god.city.mixd.nightmare.pos.FurnaceProcessor;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-05-27
 */
@RestController
public class NightmareMiXianController extends AbstractController {
    @Autowired
    private FurnaceProcessor furnaceService;
    @Autowired
    private NightmareMiXianLogic nightmareMiXianLogic;
    @Autowired
    private CengZhuProcessor cengZhuProcessor;
    /**
     * 进入迷仙洞
     * @return
     */
    @RequestMapping(CR.MiXianDong.INTO)
    public RDNightmareMxd into(){
        return nightmareMiXianLogic.intoMxd(getUserId());
    }

    /**
     * 进入宝库
     * @return
     */
    @RequestMapping(CR.MiXianDong.INTO_TREASURE_HOUSE)
    public RDNightmareMxd intoTreasureHouse(){
        return nightmareMiXianLogic.intoMxdTreasureHouse(getUserId());
    }
    /**
     * 到达格子
     * @param pos
     * @param passPath 历史路径: p1,p2,p3,p4;
     * @return
     */
    @RequestMapping(CR.MiXianDong.TOUCH_POS)
    public RDNightmareMxd touchPos(int pos,String passPath){
        RDNightmareMxd nightmareMxd= nightmareMiXianLogic.touchPos(getUserId(),pos,passPath);
        return nightmareMxd;
    }

    /**
     * 熔炼
     * @return
     */
    @RequestMapping(CR.MiXianDong.SMELT)
    public RDNightmareMxd smelt(){
        return furnaceService.smelt(getUserId());
    }

    /**
     * 保存卡组
     * @param isLevelOwner  是否是层主
     * @param cards  id1;id2;
     * @return
     */
    @RequestMapping(CR.MiXianDong.SAVE_CARDS)
    public RDNightmareMxd saveCards(@RequestParam(defaultValue = "0") Integer isLevelOwner, String cards){
        if (StrUtil.isBlank(cards)) {
            throw new ExceptionForClientTip("mxd.cant.save.empty.cardgroup");
        }
        Set<Integer> cardIds = new HashSet<>();
        for (String str : cards.split(";")) {
            cardIds.add(Integer.parseInt(str));
        }
        //卡牌数量检测
        if (cardIds.size() < NightmareMiXianTool.getLevelOwnerMinCardNum()) {
            throw new ExceptionForClientTip("mxd.cardgoup.num.cannot");
        }
        if (isLevelOwner == 1) {
            return cengZhuProcessor.saveLevelOwnerCards(getUserId(), cardIds.stream().collect(Collectors.toList()));
        }
        return nightmareMiXianLogic.saveFightCards(getUserId(), cardIds.stream().collect(Collectors.toList()));
    }


    @RequestMapping(CR.MiXianDong.NEXT)
    public RDNightmareMxd next(){
        return nightmareMiXianLogic.nextLevel(getUserId());
    }

    /**
     * 退出迷仙洞
     * @param
     * @return
     */
    @RequestMapping(CR.MiXianDong.CLOSE)
    public RDNightmareMxd close(@RequestParam(defaultValue = "0") Integer isGiveup){
        if (isGiveup==1){
            return nightmareMiXianLogic.toGiveUp(getUserId());
        }
        RDNightmareMxd rd = new RDNightmareMxd();
        nightmareMiXianLogic.toPassLevel(getUserId(),rd);
        return rd;
    }

    @RequestMapping(CR.MiXianDong.RESET)
    public RDNightmareMxd reset(){
        return nightmareMiXianLogic.reset(getUserId());
    }
}
