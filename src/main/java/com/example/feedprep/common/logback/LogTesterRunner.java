package com.example.feedprep.common.logback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class LogTesterRunner implements CommandLineRunner {

    private static final Logger slackLogger = LoggerFactory.getLogger(LogTesterRunner.class);

    @Override
    public void run(String... args) {

        slackLogger.info("이것은 INFO 메시지입니다. 슬랙으로 전송되지 않습니다 (WARN/ERROR 레벨만 전송 설정).");
        slackLogger.warn("경고! 중요 데이터 처리 중 예상치 못한 상황 발생.");

        try {
            int result = 1 / 0; // 의도적인 ArithmeticException 발생
        } catch (ArithmeticException e) {
            slackLogger.error("치명적인 오류 발생! 0으로 나누기 시도됨.", e);
        }

        slackLogger.debug("이것은 DEBUG 메시지입니다. 콘솔과 파일에만 기록됩니다.");
    }
}
