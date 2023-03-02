package com.bbw;

import com.bbw.god.game.CR;
import com.bbw.god.login.LoginVO;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.lang.reflect.Field;
import java.util.Arrays;

public class MockMvcTest extends BaseTest {
    @Autowired
    private WebApplicationContext wac;
    private static MockMvc MVC;
    private static MockHttpSession SESSION;

    @Before
    public void beforeTest() {
        if (MVC == null) {
            MVC = MockMvcBuilders.webAppContextSetup(this.wac).build();
            SESSION = new MockHttpSession();
        }
    }

    @Test
    public void login() {
        LoginVO loginVO = new LoginVO();
        loginVO.setEmail("sdsa@qq.com");
        loginVO.setPassword("123456");
        loginVO.setPlat("1");
        loginVO.setServerId(SERVER);
        loginVO.setUserType(10);
        request("account!login", loginVO);
    }

    @Test()
    public void listHead() {
        request(CR.GameUser.LIST_HEAD);
    }

    private void request(String uri, Object... params) {
        try {
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/" + uri);
            if (params.length > 0) {
                requestBuilder.params(toUriParams(params[0]));
            }
            ResultActions resultActions = MVC.perform(requestBuilder.accept(MediaType.APPLICATION_JSON_UTF8).session(SESSION));
            resultActions.andExpect(MockMvcResultMatchers.status().isOk());
            String result = resultActions.andReturn().getResponse().getContentAsString();
            System.out.println(uri + "请求结果：" + result);
//        resultActions.andDo(MockMvcResultHandlers.print());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private MultiValueMap<String, String> toUriParams(Object vo) throws IllegalAccessException {
        Field[] fields = vo.getClass().getDeclaredFields();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            String paramName = fields[i].getName();
            String paramValue = fields[i].get(vo).toString();
            params.put(paramName, Arrays.asList(paramValue));
//            System.out.println(paramName + "," + paramValue);
        }
        return params;
    }
}