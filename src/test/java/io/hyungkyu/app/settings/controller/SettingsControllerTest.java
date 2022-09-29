package io.hyungkyu.app.settings.controller;

import io.hyungkyu.app.WithAccount;
import io.hyungkyu.app.account.domain.entity.Account;
import io.hyungkyu.app.account.infra.repository.AccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    AccountRepository accountRepository;

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("프로필 수정: 입력값 정상")
    @WithAccount("gudrb")
    void updateProfile() throws Exception {
        String bio = "한 줄 소개";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(flash().attributeExists("message"));
        Account gudrb = accountRepository.findByNickname("gudrb");
        assertEquals(bio, gudrb.getProfile().getBio());
    }

    @Test
    @DisplayName("프로필 수정: 입력값 에러")
    @WithAccount("gudrb")
    void updateProfileWithError() throws Exception {
        String bio = "35자 넘으면에러35자 넘으면에러35자 넘으면에러35자 넘으면에러";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
        Account gudrb = accountRepository.findByNickname("gudrb");
        assertNull(gudrb.getProfile().getBio());
    }

    @Test
    @DisplayName("프로필 조회")
    @WithAccount("gudrb")
    void updateProfileForm() throws Exception {
        String bio = "한 줄 소개";
        mockMvc.perform(get(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }
}