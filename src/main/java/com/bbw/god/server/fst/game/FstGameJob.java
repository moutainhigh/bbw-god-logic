package com.bbw.god.server.fst.game;

import com.bbw.exception.ErrorLevel;
import com.bbw.god.game.wanxianzhen.WanXianTool;
import com.bbw.mc.mail.MailAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author lwb
 * @date 2020/5/26 14:30
 */
@Slf4j
@Component("FstGameJob")
public class FstGameJob {
    @Autowired
    private FstGameService fstGameService;
    @Autowired
    private MailAction mailAction;
    /**
     * 邮件定时器
     */
    public void doPromotion() {
        String content = "跨服封神台结算定时器：\n";
        List<Integer> groupIds = WanXianTool.getOpenedServerGroups();
        long begin = System.currentTimeMillis();
        boolean error=false;
        for (Integer group:groupIds){
            try{
                String promotion = fstGameService.doPromotion(group);
                log.info(group+":"+promotion);
                content+=group+":"+promotion+"\n";
            }catch (Exception e){
                log.error(e.getMessage(),e);
                content+=group+":异常执行\n";
                error=true;
            }
        }
        log.info("----------------");
        content+="-----------------\n";
        String timeStr="执行总耗时："+(System.currentTimeMillis()-begin);
        content+="-----------------\n";
        content+=timeStr;
        log.info(timeStr);
        if (error){
            mailAction.notifyCoder(ErrorLevel.HIGH,"异常跨服封神台结算定时器结果：",content);
        }else {
            mailAction.notifyCoder("跨服封神台结算定时器结果：",content);
        }
    }
}
