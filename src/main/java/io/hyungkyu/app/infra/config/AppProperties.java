package io.hyungkyu.app.infra.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component // @Component로 등록하여 외부에서 의존성을 주입할 수 있게 하였음.
@ConfigurationProperties("app") // @ConfigurationProperties 어노테이션을 이용해 설정파일의 app prefix 하위에 있는 항목들을 주입받아 사용할 수 있음.
public class AppProperties {
    private String host;
}
