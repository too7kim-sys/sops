package egovframework.com.cmm;

import java.util.Map;

/**
 * JSP EL 함수 — 사용자ID 를 "성명(직급)" 표시문자열로 변환한다.
 *
 * <p>전역 모델의 {@code userNameMap}(ID→성명(직급))과 함께 사용한다.
 * 매핑에 없거나(비활성/삭제) null 이면 ID(또는 빈문자)로 폴백한다.</p>
 *
 * <pre>${uf:nm(userNameMap, csr.reqId)}</pre>
 */
public final class UserFn {

    private UserFn() {
    }

    /** 사용자ID → 성명(직급). 매핑 없으면 ID 그대로(없으면 빈문자). */
    public static String nm(Object mapObj, Object id) {
        if (id == null) {
            return "";
        }
        String key = String.valueOf(id);
        if (key.isEmpty()) {
            return "";
        }
        if (mapObj instanceof Map) {
            Object v = ((Map<?, ?>) mapObj).get(key);
            if (v != null) {
                return String.valueOf(v);
            }
        }
        return key;
    }
}
