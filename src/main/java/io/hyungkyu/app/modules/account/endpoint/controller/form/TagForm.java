package io.hyungkyu.app.modules.account.endpoint.controller.form;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagForm {
    private String tagTitle; // ajax 요청시 전달하는 키 값이 tagTitle이라 일치시켰음.
}
