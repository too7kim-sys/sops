package egovframework.ops.cmm.code.service;

import java.util.List;

/**
 * 공통코드 서비스 인터페이스.
 */
public interface EgovCodeService {

    /** 특정 코드그룹의 사용중 코드 목록 (드롭다운용) */
    List<CodeVO> selectCodeList(String codeGrp);

    /** 코드 전체 목록 (관리화면, 검색조건 적용) */
    List<CodeVO> selectCodeMngList(CodeVO searchVO);

    /** 코드 전체 건수 */
    int selectCodeMngListCnt(CodeVO searchVO);

    /** 코드 등록 */
    void insertCode(CodeVO codeVO);

    /** 코드 수정 */
    void updateCode(CodeVO codeVO);

    /** 코드 삭제 */
    void deleteCode(CodeVO codeVO);
}
