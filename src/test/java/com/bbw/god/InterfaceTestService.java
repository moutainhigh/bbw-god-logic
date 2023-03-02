package com.bbw.god;

import com.alibaba.fastjson.JSONObject;
import com.bbw.BaseTest;
import com.bbw.common.JSONUtil;
import com.bbw.god.rd.RDSuccess;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;


/**
 * 接口测试服务
 *
 * @author fzj
 * @date 2022/6/20 16:33
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration
@AutoConfigureMockMvc
public class InterfaceTestService extends BaseTest {
    @Autowired
    WebApplicationContext context;
    MockMvc mockMVC;

    private static String TOKEN = "";
    /** 账号 */
    private static final String ACCOUNT = "zftest3";
    /** 密码 */
    private static final String PASSWORD = "123456";
    /** 区服 */
    private static final String SERVER = "96";

    private void login() throws Exception {
        mockMVC = MockMvcBuilders.webAppContextSetup(context).build();
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.get("/account!login").header("Origin", "1")
                .param("userType", "10")
                .param("email", ACCOUNT)
                .param("password", PASSWORD)
                .param("serverId", SERVER);
        MvcResult mvcResult = mockMVC.perform(mockHttpServletRequestBuilder).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject parseResult = (JSONObject) JSONObject.parse(contentAsString);
        TOKEN = (String) parseResult.get("token");
    }


    /**
     * get请求测试
     *
     * @param url        请求地址
     * @param parameters 参数
     * @return
     * @throws Exception
     */
    public <T extends RDSuccess> T commonGetMethodTest(String url, Map<String, String> parameters, Class<T> Object, boolean isNeedLogin) throws Exception {
        //登陆
        if (isNeedLogin) {
            login();
        }
        //接口调用
        StringBuilder urlBuilder = new StringBuilder(url + "?");
        for (Map.Entry<String, String> parameter : parameters.entrySet()) {
            urlBuilder.append(parameter.getKey()).append("=").append(parameter.getValue()).append("&");
        }
        url = urlBuilder.toString();
        MvcResult mvcResult = mockMVC.perform(MockMvcRequestBuilders.get("/" + url)
                .header("token", TOKEN).header("Origin", "1")
                .param("tk", String.valueOf(System.currentTimeMillis())))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        return JSONUtil.fromJson(contentAsString, Object);
    }
}