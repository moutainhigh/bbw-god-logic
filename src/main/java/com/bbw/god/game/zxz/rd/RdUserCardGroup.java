package com.bbw.god.game.zxz.rd;

import com.bbw.god.game.zxz.entity.ZxzFuTu;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.util.List;

/**
 * 返回用户卡组
 * @author: hzf
 * @create: 2022-09-23 14:48
 **/
@Data
public class RdUserCardGroup extends RDSuccess {
    /** 用户卡组 */
   private List<Integer> userCardGroup;
   /** 符册 dataId */
   private long fuCeDataId;
   /** 符图信息 */
   private List<ZxzFuTu> fuTus;
}
