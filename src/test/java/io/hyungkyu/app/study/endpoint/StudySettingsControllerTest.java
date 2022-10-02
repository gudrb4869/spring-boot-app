package io.hyungkyu.app.study.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hyungkyu.app.WithAccount;
import io.hyungkyu.app.account.domain.entity.Account;
import io.hyungkyu.app.account.domain.entity.Zone;
import io.hyungkyu.app.account.infra.repository.AccountRepository;
import io.hyungkyu.app.settings.controller.TagForm;
import io.hyungkyu.app.settings.controller.ZoneForm;
import io.hyungkyu.app.study.application.StudyService;
import io.hyungkyu.app.study.domain.entity.Study;
import io.hyungkyu.app.study.form.StudyForm;
import io.hyungkyu.app.study.infra.repository.StudyRepository;
import io.hyungkyu.app.tag.domain.entity.Tag;
import io.hyungkyu.app.tag.infra.repository.TagRepository;
import io.hyungkyu.app.zone.infra.repository.ZoneRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
    @Autowired MockMvc mockMvc;
    @Autowired AccountRepository accountRepository;
    @Autowired StudyRepository studyRepository;
    @Autowired TagRepository tagRepository;
    @Autowired ZoneRepository zoneRepository;
    @Autowired StudyService studyService;
    @Autowired ObjectMapper objectMapper;
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
    @DisplayName("스터디 세팅 폼 조회(소개)")
    @WithAccount("gudrb")
    void studySettingFormDescription() throws Exception {
        mockMvc.perform(get("/study/" + studyPath + "/settings/description"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/settings/description"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("studyDescriptionForm"));
    }

    @Test
    @DisplayName("스터디 세팅 수정: 정상")
    @WithAccount("gudrb")
    void updateStudyDescription() throws Exception {
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

    @Test
    @DisplayName("스터디 세팅 폼 조회(스터디 주제)")
    @WithAccount("gudrb")
    void studySettingFormTag() throws Exception {
        mockMvc.perform(get("/study/" + studyPath + "/settings/tags"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/settings/tags"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("tags"))
                .andExpect(model().attributeExists("whitelist"));
    }

    @Test
    @DisplayName("스터디 태그 추가")
    @WithAccount("gudrb")
    void addStudyTag() throws Exception {
        String tagTitle = "newTag";
        TagForm tagForm = TagForm.builder()
                .tagTitle(tagTitle)
                .build(); // 패키지가 달라 객체 생성이 되지 않아 TagForm에 @AllArgsConstructor, @Builder 추가
        mockMvc.perform(post("/study/" + studyPath + "/settings/tags/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk());
        Study study = studyRepository.findStudyWithTagsByPath(studyPath);
        Tag tag = tagRepository.findByTitle(tagTitle).orElse(null);
        assertNotNull(tag);
        assertTrue(study.getTags().contains(tag));
    }

    @Test
    @DisplayName("스터디 태그 삭제")
    @WithAccount("gudrb")
    void removeStudyTag() throws Exception {
        Study study = studyRepository.findStudyWithTagsByPath(studyPath);
        String tagTitle = "newTag";
        Tag tag = tagRepository.save(Tag.builder()
                .title(tagTitle)
                .build());
        studyService.addTag(study, tag);
        TagForm tagForm = TagForm.builder()
                .tagTitle(tagTitle)
                .build(); // 패키지가 달라 객체 생성이 되지 않아 TagForm에 @AllArgsConstructor, @Builder 추가
        mockMvc.perform(post("/study/" + studyPath + "/settings/tags/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk());
        assertFalse(study.getTags().contains(tag));
    }

    @Test
    @DisplayName("스터디 세팅 폼 조회(활동 지역)")
    @WithAccount("gudrb")
    void studySettingFormZone() throws Exception {
        mockMvc.perform(get("/study/" + studyPath + "/settings/zones"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/settings/zones"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("zones"))
                .andExpect(model().attributeExists("whitelist"));
    }

    @Test
    @DisplayName("스터디 지역 추가")
    @WithAccount("gudrb")
    void addStudyZone() throws Exception {
        Zone testZone = Zone.builder().city("test").localNameOfCity("테스트시").province("테스트주").build();
        zoneRepository.save(testZone);
        ZoneForm zoneForm = ZoneForm.builder()
                .zoneName(testZone.toString())
                .build();
        mockMvc.perform(post("/study/" + studyPath + "/settings/zones/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zoneForm))
                        .with(csrf()))
                .andExpect(status().isOk());
        Study study = studyRepository.findStudyWithZonesByPath(studyPath);
        assertTrue(study.getZones().contains(testZone));
    }

    @Test
    @DisplayName("스터디 지역 삭제")
    @WithAccount("gudrb")
    void removeStudyZone() throws Exception {
        Study study = studyRepository.findStudyWithZonesByPath(studyPath);
        Zone testZone = Zone.builder().city("test").localNameOfCity("테스트시").province("테스트주").build();
        zoneRepository.save(testZone);
        studyService.addZone(study, testZone);
        ZoneForm zoneForm = ZoneForm.builder()
                .zoneName(testZone.toString())
                .build();
        mockMvc.perform(post("/study/" + studyPath + "/settings/zones/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zoneForm))
                        .with(csrf()))
                .andExpect(status().isOk());
        assertFalse(study.getZones().contains(testZone));
    }

    @Test
    @DisplayName("스터디 세팅 폼 조회(스터디)")
    @WithAccount("gudrb")
    void studySettingFormStudy() throws Exception {
        mockMvc.perform(get("/study/" + studyPath + "/settings/study"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/settings/study"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"));
    }

    @Test
    @DisplayName("스터디 공개")
    @WithAccount("gudrb")
    void publishStudy() throws Exception {
        mockMvc.perform(post("/study/" + studyPath + "/settings/study/publish")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + studyPath + "/settings/study"))
                .andExpect(flash().attributeExists("message"));
        Study study = studyRepository.findByPath(studyPath);
        assertTrue(study.isPublished());
    }

    @Test
    @DisplayName("스터디 종료")
    @WithAccount("gudrb")
    void closeStudy() throws Exception {
        Study study = studyRepository.findByPath(studyPath);
        studyService.publish(study);
        mockMvc.perform(post("/study/" + studyPath + "/settings/study/close")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + studyPath + "/settings/study"))
                .andExpect(flash().attributeExists("message"));
        assertTrue(study.isClosed());
    }

    @Test
    @DisplayName("스터디 팀원 모집 시작")
    @WithAccount("gudrb")
    void startRecruit() throws Exception {
        Study study = studyRepository.findByPath(studyPath);
        studyService.publish(study);
        mockMvc.perform(post("/study/" + studyPath + "/settings/recruit/start")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + studyPath + "/settings/study"))
                .andExpect(flash().attributeExists("message"));
        assertTrue(study.isRecruiting());
    }

    @Test
    @DisplayName("스터디 팀원 모집 중지: 1시간 이내 시도 -> 실패")
    @WithAccount("gudrb")
    void stopRecruit() throws Exception {
        Study study = studyRepository.findByPath(studyPath);
        studyService.publish(study);
        studyService.startRecruit(study);
        mockMvc.perform(post("/study/" + studyPath + "/settings/recruit/stop")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + studyPath + "/settings/study"))
                .andExpect(flash().attributeExists("message"));
        assertTrue(study.isRecruiting());
    }

    @Test
    @DisplayName("스터디 경로 변경")
    @WithAccount("gudrb")
    void updateStudyPath() throws Exception {
        String newPath = "new-path";
        mockMvc.perform(post("/study/" + studyPath + "/settings/study/path")
                        .param("newPath", newPath)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + newPath + "/settings/study"))
                .andExpect(flash().attributeExists("message"));
        Study study = studyRepository.findByPath(newPath);
        assertEquals(newPath, study.getPath());
    }

    @Test
    @DisplayName("스터디 이름 변경")
    @WithAccount("gudrb")
    void updateStudyTitle() throws Exception {
        String newTitle = "newTitle";
        mockMvc.perform(post("/study/" + studyPath + "/settings/study/title")
                        .param("newTitle", newTitle)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + studyPath + "/settings/study"))
                .andExpect(flash().attributeExists("message"));
        Study study = studyRepository.findByPath(studyPath);
        assertEquals(newTitle, study.getTitle());
    }

    @Test
    @DisplayName("스터디 삭제")
    @WithAccount("gudrb")
    void removeStudy() throws Exception {
        mockMvc.perform(post("/study/" + studyPath + "/settings/study/remove")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        assertNull(studyRepository.findByPath(studyPath));
    }
}