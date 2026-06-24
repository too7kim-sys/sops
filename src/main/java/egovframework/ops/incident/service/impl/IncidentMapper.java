package egovframework.ops.incident.service.impl;

import egovframework.ops.incident.service.IncidentHisVO;
import egovframework.ops.incident.service.IncidentVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 장애관리 MyBatis Mapper (Persistence 계층).
 */
@Mapper
public interface IncidentMapper {

    List<IncidentVO> selectIncidentList(IncidentVO searchVO);

    int selectIncidentListCnt(IncidentVO searchVO);

    IncidentVO selectIncident(Long incId);

    List<IncidentHisVO> selectIncidentHisList(Long incId);

    void insertIncident(IncidentVO vo);

    void updateIncident(IncidentVO vo);

    void updateIncidentProcess(IncidentVO vo);

    void insertIncidentHis(IncidentHisVO hisVO);

    void deleteIncident(Long incId);

    void deleteIncidentHis(Long incId);
}
