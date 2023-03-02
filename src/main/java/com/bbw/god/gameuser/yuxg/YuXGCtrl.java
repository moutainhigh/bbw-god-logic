package com.bbw.god.gameuser.yuxg;

import com.bbw.common.Rst;
import com.bbw.common.SensitiveWordUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.gameuser.yuxg.rd.*;
import com.bbw.god.login.LoginPlayer;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 玉虚宫请求接口
 *
 * @author: suhq
 * @date: 2021/10/21 10:11 上午
 */
@Api(tags = {"玉虚宫接口"})
@RestController
public class YuXGCtrl extends AbstractController {
    @Autowired
    YuXGLogic yuXGLogic;

    @ApiOperation(value = "进入玉虚宫调用")
    @GetMapping(CR.YuXG.ENTER)
    public RDYuXGEnter enter() {
        return yuXGLogic.enter(getUserId());
    }

    /**
     * 祈符
     *
     * @param sparId 晶石ID
     * @return
     */
    @ApiOperation(value = "祈符")
    @GetMapping(CR.YuXG.PRAY)
    public RDYuXGPray pray(int sparId) {
        return yuXGLogic.pray(getUserId(), sparId);
    }

    /**
     * 设置许愿清单
     * @param wishingDetailed eg:1@符图id,符图id,符图id;2@符图id,符图id,符图id;3@符图id,符图id,符图id
     * @return
     */
    @ApiOperation(value = "设置许愿清单")
    @GetMapping(CR.YuXG.SET_WISHING_DETAILED)
    public RDSuccess setWishingDetailed(String wishingDetailed){
        return yuXGLogic.setWishingDetailed(getUserId(), wishingDetailed);
    }


    @ApiOperation(value = "获取许愿清单")
    @GetMapping(CR.YuXG.GET_WISHING_DETAILED)
    public RdWishingDetailed getWishingDetailed(){
        return yuXGLogic.getWishingDetailed(getUserId());
    }

    /**
     * 熔炼
     *
     * @return
     */
    @ApiOperation(value = "熔炼")
    @GetMapping(CR.YuXG.MELT)
    public RDYuXGMelt melt(String smeltGoods) {
        return yuXGLogic.melt(getUserId(), smeltGoods);
    }

    /**
     * 使用符首进行祈福
     *
     * @param fuShouId 符首id
     * @return
     */
    @ApiOperation(value = "使用符首改变符坛等级")
    @GetMapping(CR.YuXG.CHANGE_FUTAN)
    public RDYuXGPray changeFuTan(int fuShouId) {
        return yuXGLogic.changeFuTan(getUserId(), fuShouId);
    }

    /**
     * 升级符图
     *
     * @param yuSuis 玉髓id_玉髓数量,玉髓id_玉髓数量
     * @param fuTus 符图1,符图2
     * @param needUpFuTuId 需要升级的符图
     * @return
     */
    @ApiOperation(value = "升级符图")
    @GetMapping(CR.YuXG.UPDATE_RUNE)
    public RDYuXGFuTuUpdate updateFuTu(String yuSuis, String fuTus, Long needUpFuTuId) {
        return yuXGLogic.updateFuTu(getUserId(), yuSuis, fuTus, needUpFuTuId);
    }

    /**
     * 设置
     *
     * @param settings FT1_0,FT2_1
     * @return
     */
    @ApiOperation(value = "符图升级设置")
    @GetMapping(CR.YuXG.SETTING)
    public RDSuccess setting(String settings) {
        return yuXGLogic.setting(getUserId(), settings);
    }

    /**
     * 获取设置
     *
     * @return
     */
    @ApiOperation(value = "获取符图升级设置")
    @GetMapping(CR.YuXG.GET_SETTING)
    public RDYuXGSetting getSetting() {
        return yuXGLogic.getSetting(getUserId());
    }

    /**
     * 保护符图
     *
     * @param dataId 符册dataId
     * @return
     */
    @ApiOperation(value = "保护符图")
    @GetMapping(CR.YuXG.PROTECT_RUNE)
    public RDSuccess protectFuTu(Long dataId) {
        return yuXGLogic.changeFuTuStatus(getUserId(), dataId ,1);
    }

    /**
     * 取消符图保护
     *
     * @param dataId 符册dataId
     * @return
     */
    @ApiOperation(value = "取消符图保护")
    @GetMapping(CR.YuXG.UNPROTECT_RUNE)
    public RDSuccess unprotectFuTu(Long dataId) {
        return yuXGLogic.changeFuTuStatus(getUserId(), dataId, 0);
    }

    /**
     * 获取符册
     *
     * @return
     */
    @ApiOperation(value = "获取符册")
    @GetMapping(CR.YuXG.GET_RUNE_BOOKS)
    public RDYuXGFuCes getFuCes() {
        return yuXGLogic.getFuCes(getUserId());
    }

    /**
     * 解锁符册
     *
     * @return
     */
    @ApiOperation(value = "解锁符册")
    @GetMapping(CR.YuXG.UNCLOCK_RUNE_BOOK)
    public RDCommon unlockFuCe() {
        return yuXGLogic.unlockFuCe(getUserId());
    }

    /**
     * 替换符图
     *
     * @return
     */
    @ApiOperation(value = "替换符图")
    @GetMapping(CR.YuXG.REPLACE_RUNE)
    public RDFuTus replaceFuTu(int pos, Long targetRuneDataId, int fuCeId) {
        return yuXGLogic.replaceFuTu(getUserId(), pos, targetRuneDataId, fuCeId);
    }

    /**
     * 一键拆卸
     *
     * @return
     */
    @ApiOperation(value = "一键拆卸")
    @GetMapping(CR.YuXG.BATCH_REPLACE_RUNES)
    public RDFuTus batchReplaceFuTus(int fuCeId) {
        return yuXGLogic.batchReplaceFuTus(getUserId(), fuCeId);
    }

    /**
     * 编辑符册名
     *
     * @param fuCeId
     * @param name
     * @return
     */
    @ApiOperation(value = "编辑符册名")
    @GetMapping(CR.YuXG.EDIT_RUNE_BOOK_NAME)
    public RDSuccess editFuCeName(int fuCeId, String name) {
        LoginPlayer user = getUser();
        if (SensitiveWordUtil.isNotPass(name, user.getChannelId(), user.getOpenId())) {
            throw new ExceptionForClientTip("input.not.sensitive.words");
        }
        return yuXGLogic.changeFuCeName(getUserId(), fuCeId, name);
    }

    /**
     * 熔炼值获取晶石
     *
     * @return
     */
    @ApiOperation(value = "熔炼值获取晶石")
    @GetMapping(CR.YuXG.USER_MELTVALUE_GET_SPAR)
    public RDSuccess meltValueToSpar() {
        return yuXGLogic.meltValueExchangeSpar(getUserId());
    }

    /**
     * 更新祈福设置
     *
     * @param cpUserYuXGPraySeting
     * @return
     */
    @ApiOperation(value = "更新祈福设置")
    @GetMapping(CR.YuXG.UPDATE_PRAY_SETTINGS)
    public Rst updateSpecialSettings(int cpUserYuXGPraySeting) {
        return yuXGLogic.updateYuXGPraySetting(getUserId(), cpUserYuXGPraySeting);
    }
}
