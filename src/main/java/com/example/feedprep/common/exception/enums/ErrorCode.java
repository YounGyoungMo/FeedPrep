package com.example.feedprep.common.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 잘못된 요청
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다." ),

    INVALID_ROLE_REQUEST(HttpStatus.FORBIDDEN, "해당 역할로 가입할 수 없습니다."),

    //인증
    MISSING_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 존재하지 않습니다."),
    INVALID_JWT_SIGNATURE(HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT 서명입니다."),
    INVALID_SECRET_CODE(HttpStatus.UNAUTHORIZED, "유효하지 않은 시크릿 코드입니다." ),
    NOT_MATCH_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 틀렸습니다."),


    //유저
    INVALID_USER_ROLE(HttpStatus.BAD_REQUEST, "잘못된 역할입니다. STUDENT, PENDING_TUTOR, ADMIN 중 하나를 선택해 주세요"),
    NOT_FOUND_TUTOR(HttpStatus.BAD_REQUEST,"검색 된 튜터가 없습니다."),

    // 문서 및 S3 파일 업로드
    NOT_FOUND_DOCUMENT(HttpStatus.NOT_FOUND,"해당 문서를 찾을 수 없습니다."),
    NOT_FOUND_FILE(HttpStatus.NOT_FOUND, "업로드 할 이력서를 넣어 주세요."),
    S3_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3 파일 업로드에 실패했습니다."),
    DONT_CREATE_MORE(HttpStatus.BAD_REQUEST, "이력서 저장은 최대 5개까지 입니다."),
    FOREIGN_DOCUMENT_ACCESS(HttpStatus.FORBIDDEN, "자신의 문서만 조회 할 수 있습니다."),
    DONT_DELETE_S3FILE(HttpStatus.INTERNAL_SERVER_ERROR,"저장된 이력서 삭제에 실패하였습니다."),


    // 구독
    CANNOT_SUBSCRIBE_SELF(HttpStatus.BAD_REQUEST, "자기 자신을 구독할 수 없습니다."),
    SUBSCRIPTION_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 구독 정보입니다."),
    UNAUTHORIZED_SUBSCRIPTION_ACCESS(HttpStatus.BAD_REQUEST, "해당 구독 정보를 수정할 수 있는 권한이 없습니다."),
    INVALID_ENUM_GETTYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 GetType 입니다."),


    // 기술 스택
    TECH_STACK_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 기술 스택 정보를 찾을 수 없습니다."),
    DUPLICATE_TECH_STACK(HttpStatus.CONFLICT, "이미 추가된 기술 스택입니다."),
    UNAUTHORIZED_TECH_STACK_DELETION(HttpStatus.BAD_REQUEST, "해당 기술 스택을 삭제할 권한이 없습니다."),

    // ENUM 입력 오류
    INVALID_ENUM_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 요청 입니다."),

    // 유저 모듈 임시 에러 코드
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."),

    // 피드백 요청 생성
    // 사용자 인증 관련
    UNAUTHORIZED_REQUESTER_ACCESS(HttpStatus.FORBIDDEN, "해당 요청을 수행할 권한이 없습니다."),
    TUTOR_NOT_FOUND(HttpStatus.NOT_FOUND, "튜터 정보를 찾을 수 없습니다."),
    INVALID_DOCUMENT(HttpStatus.NOT_FOUND, "존재하지 않는 문서입니다."),
    SELF_FEEDBACK_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "자신에게는 피드백을 요청할 수 없습니다."),
    CANNOT_EDIT_COMPLETED_REQUEST(HttpStatus.BAD_REQUEST, "이미 완료된 피드백은 수정할 수 없습니다."),
    // 요청 취소 관련
    CANNOT_CANCEL_COMPLETED(HttpStatus.BAD_REQUEST, "이미 완료된 피드백은 취소할 수 없습니다."),
    UNAUTHORIZED_CANCEL(HttpStatus.FORBIDDEN, "이 피드백 요청을 취소할 권한이 없습니다."),

    // 추천
    PAGE_NOT_FOUND(HttpStatus.BAD_REQUEST, "잘못된 페이지 입니다."),

    // 조회/접근 관련
    FEEDBACK_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 피드백 요청을 찾을 수 없습니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "이 피드백에 접근할 수 없습니다."),

    // 일반 오류
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "피드백 처리 중 내부 오류가 발생했습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String message;
}
