package egovframework.com.cmm;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * 근무일(영업일) 계산 유틸 + JSP EL 함수.
 *
 * <p>주말(토/일)을 제외한 근무일 기준으로 (1) 시작일에 N 근무일을 더한 날짜와
 * (2) 두 날짜 사이의 소요 근무일을 계산한다. 공휴일 테이블은 적용하지 않는다(주말만 제외).</p>
 */
public final class WorkDays {

    private WorkDays() {
    }

    /** 주말 여부 */
    private static boolean isWeekend(LocalDate d) {
        DayOfWeek w = d.getDayOfWeek();
        return w == DayOfWeek.SATURDAY || w == DayOfWeek.SUNDAY;
    }

    /**
     * 시작일에 근무일 {@code days} 을 더한 날짜.
     * 시작일 자체는 세지 않고, 다음 근무일부터 days 만큼 진행한다. (예: 월요일 + 1근무일 = 화요일)
     */
    public static LocalDate addWorkDays(LocalDate start, int days) {
        if (start == null) {
            start = LocalDate.now();
        }
        LocalDate d = start;
        int added = 0;
        while (added < days) {
            d = d.plusDays(1);
            if (!isWeekend(d)) {
                added++;
            }
        }
        return d;
    }

    /**
     * 시작/종료일 사이의 소요 근무일(양끝 포함). 같은 근무일이면 1, 종료가 시작보다 빠르면 0.
     */
    public static int between(LocalDate from, LocalDate to) {
        if (from == null || to == null || to.isBefore(from)) {
            return 0;
        }
        int count = 0;
        LocalDate d = from;
        while (!d.isAfter(to)) {
            if (!isWeekend(d)) {
                count++;
            }
            d = d.plusDays(1);
        }
        return count;
    }

    /** 문자열("yyyy-MM-dd" 또는 "yyyy-MM-dd HH:mm…")을 LocalDate 로(파싱 실패 시 null) */
    private static LocalDate parse(Object value) {
        if (value == null) {
            return null;
        }
        String s = String.valueOf(value).trim();
        if (s.length() < 10) {
            return null;
        }
        try {
            return LocalDate.parse(s.substring(0, 10));
        } catch (Exception e) {
            return null;
        }
    }

    /* ===== JSP EL 함수 ===== */

    /**
     * 소요 근무일(EL). {@code to} 가 비어있으면 오늘까지로 계산(진행중).
     * 파싱 불가/시작일 없음이면 빈 문자열.
     */
    public static String wdays(Object fromStr, Object toStr) {
        LocalDate from = parse(fromStr);
        if (from == null) {
            return "";
        }
        LocalDate to = parse(toStr);
        if (to == null) {
            to = LocalDate.now();
        }
        return String.valueOf(between(from, to));
    }

    /** 종료일 유무(EL) — '진행중' 표시 분기용. to 가 비어있으면 true */
    public static boolean ongoing(Object toStr) {
        return parse(toStr) == null;
    }
}
