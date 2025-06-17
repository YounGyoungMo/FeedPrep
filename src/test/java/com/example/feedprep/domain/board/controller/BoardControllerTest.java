// package com.example.feedprep.domain.board.controller;
//
// import com.example.feedprep.domain.board.dto.BoardRequestDto;
// import com.example.feedprep.domain.board.dto.BoardResponseDto;
// import com.example.feedprep.domain.board.service.BoardService;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mockito;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.MediaType;
// import org.springframework.security.test.context.support.WithMockUser;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.test.web.servlet.MockMvc;
//
// import java.util.Collections;
//
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.when;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
// @WithMockUser(username = "testuser", roles = "USER")
// @SpringBootTest
// @AutoConfigureMockMvc
// @ActiveProfiles("test")
// class BoardControllerTest {
//
//     @Autowired
//     private MockMvc mockMvc;
//
//     @MockBean
//     private BoardService boardService;
//
//     @Autowired
//     private ObjectMapper objectMapper;
//
//     @Test
//     @WithMockUser
//     void 게시글_생성() throws Exception {
//         BoardRequestDto request = new BoardRequestDto();
//         request.setTitle("제목");
//         request.setContent("내용");
//         request.setTag("정보");
//         request.setSecret(false);
//         request.setUserId(1L);
//
//         BoardResponseDto response = BoardResponseDto.builder()
//                 .id(1L)
//                 .title("제목")
//                 .content("내용")
//                 .tag("정보")
//                 .secret(false)
//                 .userId(1L)
//                 .viewCount(0)
//                 .build();
//
//         when(boardService.createBoard(any(BoardRequestDto.class))).thenReturn(response);
//
//         mockMvc.perform(post("/boards")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(objectMapper.writeValueAsString(request)))
//                 .andExpect(status().isCreated())
//                 .andExpect(jsonPath("$.id").value(1L))
//                 .andExpect(jsonPath("$.title").value("제목"));
//     }
//
//     @Test
//     @WithMockUser
//     void 게시글_목록조회() throws Exception {
//         when(boardService.getBoards(any())).thenReturn(Collections.emptyList());
//
//         mockMvc.perform(get("/boards"))
//                 .andExpect(status().isOk());
//     }
//
//     @Test
//     @WithMockUser
//     void 게시글_단건조회() throws Exception {
//         Long boardId = 1L;
//         when(boardService.getBoard(boardId)).thenReturn(BoardResponseDto.builder()
//                 .id(boardId)
//                 .title("테스트")
//                 .content("내용")
//                 .tag("질문")
//                 .secret(false)
//                 .userId(1L)
//                 .viewCount(0)
//                 .build());
//
//         mockMvc.perform(get("/boards/{boardId}", boardId))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.id").value(boardId));
//     }
//
//     @Test
//     @WithMockUser
//     void 게시글_수정() throws Exception {
//         BoardRequestDto request = new BoardRequestDto();
//         request.setTitle("수정제목");
//         request.setContent("수정내용");
//         request.setTag("후기");
//         request.setSecret(true);
//         request.setUserId(1L);
//
//         mockMvc.perform(put("/boards/{boardId}", 1L)
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(objectMapper.writeValueAsString(request)))
//                 .andExpect(status().isOk());
//     }
//
//     @Test
//     @WithMockUser
//     void 게시글_삭제() throws Exception {
//         mockMvc.perform(delete("/boards/{boardId}", 1L))
//                 .andExpect(status().isNoContent());
//     }
//
//     @Test
//     @WithMockUser
//     void 게시글_추천() throws Exception {
//         when(boardService.recommendBoard(1L)).thenReturn(true);
//
//         mockMvc.perform(post("/boards/{boardId}/recommend", 1L))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string("추천 완료"));
//     }
//
//     @Test
//     @WithMockUser
//     void 게시글_스크랩() throws Exception {
//         when(boardService.scrapBoard(1L)).thenReturn(true);
//
//         mockMvc.perform(post("/boards/{boardId}/scrap", 1L))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string("스크랩 완료"));
//     }
// }