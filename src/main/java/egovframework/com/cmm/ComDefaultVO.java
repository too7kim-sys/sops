package egovframework.com.cmm;

import lombok.Data;

import java.io.Serializable;

/**
 * 검색/페이징 공통 VO.
 *
 * <p>표준프레임워크 공통 검색조건(searchCondition/searchKeyword) 과 페이징
 * 파라미터를 제공한다. 모든 업무 목록 조회 VO 는 본 클래스를 상속한다.</p>
 */
@Data
public class ComDefaultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 검색 조건 (예: 시스템, 상태, 제목 등) */
    private String searchCondition = "";

    /** 검색 키워드 */
    private String searchKeyword = "";

    /** 검색 상태값(워크플로우 상태 필터) */
    private String searchStatus = "";

    /** 검색 대상 시스템 ID */
    private String searchSysId = "";

    /** 현재 페이지 번호 */
    private int pageIndex = 1;

    /** 한 페이지에 표시되는 레코드 건수 */
    private int pageUnit = 10;

    /** 페이지 하단에 표시되는 페이지 사이즈 */
    private int pageSize = 10;

    /** 쿼리 시작 위치(0-base) — firstIndex */
    private int firstIndex = 0;

    /** 페이지당 레코드 수 */
    private int recordCountPerPage = 10;

    /**
     * pageIndex/pageUnit 값을 기준으로 firstIndex/recordCountPerPage 를 계산한다.
     * Controller 에서 목록 조회 직전에 호출한다.
     */
    public void initPaging() {
        if (pageIndex < 1) {
            pageIndex = 1;
        }
        this.recordCountPerPage = this.pageUnit;
        this.firstIndex = (this.pageIndex - 1) * this.pageUnit;
    }
}
