package com.bbw.common;

import com.bbw.validator.wx.minigame.WXMiniGameConfig;
import com.bbw.validator.wx.minigame.WXMiniGameSensitiveWordUtil;

/**
 * 敏感字检查
 *
 * @author: suhq
 * @date: 2022/1/27 7:34 下午
 */
public class SensitiveWordUtil {

  /**
   * 敏感字检查
   *
   * @param content
   * @param channelId
   * @param wxOpenId
   * @return
   */
  public static boolean isNotPass(String content, int channelId, String wxOpenId) {
    if (WXMiniGameConfig.CHANNEL_ID == channelId && StrUtil.isNotEmpty(wxOpenId)) {
      return !WXMiniGameSensitiveWordUtil.isPass(wxOpenId, content);
    }
    return BbwSensitiveWordUtil.contains(content);
  }
}
