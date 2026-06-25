package egovframework.ops.incident.service;

import java.util.List;

/**
 * 장애관리 서비스 인터페이스.
 *
 * <p>응용프로그램 표준운영절차 — 장애 접수/조회/처리(상태전이)/종결을 제공한다.</p>
 */
public interface EgovIncidentService {

    List<IncidentVO> selectIncidentList(IncidentVO searchVO);

    int selectIncidentListCnt(IncidentVO searchVO);

    /** 장애 상세 (처리이력 포함) */
    IncidentVO selectIncident(Long incId);

    /** 장애 접수(등록) */
    void insertIncident(IncidentVO vo);

    /** 장애 기본정보 수정 */
    void updateIncident(IncidentVO vo);

    /**
     * 장애 처리(상태전이).
     * 상태 변경과 함께 원인/조치 내용을 갱신하고 처리이력을 적재한다.
     */
    void processIncident(IncidentVO vo);

    /** 장애 삭제 */
    void deleteIncident(Long incId);

    /** 에스컬레이션 등록 */
    void escalateIncident(IncidentEscalVO vo);
}
