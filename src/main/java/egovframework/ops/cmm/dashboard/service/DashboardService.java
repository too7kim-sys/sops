package egovframework.ops.cmm.dashboard.service;

import java.util.List;
import java.util.Map;

/**
 * 운영현황 대시보드 서비스 인터페이스.
 */
public interface DashboardService {

    /** 대시보드 요약 지표 (Map: openIncident, pendingChange, todayRelease, abnormalCheck, systemCnt 등) */
    Map<String, Object> selectSummary();

    /** 장애 상태별 통계 */
    List<Map<String, Object>> selectIncidentStatusStat();

    /** 변경 상태별 통계 */
    List<Map<String, Object>> selectChangeStatusStat();

    /** 최근 장애 목록 */
    List<Map<String, Object>> selectRecentIncidents();

    /** 최근 변경 목록 */
    List<Map<String, Object>> selectRecentChanges();
}
