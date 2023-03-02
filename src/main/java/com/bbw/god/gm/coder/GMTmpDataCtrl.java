package com.bbw.god.gm.coder;

import com.bbw.common.JSONUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.Rst;
import com.bbw.common.StrUtil;
import com.bbw.god.cache.tmp.AbstractTmpData;
import com.bbw.god.cache.tmp.AbstractTmpDataRedisService;
import com.bbw.god.cache.tmp.TmpDataRedisServiceFactory;
import com.bbw.god.cache.tmp.TmpDataType;
import com.bbw.god.controller.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * 玩家数据操作接口
 *
 * @author: suhq
 * @date: 2022/11/23 3:40 下午
 */
@RestController
@RequestMapping("/gm")
public class GMTmpDataCtrl extends AbstractController {

    @Autowired
    private TmpDataRedisServiceFactory tmpDataRedisServiceFactory;

    /**
     * 查询玩家特定类型的临时数据
     *
     * @param belong
     * @param dataType
     * @param loop
     * @return
     */
    @RequestMapping("data!listTmpDatas")
    public Rst listTmpDatas(long belong, String dataType, String loop) {
        TmpDataType tmpDataType = TmpDataType.fromRedisKey(dataType);
        Optional<AbstractTmpDataRedisService> op = tmpDataRedisServiceFactory.getService(tmpDataType);
        if (!op.isPresent()) {
            return Rst.businessFAIL(dataType + "没有对应的数据服务");
        }
        AbstractTmpDataRedisService service = op.get();
        List datas = null;
        if (StrUtil.isEmpty(loop)) {
            datas = service.getDatas(belong);
        } else {
            datas = service.getDatas(belong, loop);
        }

        Rst rst = Rst.businessOK();
        if (ListUtil.isEmpty(datas)) {
            return rst;
        }
        for (Object data : datas) {
            AbstractTmpData tmpData = (AbstractTmpData) data;
            rst.put(tmpData.getId().toString(), JSONUtil.toJson(data));
        }
        return rst;
    }
}
