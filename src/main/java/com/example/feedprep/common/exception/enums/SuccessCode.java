package com.example.feedprep.common.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode {

    // OAuth2
    SOCIAL_OAUTH_LOGIN_SUCCESS(HttpStatus.OK, "소셜 로그인에 성공하였습니다."),


    // 회원가입, 로그인, 로그아웃, 탈퇴 성공
    SIGNUP_SUCCESS(HttpStatus.CREATED,"회원가입에 성공하였습니다." ),
    LOGIN_SUCCESS(HttpStatus.OK, "로그인에 성공하였습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK, "로그아웃에 성공하였습니다."),
    WITHDRAW_SUCCESS(HttpStatus.OK, "회원 탈퇴에 성공하였습니다."),

    // access token 및 refresh token 재발급
    TOKEN_REFRESH_SUCCESS(HttpStatus.OK, "토큰 재발급에 성공하였습니다."),

    // 회원 정보
    GET_TUTORLIST_SUCCESS(HttpStatus.OK, "튜터 목록 조회를 성공하였습니다."),
    GET_MYINFO_SUCCESS(HttpStatus.OK,"내 정보 불러오기에 성공하였습니다."),
    UPDATE_MYINFO_SUCCESS(HttpStatus.OK,"내 정보 수정이 완료 되었습니다."),
    CHANGE_PASSWORD_SUCCESS(HttpStatus.OK,"비밀번호 수정을 성공하였습니다."),
    SEND_MAIL_SUCCESS(HttpStatus.OK, "메일 발송을 성공하였습니다."),

    // 문서
    CREATE_DOCUMENT_SUCCESS(HttpStatus.CREATED, "문서 작성에 성공하였습니다."),
    GET_MYDOCUMENLIST_SUCCESS(HttpStatus.OK, "나의 문서목록 조회를 성공했습니다."),
    GET_MYDOCUMEN_SUCCESS(HttpStatus.OK, "나의 단건 문서 조회를 성공했습니다."),
    DELETE_MYDOCUMEN_SUCCESS(HttpStatus.ACCEPTED, "문서 삭제에 성공하였습니다."),


    // 구독 모듈
    SUBSCRIBED(HttpStatus.CREATED, "구독하였습니다."),
    UNSUBSCRIBED(HttpStatus.OK, "구독을 취소하였습니다."),
    SUBSCRIPTION_LIST(HttpStatus.OK, "구독 목록입니다."),
    SUBSCRIBER_LIST(HttpStatus.OK, "구독자 목록입니다."),

    //피드백
    OK_SUCCESS_FEEDBACK_CREATED(HttpStatus.CREATED,"피드백 작성을 완료했습니다."),
    OK_SUCCESS_FEEDBACK_UPDATE(HttpStatus.OK,"피드백 수정을 완료했습니다."),

    //피브백 신청
    OK_SUCCESS_FEEDBACK_REQUEST_CREATED(HttpStatus.CREATED,"정상적으로 요청 신청이 완료 되었습니다."),
    OK_SUCCESS_FEEDBACK_REQUEST(HttpStatus.CREATED,"정상적으로 조회 되었습니다."),
    OK_SUCCESS_FEEDBACK_REQUEST_UPDATE(HttpStatus.CREATED,"정상적으로 요청이 수정 되었습니다."),
    OK_SUCCESS_FEEDBACK_REQUEST_ACCEPT(HttpStatus.OK, "피드백 요청이 수락되었습니다."),
    OK_SUCCESS_FEEDBACK_REQUEST_REJECTED(HttpStatus.OK, "피드백 요청이 거절되었습니다."),
    OK_SUCCESS_FEEDBACK_REQUEST_CANCELED(HttpStatus.OK, "정상적으로 요청이 취소 되었습니다."),

    //피드백 리뷰
    OK_SUCCESS_FEEDBACK_REVIEW_CREATED(HttpStatus.CREATED, "피드백 리뷰가 작성 되었습니다."),
    OK_SUCCESS_FEEDBACK_REVIEW(HttpStatus.CREATED, "피드백 리뷰가 조회 되었습니다."),
    OK_SUCCESS_FEEDBACK_REVIEW_UPDATE(HttpStatus.CREATED, "피드백 리뷰가 수정 되었습니다."),
    OK_SUCCESS_FEEDBACK_REVIEW_DELETED(HttpStatus.OK, "피드백 리뷰가 정상적으로 삭제되었습니다."),
    //알림
    OK_SUCCESS_Notification(HttpStatus.OK, "알림을 정상적으로 조회 했습니다."),
    OK_SUCCESS_Notification_DELETED(HttpStatus.OK, "알림을 정상적으로 삭제 되었습니다."),

    // 기술스택
    TECH_STACK_CREATED(HttpStatus.CREATED, "기술 스택을 추가하였습니다."),
    TECH_STACK_DELETED(HttpStatus.OK, "기술 스택을 삭제하였습니다."),
    TECH_STACK_LISTED(HttpStatus.OK, "기술 스택 목록입니다."),
    ADD_MY_TECH_STACK(HttpStatus.CREATED, "관심 기술 스택을 추가하였습니다."),
    REMOVE_MY_TECH_STACK(HttpStatus.OK, "관심 기술 스택을 삭제하였습니다."),

    // 관리자
    APPROVE_TUTOR(HttpStatus.OK, "튜터 활동을 승인하였습니다.")

    ;


    private final HttpStatus httpStatus;
    private final String message;
}
