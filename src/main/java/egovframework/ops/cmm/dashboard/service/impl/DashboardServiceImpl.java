package egovframework.ops.cmm.dashboard.service.impl;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import egovframework.ops.cmm.dashboard.service.DashboardService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 운영현황 대시보드 서비스 구현체.
 */
@Service("dashboardService")
public class DashboardServiceImpl extends EgovAbstractServiceImpl implements DashboardService {

    private final DashboardMapper dashboardMapper;

    public DashboardServiceImpl(DashboardMapper dashboardMapper) {
        this.dashboardMapper = dashboardMapper;
    }

    @Override
    public Map<String, Object> selectSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("openIncident", dashboardMapper.selectOpenIncidentCnt());
        summary.put("pendingChange", dashboardMapper.selectPendingChangeCnt());
        summary.put("todayRelease", dashboardMapper.selectTodayReleaseCnt());
        summary.put("abnormalCheck", dashboardMapper.selectTodayAbnormalCheckCnt());
        summary.put("systemCnt", dashboardMapper.selectSystemCnt());
        return summary;
    }

    @Override
    public List<Map<String, Object>> selectIncidentStatusStat() {
        return dashboardMapper.selectIncidentStatusStat();
    }

    @Override
    public List<Map<String, Object>> selectChangeStatusStat() {
        return dashboardMapper.selectChangeStatusStat();
    }

    @Override
    public List<Map<String, Object>> selectRecentIncidents() {
        return dashboardMapper.selectRecentIncidents();
    }

    @Override
    public List<Map<String, Object>> selectRecentChanges() {
        return dashboardMapper.selectRecentChanges();
    }
}
