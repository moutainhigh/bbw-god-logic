package com.bbw.god.game.sxdh.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2019年11月25日 上午10:31:44
 * 类说明  神仙大会仙称号变动事件
 */
@Deprecated
public class SxdhTitleChangeEvent extends ApplicationEvent implements IEventParam {

    private static final long serialVersionUID = 1L;

    public SxdhTitleChangeEvent(SxdhTitleChange source) {
        super(source);
    }

    @SuppressWarnings("unchecked")
    @Override
    public SxdhTitleChange getEP() {
        return (SxdhTitleChange) getSource();
    }

}
