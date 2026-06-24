package egovframework.com.cmm;

import lombok.Getter;
import lombok.Setter;

/**
 * 페이징 정보 계산기.
 *
 * <p>표준프레임워크 페이징 처리에 대응하는 화면 페이징 계산 유틸이다.
 * 전체 건수/현재 페이지/페이지 단위를 입력받아 시작·종료 페이지번호를 산출한다.</p>
 */
@Getter
@Setter
public class PaginationInfo {

    /** 현재 페이지 */
    private int currentPageNo = 1;

    /** 페이지당 레코드 수 */
    private int recordCountPerPage = 10;

    /** 화면 하단 페이지 번호 개수 */
    private int pageSize = 10;

    /** 전체 레코드 수 */
    private int totalRecordCount = 0;

    public int getTotalPageCount() {
        if (recordCountPerPage <= 0) {
            return 1;
        }
        return ((totalRecordCount - 1) / recordCountPerPage) + 1;
    }

    public int getFirstPageNoOnPageList() {
        return ((currentPageNo - 1) / pageSize) * pageSize + 1;
    }

    public int getLastPageNoOnPageList() {
        int lastPage = getFirstPageNoOnPageList() + pageSize - 1;
        return Math.min(lastPage, getTotalPageCount());
    }

    public boolean isHasPrevPageList() {
        return getFirstPageNoOnPageList() > 1;
    }

    public boolean isHasNextPageList() {
        return getLastPageNoOnPageList() < getTotalPageCount();
    }
}
