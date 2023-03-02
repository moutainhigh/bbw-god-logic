package com.bbw.god.city.miaoy.hexagram;

import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HexagramController extends AbstractController {
    @Autowired
    private HexagramBuffService hexagramBuffService;

    /**
     *
     * 获取当前卦象信息
     * @return
     */
    @GetMapping(CR.Hexagram.GET_BUFF_INFO)
    public RDHexagram getHexagramBUffInfo(){
        return hexagramBuffService.getHexagramBuffInfo(getUserId());
    }

    /**
     * 抽卦
     * @return
     */
    @GetMapping(CR.Hexagram.GET_HEXAGRAM)
    public RDHexagram getHexagram(){
        return hexagramBuffService.getHexagram(getUserId());
    }

}
