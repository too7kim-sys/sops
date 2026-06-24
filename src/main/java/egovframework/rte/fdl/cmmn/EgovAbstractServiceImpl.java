package egovframework.rte.fdl.cmmn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ApplicationObjectSupport;

/**
 * 전자정부 표준프레임워크 Business Logic 계층의 ServiceImpl 추상 클래스.
 *
 * <p>표준프레임워크의 {@code egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl} 과
 * 동일한 역할(공통 로깅/메시지 처리)을 제공한다. 모든 업무 ServiceImpl 은 본 클래스를
 * 상속하여 표준운영절차의 비즈니스 로직을 구현한다.</p>
 */
public abstract class EgovAbstractServiceImpl extends ApplicationObjectSupport {

    /** 공통 로거 */
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

}
