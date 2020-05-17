package us.dev.backend.AdminManager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManagerNotice {
    String noticeAll = "유저 관리에서 각 Token 확인가능합니다.";
    String noticeWeb = "API 기반 테스트 진행해주시고, Slack에 의견 반영부탁드려요.";
    String noticeAndroid = "API 기반 테스트 진행해주시고, Slack에 의견 반영부탁드려요.";
    String noticeBackEnd = "DashBoard에서 구현하지 않은 부분 개발할지 논의, Refactoring 해야함.";
}
