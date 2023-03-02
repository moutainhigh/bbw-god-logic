package com.bbw.god.game.config;

import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2019年10月11日 上午10:53:20
 * 类说明 随机昵称
 */
@Data
public class CfgRandomName implements CfgInterface, CfgPrepareListInterface {
    private String key;
    private String word;
    private String surName;

    private List<String> surNames;
    private List<String> words;

    @Override
    public void prepare() {
        String[] surnameArray = surName.split(",");
        surNames = Arrays.asList(surnameArray);

        String[] wordArray = word.split(",");
        words = Arrays.asList(wordArray);
    }

    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 1;
    }

}
