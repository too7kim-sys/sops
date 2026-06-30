package egovframework.ops.deploy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * SVN 연동 자동배포 실행기.
 *
 * <p>{@code svn} CLI 로 지정 저장소(URL)를 배포 디렉터리에 체크아웃(최초) 또는
 * 업데이트(이후)하고, 시스템별 배포 스크립트를 실행한다. ref 가 숫자면 해당
 * 리비전(-r)으로 고정한다. 체크아웃·스크립트 로그와 리비전, 성공 여부를 반환한다.</p>
 *
 * <p>Git(JGit) 과 동일한 {@link GitDeployManager.Outcome} 형식으로 결과를 돌려
 * 배포 실행기에서 형상관리 유형만 분기하면 되도록 한다.</p>
 */
@Component
public class SvnDeployManager {

    private static final Logger log = LoggerFactory.getLogger(SvnDeployManager.class);

    /**
     * SVN 체크아웃/업데이트 후 배포 스크립트를 실행한다.
     *
     * @param repoUrl    SVN 저장소 URL (trunk/branches/tags 경로 포함 가능)
     * @param ref        배포 대상 리비전(숫자). 비어있으면 HEAD
     * @param dir        체크아웃 대상 디렉터리
     * @param script     체크아웃 후 실행할 배포 스크립트(없으면 생략)
     * @param timeoutSec 명령/스크립트 실행 타임아웃(초)
     * @param env        스크립트 실행 환경변수
     */
    public GitDeployManager.Outcome run(String repoUrl, String ref, File dir,
                                        String script, int timeoutSec, Map<String, String> env) {
        StringBuilder sb = new StringBuilder();
        String revision = null;
        boolean success = false;
        try {
            String rev = (ref != null && ref.trim().matches("\\d+")) ? ref.trim() : null;
            File svnDir = new File(dir, ".svn");

            if (!svnDir.exists()) {
                dir.mkdirs();
                sb.append("[svn] checkout ").append(repoUrl)
                        .append(rev != null ? (" -r " + rev) : "")
                        .append(" -> ").append(dir.getAbsolutePath()).append('\n');
                int co = rev != null
                        ? runCmd(dir, env, timeoutSec, sb, "svn", "checkout", "-r", rev, "--non-interactive", repoUrl, ".")
                        : runCmd(dir, env, timeoutSec, sb, "svn", "checkout", "--non-interactive", repoUrl, ".");
                if (co != 0) {
                    return new GitDeployManager.Outcome(false, null, sb.toString());
                }
            } else {
                sb.append("[svn] update").append(rev != null ? (" -r " + rev) : "").append('\n');
                int up = rev != null
                        ? runCmd(dir, env, timeoutSec, sb, "svn", "update", "-r", rev, "--non-interactive")
                        : runCmd(dir, env, timeoutSec, sb, "svn", "update", "--non-interactive");
                if (up != 0) {
                    return new GitDeployManager.Outcome(false, null, sb.toString());
                }
            }

            // 체크아웃된 리비전 확인
            revision = resolveRevision(dir, env, timeoutSec, sb);
            sb.append("[svn] revision = ").append(revision == null ? "?" : revision).append('\n');

            // 배포 스크립트 실행
            if (script != null && !script.isBlank()) {
                sb.append("[script] 실행\n");
                int exit = runScript(script, dir, env, timeoutSec, sb);
                sb.append("[script] exit code = ").append(exit).append('\n');
                success = (exit == 0);
            } else {
                sb.append("[script] 배포 스크립트 미설정 - 생략\n");
                success = true;
            }
        } catch (Exception e) {
            log.warn("SVN 배포 실패", e);
            sb.append("[error] ").append(e.getClass().getSimpleName())
                    .append(" : ").append(e.getMessage()).append('\n');
            success = false;
        }
        return new GitDeployManager.Outcome(success, revision, sb.toString());
    }

    /** svnversion 으로 현재 작업본 리비전을 얻는다(실패 시 null). */
    private String resolveRevision(File dir, Map<String, String> env, int timeoutSec, StringBuilder sb) {
        try {
            StringBuilder out = new StringBuilder();
            int code = runCmdCapture(dir, env, timeoutSec, out, "svnversion", "-n");
            if (code == 0) {
                String v = out.toString().trim();
                return v.isEmpty() ? null : v;
            }
        } catch (Exception ignore) {
            // svnversion 미설치 등 — 리비전 미확인으로 처리
        }
        return null;
    }

    private int runCmd(File dir, Map<String, String> env, int timeoutSec, StringBuilder sb, String... cmd)
            throws Exception {
        StringBuilder out = new StringBuilder();
        int code = runCmdCapture(dir, env, timeoutSec, out, cmd);
        if (out.length() > 0) {
            for (String line : out.toString().split("\n")) {
                sb.append("  ").append(line).append('\n');
            }
        }
        return code;
    }

    private int runCmdCapture(File dir, Map<String, String> env, int timeoutSec, StringBuilder out, String... cmd)
            throws Exception {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.directory(dir);
        pb.redirectErrorStream(true);
        if (env != null) {
            env.forEach((k, v) -> pb.environment().put(k, v == null ? "" : v));
        }
        Process p = pb.start();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                out.append(line).append('\n');
            }
        }
        if (!p.waitFor(timeoutSec, TimeUnit.SECONDS)) {
            p.destroyForcibly();
            out.append("타임아웃(").append(timeoutSec).append("s) - 강제 종료\n");
            return -1;
        }
        return p.exitValue();
    }

    private int runScript(String script, File dir, Map<String, String> env, int timeoutSec, StringBuilder sb)
            throws Exception {
        ProcessBuilder pb = new ProcessBuilder("sh", "-c", script);
        pb.directory(dir);
        pb.redirectErrorStream(true);
        if (env != null) {
            env.forEach((k, v) -> pb.environment().put(k, v == null ? "" : v));
        }
        Process p = pb.start();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append("  ").append(line).append('\n');
            }
        }
        if (!p.waitFor(timeoutSec, TimeUnit.SECONDS)) {
            p.destroyForcibly();
            sb.append("[script] 타임아웃(").append(timeoutSec).append("s) - 강제 종료\n");
            return -1;
        }
        return p.exitValue();
    }
}
