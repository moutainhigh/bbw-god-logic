package com.bbw.god.city.miaoy.hexagram;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.city.CityChecker;
import com.bbw.god.city.miaoy.MiaoYProcessor;
import com.bbw.god.city.miaoy.hexagram.event.EPHexagram;
import com.bbw.god.city.miaoy.hexagram.event.HexagramEventPublisher;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTypeEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.mc.m2c.M2cService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


/**
 * 卦象BUFF
 * @author liuwenbin
 */
@Service
public class HexagramBuffService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private HexagramFactory hexagramFactory;
    @Autowired
    private MiaoYProcessor miaoYProcessor;
    @Autowired
    private M2cService m2cService;

    public Optional<UserHexagramBuff> getHexagramBuff(long uid){
        UserHexagramBuff hexagramBuff = gameUserService.getSingleItem(uid, UserHexagramBuff.class);
        if (hexagramBuff!=null){
            return Optional.of(hexagramBuff);
        }
        return Optional.empty();
    }
    /**
     * 是否含有某个卦象BUFF
     * @param uid
     * @param hexagramId
     * @return
     */
    public boolean isHexagramBuff(long uid,int hexagramId){
        Optional<UserHexagramBuff> optional = getHexagramBuff(uid);
        if (optional.isPresent()){
            UserHexagramBuff hexagramBuff = optional.get();
            return hexagramBuff.getHexagramId()==hexagramId && hexagramBuff.ifActive();
        }
        return false;
    }

    /**
     * 是否含有原地不动的BUFF
     * @param uid
     * @return
     */
    public Integer hasStandHexagramBuff(long uid){
        Optional<UserHexagramBuff> optional = getHexagramBuff(uid);
        if (optional.isPresent()){
            UserHexagramBuff hexagramBuff = optional.get();
            if (HexagramBuffEnum.isStand(hexagramBuff.getHexagramId())){
                return hexagramBuff.getHexagramId();
            }
        }
        return null;
    }

    /**
     * 添加新的卦象buff
     * @param uid
     * @param hexagramId
     */
    public void addHexagramBuff(long uid,int hexagramId,int effectTimes){
        Optional<UserHexagramBuff> optional = getHexagramBuff(uid);
        UserHexagramBuff hexagramBuff = null;
        if (optional.isPresent()){
            hexagramBuff = optional.get();
            hexagramBuff.resetBuff(hexagramId,effectTimes);
            gameUserService.updateItem(hexagramBuff);
            return;
        }
        hexagramBuff=UserHexagramBuff.getInstance(uid,hexagramId,effectTimes);
        gameUserService.addItem(uid,hexagramBuff);
    }

    /**
     * 获取当前BUff信息
     * @param uid
     * @return
     */
    public RDHexagram getHexagramBuffInfo(long uid){
        RDHexagram rd=new RDHexagram();
        Optional<UserHexagramBuff> optional = getHexagramBuff(uid);
        int hexagramId=0;
        int times=0;
        if (optional.isPresent()){
            hexagramId=optional.get().getHexagramId();
            times=optional.get().getEffectTimes();
        }
        rd.setCurrentHexagram(new RDHexagram.BuffInfo(hexagramId,times));
        return rd;
    }

    /**
     * 抽卦
     * @param uid
     * @return
     */
    public RDHexagram getHexagram(long uid){
        GameUser gu = gameUserService.getGameUser(uid);
        CfgCityEntity city = gu.gainCurCity();
        // 是否在城市中
        CityChecker.checkIsCity(city, CityTypeEnum.MY);
        miaoYProcessor.checkIsHandle(gu, null);
        RDHexagram rd = new RDHexagram();
        if (!gu.getStatus().ifNotInFsdlWorld()) {
            throw new ExceptionForClientTip("hexagram.cant.do");
        }
        ResChecker.checkGold(gu, 20);
        ResEventPublisher.pubGoldDeductEvent(uid, 20, WayEnum.HEXAGRAM, rd);
        deductBuffTimes(uid, 0, 0);
        miaoYProcessor.setHandleStatus(gu, null);
        if (miaoYProcessor.isActive(gu)) {
            hexagramFactory.buildRandomHexagram(gu, rd);
        } else {
            hexagramFactory.buildHexagramAsExp(gu, rd);
        }
        //保存卦象
        addHexagram(uid, rd.getHexagramId());
        return rd;
    }

    /**
     * 添加卦象（用于已点亮卦象列表展示）
     * @param uid
     * @return
     */
    public void addHexagram(long uid,int hexagramId){
        boolean isNewHexagram;
        UserHexagram userHexagram = gameUserService.getSingleItem(uid, UserHexagram.class);
        if(userHexagram == null){
            userHexagram = UserHexagram.instance(uid,hexagramId);
            gameUserService.addItem(uid,userHexagram);
            isNewHexagram = true;
        }else {
            isNewHexagram = userHexagram.addHexagram(hexagramId);
            if(isNewHexagram){
                gameUserService.updateItem(userHexagram);
            }
        }
        //发布抽卦事件
        BaseEventParam bep = new BaseEventParam(uid, WayEnum.HEXAGRAM);
        EPHexagram ep = EPHexagram.instance(bep,hexagramId,isNewHexagram);
        HexagramEventPublisher.pubHexagramEvent(ep);
    }

    /**
     * 直接设置卦
     * @param uid
     * @return
     */
    public RDHexagram setHexagram(long uid,int hexagramId){
        RDHexagram rd=new RDHexagram();
        hexagramFactory.buildHexagram(uid,hexagramId,rd);
        return rd;
    }

    /**
     * 扣除BUFF 次数
     * @param uid
     * @param hexagramId  为0时表示清除BUFF
     * @param times
     */
    public void deductBuffTimes(long uid,int hexagramId,int times){
        Optional<UserHexagramBuff> buffOp =getHexagramBuff(uid);
        if (buffOp.isPresent()){
            UserHexagramBuff buff = buffOp.get();
            if (hexagramId==0){
                //清除buff
                gameUserService.deleteItem(buff);
                m2cService.sendHexagramDoneMsg(uid);
                return;
            }
            //扣除BUFF次数
            if (buff.getHexagramId()==hexagramId){
                buff.deductTimes(times);
                if (buff.getEffectTimes()<=0){
                    gameUserService.deleteItem(buff);
                    m2cService.sendHexagramDoneMsg(uid);
                }else {
                    gameUserService.updateItem(buff);
                }
            }
        }
    }
}
