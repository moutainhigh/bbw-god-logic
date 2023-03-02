package com.bbw.god.gm;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.bbw.common.Rst;
import com.bbw.god.db.entity.UserSpecialCardEntity;
import com.bbw.god.db.service.UserSpecialCardService;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author：lwb
 * @date: 2020/12/2 16:25
 * @version: 1.0
 */
@RequestMapping("gm/specialCard")
@RestController
public class GMUserSpecialCardCtrl {
    @Autowired
    private UserSpecialCardService userSpecialCardService;
    @Autowired
    private GameUserService gameUserService;
    @RequestMapping("/repairUserInfo")
    public Rst repairUserInfo(){
        EntityWrapper<UserSpecialCardEntity> ew=new EntityWrapper<>();
        ew.isNull("head");
        List<UserSpecialCardEntity> entities = userSpecialCardService.selectList(ew);
        for (UserSpecialCardEntity entity:entities){
            Long uid = entity.getUid();
            GameUser gu = gameUserService.getGameUser(uid);
            entity.setHead(gu.getRoleInfo().getHead());
            entity.setIcon(gu.getRoleInfo().getHeadIcon());
        }
        userSpecialCardService.updateBatchById(entities,entities.size());
        return Rst.businessOK();
    }
}
