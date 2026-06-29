package egovframework.ops.sys.menu.service;

import egovframework.com.cmm.ComDefaultVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 메뉴 VO (메뉴관리 / 동적 메뉴 렌더링 / 권한관리).
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MenuVO extends ComDefaultVO {

    private static final long serialVersionUID = 1L;

    /** 메뉴 ID */
    private Long menuId;

    /** 메뉴명 */
    private String menuNm;

    /** 링크(그룹은 null) */
    private String menuUrl;

    /** 활성표시 키(컨트롤러 menu 속성과 매칭) */
    private String menuKey;

    /** 유형 : GROUP(그룹헤더) / ITEM(메뉴) */
    private String menuType;

    /** 상위 메뉴 ID */
    private Long upperId;

    /** 상위 메뉴명(조인) */
    private String upperNm;

    /** 정렬순서 */
    private Integer sortOrdr;

    /** 사용여부 */
    private String useAt;

    /** 하위 메뉴(트리 렌더링) */
    private List<MenuVO> children = new ArrayList<>();

    /** 권한관리 화면 — 역할 접근 허용 여부 */
    private boolean granted;
}
