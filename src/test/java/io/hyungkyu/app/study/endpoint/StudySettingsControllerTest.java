package io.hyungkyu.app.study.endpoint;

import io.hyungkyu.app.WithAccount;
import io.hyungkyu.app.account.domain.entity.Account;
import io.hyungkyu.app.account.infra.repository.AccountRepository;
import io.hyungkyu.app.study.application.StudyService;
import io.hyungkyu.app.study.domain.entity.Study;
import io.hyungkyu.app.study.form.StudyForm;
import io.hyungkyu.app.study.infra.repository.StudyRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class StudySettingsControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    StudyRepository studyRepository;
    @Autowired
    StudyService studyService;
    private final String studyPath = "study-test";

    @BeforeEach
    void beforeEach() {
        Account account = accountRepository.findByNickname("gudrb");
        studyService.createNewStudy(StudyForm.builder()
                .path(studyPath)
                .shortDescription("short-description")
                .fullDescription("full-description")
                .title("title")
                .build(), account);
    }

    @AfterEach
    void afterEach() {
        studyRepository.deleteAll();
    }

    @Test
    @DisplayName("스터디 세팅 폼 조회")
    @WithAccount("gudrb")
    void studyForm() throws Exception {
        mockMvc.perform(get("/study/" + studyPath + "/settings/description"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/settings/description"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("studyDescriptionForm"));
    }

    @Test
    @DisplayName("스터디 세팅 수정: 정상")
    @WithAccount("gudrb")
    void createStudy() throws Exception {
        Account account = accountRepository.findByNickname("gudrb");
        String shortDescriptionToBeUpdated = "short-description-test";
        String fullDescriptionToBeUpdated = "full-description-test";
        mockMvc.perform(post("/study/" + studyPath + "/settings/description")
                        .param("shortDescription", shortDescriptionToBeUpdated)
                        .param("fullDescription", fullDescriptionToBeUpdated)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + studyPath + "/settings/description"));
        Study study = studyService.getStudy(account, studyPath);
        assertEquals(shortDescriptionToBeUpdated, study.getShortDescription());
        assertEquals(fullDescriptionToBeUpdated, study.getFullDescription());
    }

    @Test
    @DisplayName("스터디 세팅 폼 조회(배너)")
    @WithAccount("gudrb")
    void studySettingFormBanner() throws Exception {
        mockMvc.perform(get("/study/" + studyPath + "/settings/banner"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/settings/banner"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"));
    }

    @Test
    @DisplayName("스터디 배너 업데이트")
    @WithAccount("gudrb")
    void updateStudyBanner() throws Exception {
        mockMvc.perform(post("/study/" + studyPath + "/settings/banner")
                        .param("image", "image-test")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + studyPath + "/settings/banner"));
    }

    @Test
    @DisplayName("스터디 배너 사용")
    @WithAccount("gudrb")
    void enableStudyBanner() throws Exception {
        mockMvc.perform(post("/study/" + studyPath + "/settings/banner/enable")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + studyPath + "/settings/banner"));
        Study study = studyRepository.findByPath(studyPath);
        assertTrue(study.useBanner());
    }

    @Test
    @DisplayName("스터디 배너 미사용")
    @WithAccount("gudrb")
    void disableStudyBanner() throws Exception {
        mockMvc.perform(post("/study/" + studyPath + "/settings/banner/disable")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + studyPath + "/settings/banner"));
        Study study = studyRepository.findByPath(studyPath);
        assertFalse(study.useBanner());
    }
}