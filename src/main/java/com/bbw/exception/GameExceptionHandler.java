package com.bbw.exception;

import com.bbw.common.HttpRequestUtil;
import com.bbw.common.JSONUtil;
import com.bbw.common.LM;
import com.bbw.common.Rst;
import com.bbw.god.db.entity.CfgChannelEntity;
import com.bbw.god.game.combat.CombatRedisService;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.login.LoginPlayer;
import com.bbw.mc.mail.MailAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 统一错误异常处理 启动应用后，被 @ExceptionHandler、@InitBinder、@ModelAttribute 注解的方法，都会作用在
 */
@Slf4j
@RestControllerAdvice
public class GameExceptionHandler {
    public static String REQUEST_ATTR_KEY = "user";

    @Autowired
    private MailAction notify;
    @Autowired
    public HttpServletRequest request;
    @Autowired
    private CombatRedisService combatRedisService;

    /**
     * 获得玩家信息
     *
     * @return
     */
    private String getUserInfo(){
        LoginPlayer user = (LoginPlayer) request.getAttribute(LoginPlayer.REQUEST_ATTR_KEY);
        if (null == user) {
            return "没有玩家信息";
        }
        return "账号：" + user.getAccount() + "，"
                + "区服Id：" + user.getServerId() + "，"
                + "区服：" + ServerTool.getServerShortName(user.getServerId()) + "，"
                + "uid：" + user.getUid() + "，"
                + "角色：" + user.getNickName() + "，"
                + "请求：" + request.getRequestURI();
    }

    /**
     * 组装错误信息
     * @param message
     * @return
     */
    private String packErrorMessage(String message) {
        return message + "\n" + getUserInfo();
    }

    /**
     * 给客户端返回错误提示
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = ExceptionForClientTip.class)
    public Rst sendTipToClient(ExceptionForClientTip e) {
        log.info("给客户端返回错误:" + e.getTip());
        return Rst.businessFAIL(e.getTip());
    }

    /**
     * 处理业务逻辑异常
     *
     * @param e
     * @return
     */
//    @ExceptionHandler(value = LogicException.class)
//    public Rst handlerLogicException(LogicException e) {
//        log.error(e.getMessage(), e);
//        return Rst.businessFAIL(e.getCode(), e.getMsg());
//    }

    /**
     * 校验失败
     *
     * @param exception
     * @return
     */
//    @ExceptionHandler
//    public Rst handle(ValidationException exception) {
//        if (exception instanceof ConstraintViolationException) {
//            ConstraintViolationException exs = (ConstraintViolationException) exception;
//
//            Set<ConstraintViolation<?>> violations = exs.getConstraintViolations();
//            for (ConstraintViolation<?> item : violations) {
//                /** 打印验证不通过的信息 */
//                log.error(item.getMessage());
//                return Rst.businessFAIL(item.getMessage());
//            }
//        } else {
//            log.error(exception.getMessage(), exception);
//            return Rst.businessFAIL("校验异常！");
//        }
//        return Rst.businessFAIL("校验失败！");
//    }

    /**
     * 基本异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = GodException.class)
    public void handlerGodException(GodException e) {
        String msg = packErrorMessage(e.getMessage());
        log.error(msg, e);
    }

    @ExceptionHandler(value = PayException.class)
    public Rst handlerPayException(PayException e) {
        String msg = packErrorMessage(e.getMessage());
        log.error(msg, e);
        return Rst.businessFAIL(e.getMsg());
    }

    /**
     * 程序员引起的错误
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = CoderException.class)
    public Rst handlerCoderException(CoderException e) {
        String msg = packErrorMessage(e.getMessage());
        log.error(e.getMsg() + "\t" + msg, e);

        LoginPlayer player = (LoginPlayer) request.getAttribute(LoginPlayer.REQUEST_ATTR_KEY);
        if (null == player) {
            notify.notifyCoder(e.getErrorLevel(), e.getMsg(), e.getStackMessage());
            return Rst.businessFAIL(LM.I.getMsg("server.unknow.error"));
        }

        String errorMsg = String.format("\n[%s][%s][%s]\n发起请求:%s,\n参数:%s,\n报错:%s", Cfg.I.get(player.getChannelId(), CfgChannelEntity.class).getName(), player.getNickName() + "@" + player.getUid(), ServerTool.getServer(player.getLoginSid()).getName() + "@" + player.getLoginSid(), request.getRequestURI(), HttpRequestUtil.getRequestParams(request), e.getMessage());
        Map<String, String> uniqueParams = HttpRequestUtil.getUniqueParams(request);
        if (null != uniqueParams.get("combatId")) {
            Combat combat = combatRedisService.get(Long.valueOf(uniqueParams.get("combatId")));
            notify.notifyCoder(e.getErrorLevel(), errorMsg + "，具体战斗信息请查看错误日志", e.getStackMessage());
            log.error(errorMsg + ",战斗信息:" + JSONUtil.toJson(combat));
        } else {
            notify.notifyCoder(e.getErrorLevel(), errorMsg, e.getStackMessage());
            //资源操作通知运营
            if (errorMsg.contains("增加异常")) {
                notify.notifyOperator("资源操作可能有问题", e.getMessage());
            }
            log.error(errorMsg);
        }
        //返回数据给客户端
        return Rst.businessFAIL(LM.I.getMsg("server.unknow.error"));
    }

    /**
     * 安全错误
     *
     * @param e
     * @return
     */
//    @ExceptionHandler(value = AppSecurityException.class)
//    public Rst handlerCoderException(AppSecurityException e) {
//        notify.notifyCoder(e.getSecurityLevel(), e.getMsg(), e.getStackMessage().substring(0, 500) + "\n更多堆栈前往服务器IP：" + IpUtil.getInet4Address());
//        log.error(e.getMessage(), e);
//        return Rst.businessFAIL("操作失败！");
//    }

    /**
     * BindException:处理方法参数注解校验失败的异常
     * MethodArgumentTypeMismatchException:处理请求参数类型不匹配的异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = {BindException.class, MethodArgumentTypeMismatchException.class})
    public Rst handleMethodArgumentNotValidHandler(Exception e) {
        String msg = packErrorMessage(e.getMessage());
        log.error(msg, e);
        String tipKey = "client.request.unvalid.arg";
        return Rst.businessFAIL(LM.I.getMsg(tipKey));
    }

    /**
     * 处理未知的异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    public Rst handleUnknowException(Exception e) {
        String msg = packErrorMessage(e.getMessage());
        log.error(msg, e);
        log.error(msg+"\t"+GodException.getStackMessage(e));
        String tipKey = "server.unknow.error";
        if (null != e.getMessage() && e.getMessage().contains("parameter")) {
            tipKey = "client.request.unvalid.arg";
        }
        return Rst.businessFAIL(LM.I.getMsg(tipKey));
    }
}
