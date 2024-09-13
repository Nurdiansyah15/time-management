package com.tunduh.timemanagement.timemanagement;

import com.tunduh.timemanagement.controller.AuthControllerTest;
import com.tunduh.timemanagement.service.AuthServiceTest;
import com.tunduh.timemanagement.service.CustomOAuth2UserServiceTest;
import com.tunduh.timemanagement.security.OAuth2AuthenticationSuccessHandlerTest;
import com.tunduh.timemanagement.integration.AuthIntegrationTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        AuthControllerTest.class,
        AuthServiceTest.class,
        CustomOAuth2UserServiceTest.class,
        OAuth2AuthenticationSuccessHandlerTest.class,
        AuthIntegrationTest.class
})
public class AllTests {
}