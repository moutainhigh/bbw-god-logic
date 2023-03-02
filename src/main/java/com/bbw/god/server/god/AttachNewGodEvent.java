package com.bbw.god.server.god;

import com.bbw.god.event.EventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 附体新的神仙
 * 
 * @author suhq
 * @date 2019-05-23 11:51:20
 */
public class AttachNewGodEvent extends ApplicationEvent {
	private static final long serialVersionUID = 1L;
	private Integer effect;
	/**
	 * 是否允许使用神符
	 */
	private boolean canUseSSF=true;

	public AttachNewGodEvent(EventParam<ServerGod> eventParam) {
		super(eventParam);
	}

	public Integer getEffect() {
		return effect;
	}

	public void setEffect(Integer effect) {
		this.effect = effect;
	}

	public boolean isCanUseSSF() {
		return canUseSSF;
	}

	public void setCanUseSSF(boolean canUseSSF) {
		this.canUseSSF = canUseSSF;
	}
}
