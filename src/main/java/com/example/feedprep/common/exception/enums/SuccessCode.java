package com.example.feedprep.common.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode {

    // 회원가입 및 로그인 성공
    SIGNUP_SUCCESS(HttpStatus.CREATED,"회원가입에 성공하였습니다." ),
    LOGIN_SUCCESS(HttpStatus.OK, "로그인에 성공하였습니다."),

    // 회원 정보
    GET_TUTORLIST_SUCCESS(HttpStatus.OK, "튜터 목록 조회를 성공하였습니다."),
    GET_MYINFO_SUCCESS(HttpStatus.OK,"내 정보 불러오기에 성공하였습니다."),
    UPDATE_MYINFO_SUCCESS(HttpStatus.OK,"내 정보 수정이 완료 되었습니다."),
    CHANGE_PASSWORD_SUCCESS(HttpStatus.OK,"비밀번호 수정을 성공하였습니다."),

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

   //피드백 요청 취소 성공
    OK_FEEDBACK_REQUEST_CANCELED(HttpStatus.OK, "정상적으로 취소 되었습니다."),
    // 기술스택
    TECH_STACK_CREATED(HttpStatus.CREATED, "기술 스택을 추가하였습니다."),
    TECH_STACK_DELETED(HttpStatus.OK, "기술 스택을 삭제하였습니다."),
    TECH_STACK_LISTED(HttpStatus.OK, "기술 스택 목록입니다."),
    ADD_MY_TECH_STACK(HttpStatus.CREATED, "관심 기술 스택을 추가하였습니다."),
    REMOVE_MY_TECH_STACK(HttpStatus.OK, "관심 기술 스택을 삭제하였습니다."),

    RECOMMEND_TUTOR_LIST(HttpStatus.OK, "추천 튜터 리스트 입니다.")

    ;


    private final HttpStatus httpStatus;
    private final String message;
}
