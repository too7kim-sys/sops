package egovframework.ops.cmm.dashboard.service.impl;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 운영현황 대시보드 집계 Mapper.
 */
@Mapper
public interface DashboardMapper {

    /** 장애 상태별 건수 */
    List<Map<String, Object>> selectIncidentStatusStat();

    /** 진행중(미종결) 장애 건수 */
    int selectOpenIncidentCnt();

    /** 변경 상태별 건수 */
    List<Map<String, Object>> selectChangeStatusStat();

    /** 승인대기(요청/검토) 변경 건수 */
    int selectPendingChangeCnt();

    /** 금일 배포예정 건수 */
    int selectTodayReleaseCnt();

    /** 금일 점검 이상 건수 */
    int selectTodayAbnormalCheckCnt();

    /** 응용시스템 건수 */
    int selectSystemCnt();

    /** 최근 장애 목록(상위 5건) */
    List<Map<String, Object>> selectRecentIncidents();

    /** 최근 변경 목록(상위 5건) */
    List<Map<String, Object>> selectRecentChanges();
}
