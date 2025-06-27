package com.example.feedprep.common.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 서버 오류
    SOCIAL_LOGIN_REDIRECT_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "소셜 로그인 중 리다이렉트 요청이 실패했습니다."),
    SOCIAL_LOGIN_TOKEN_REQUEST_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "소셜 로그인 토큰 요청에 실패했습니다."),
    SOCIAL_LOGIN_INVALID_PROVIDER(HttpStatus.BAD_REQUEST, "유효하지 않은 소셜 로그인 제공자입니다."),
    SOCIAL_LOGIN_AUTH_CODE_MISSING(HttpStatus.BAD_REQUEST, "인가 코드가 없습니다."),

    // 잘못된 요청
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다." ),
    UNSUPPORTED_OAUTH_PROVIDER(HttpStatus.BAD_REQUEST, "해당 소셜 로그인 방식은 지원하지 않습니다."),

    // @Column(unique = true) 값이 중복되는 오류
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "이미 존재하는 값입니다. 중복된 입력을 확인해주세요."),

    // Forbidden
    INVALID_ROLE_REQUEST(HttpStatus.FORBIDDEN, "해당 역할로 가입할 수 없습니다."),
    UNAUTHORIZED_ROLE_LOGIN(HttpStatus.FORBIDDEN, "요청하신 로그인 경로에서는 해당 역할의 계정으로 로그인할 수 없습니다."),
    FORBIDDEN_LOGOUT_ATTEMPT(HttpStatus.FORBIDDEN, "다른 사용자의 로그아웃 요청은 허용되지 않습니다."),
    WITHDRAWN_USER(HttpStatus.FORBIDDEN, "탈퇴 처리된 사용자입니다."),

    //인증
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    MISSING_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 존재하지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    INVALID_JWT_SIGNATURE(HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT 서명입니다."),
    INVALID_SECRET_CODE(HttpStatus.UNAUTHORIZED, "유효하지 않은 시크릿 코드입니다." ),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED,"이미 만료된 토큰입니다."),
    UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED,"지원하지 않는 토큰입니다."),


    NOT_MATCH_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 틀렸습니다."),

    // 메일
    CREATE_MAIL_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "메일 생성에 실피하였습니다."),
    SEND_MAIL_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "메일 송인이 실패하였습니다."),
    NOT_CONFIRMED_AUTHNUMBER(HttpStatus.BAD_REQUEST, "인증번호가 만료 되었거나 일치 하지 않습니다."),

    // 관리자
    PENDING_TUTOR(HttpStatus.BAD_REQUEST, "해당 유저는 승인 대기중인 튜터입니다."),
    NOT_PENDING_TUTOR(HttpStatus.BAD_REQUEST, "해당 유저는 승인 대기중인 튜터가 아닙니다."),
    ALREADY_REGISTERED_TECHSTACK(HttpStatus.BAD_REQUEST, "이미 등록된 기술스택 입니다."),


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
    DUPLICATE_SUBSCRIPTION(HttpStatus.CONFLICT, "이미 구독중입니다."),
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
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),



    // 사용자 인증 관련
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 일치하지 않습니다. " ),
    TUTOR_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 튜터를 찾을 수 없습니다."),
    INVALID_DOCUMENT(HttpStatus.NOT_FOUND, "존재하지 않는 문서입니다."),

    // 피드백 요청 에러코드
    NOT_FOUND_FEEDBACK_REQUEST(HttpStatus.NOT_FOUND, "해당 피드백 요청을 찾을 수 없습니다."),
    NOT_FOUND_FEEDBACK(HttpStatus.NOT_FOUND, "해당 피드백을 찾을 수 없습니다."),
    NOT_FOUND_FEEDBACK_REVIEW(HttpStatus.NOT_FOUND, "해당 리뷰를 찾을 수 없습니다."),
    UNAUTHORIZED_REQUESTER_ACCESS(HttpStatus.FORBIDDEN, "해당 요청을 수행할 권한이 없습니다."),
    BAD_REQUEST_STATE(HttpStatus.BAD_REQUEST, "유효하지 않은 상태 번호입니다"),
    FOREIGN_REQUESTER_REVIEW_ACCESS(HttpStatus.FORBIDDEN, "본인이 작성하지 않는 리뷰에 접근할수 없습니다.."),

    SELF_FEEDBACK_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "자신에게는 피드백을 요청할 수 없습니다."),
    CANNOT_EDIT_COMPLETED_REQUEST(HttpStatus.BAD_REQUEST, "이미 완료된 피드백은 수정할 수 없습니다."),
    CANNOT_EDIT_PENDING_REQUEST(HttpStatus.BAD_REQUEST, "이미 진행중인 피드백은 수정할 수 없습니다."),
    CANNOT_EDIT_IN_PROCESS_REQUEST(HttpStatus.BAD_REQUEST, "이미 진행중인 피드백은 수정할 수 없습니다."),
    CANNOT_CANCEL_COMPLETED(HttpStatus.BAD_REQUEST, "이미 완료된 피드백은 취소할 수 없습니다."),
    DUPLICATE_FEEDBACK_REQUEST(HttpStatus.CONFLICT, "이미 같은 튜터님께 신청 대기 중입니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "피드백 처리 중 내부 오류가 발생했습니다."),

    //피드백 작성  에러코드
    INVALID_REQUEST_STATE(HttpStatus.BAD_REQUEST, "요청이 진행중인 것만 작성 가능합니다."),
    CANNOT_EDIT_PENDING_FEEDBACK(HttpStatus.CONFLICT, "작성 대기중인 피드백은 수정이 불가합니다."),
    CANNOT_REJECT_NON_PENDING_FEEDBACK(HttpStatus.CONFLICT, "작성 대기중인 피드백만 거절할 수 있습니다."),

    //알림
    NOT_FOUND_NOTIFICATION(HttpStatus.NOT_FOUND,"요청하신 알림은 존재 하지 않습니다."),

    // 결제
    ALREADY_REFUNDED(HttpStatus.BAD_REQUEST, "이미 환불된 결제입니다."),
    INSUFFICIENT_POINTS(HttpStatus.BAD_REQUEST, "포인트가 부족합니다."),

    ;

    private final HttpStatus httpStatus;
    private final String message;
}
