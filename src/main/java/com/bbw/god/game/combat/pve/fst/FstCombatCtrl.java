package com.bbw.god.game.combat.pve.fst;

import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.server.fst.RDFst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-07-02
 */
@RestController
public class FstCombatCtrl extends AbstractController {
    @Autowired
    private FstCombatLogic fstCombatLogic;
    /**
     * 封神台战斗发起
     * @param oppId 对手ID
     * @param isGameFst 是否是跨服封神台
     * @return
     */
    @GetMapping(CR.CombatPVE.FST_ATTACK)
    public RDFst attack(Long oppId, Integer isGameFst){
        if (isGameFst!=null && isGameFst==1){
            return fstCombatLogic.doGameFst(getUserId(),oppId);
        }
        return fstCombatLogic.doServerFst(getUserId(),oppId);
    }
}
