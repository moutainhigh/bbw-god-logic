package com.bbw.common;

import com.bbw.BaseTest;
import org.junit.Assert;
import org.junit.Test;

public class SensitiveWordUtilTest extends BaseTest {

    @Test
    public void check() {
        Assert.assertEquals(false, SensitiveWordUtil.isNotPass("李强", 80, "o2o155KIk_MPSpiqL8l-B6K0Zw_U"));
        Assert.assertEquals(true, SensitiveWordUtil.isNotPass("李KE强", 80, "o2o155KIk_MPSpiqL8l-B6K0Zw_U"));
    }
}