package com.bbw.god.db.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

/**
 * @author lwb
 * @date 2020/5/26 15:31
 */
@Data
@TableName("wanxian_user_cards")
public class WanXianUserCardsEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Long uid;
    private Integer lv;
    private String cards;
    private Integer season;
    private Integer wxtype;
    private Integer gid;

    public static WanXianUserCardsEntity instance(long uid,int lv,String cards,int season,int type){
        WanXianUserCardsEntity entity=new WanXianUserCardsEntity();
        entity.setCards(cards);
        entity.setLv(lv);
        entity.setUid(uid);
        entity.setSeason(season);
        entity.setWxtype(type);
        return entity;
    }
}
