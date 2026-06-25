package egovframework.ops.deploy.service;

import lombok.Getter;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
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
 * Git 연동 자동배포 실행기.
 *
 * <p>JGit 으로 내부 git 저장소에서 지정 ref(태그/브랜치/커밋)를 배포 디렉터리로
 * 체크아웃(최초 clone, 이후 fetch+checkout)한 뒤, 시스템별 배포 스크립트를 실행한다.
 * 체크아웃·스크립트 실행 로그와 커밋 해시, 성공 여부를 결과로 반환한다.</p>
 */
@Component
public class GitDeployManager {

    private static final Logger log = LoggerFactory.getLogger(GitDeployManager.class);

    /** 배포 실행 결과 */
    @Getter
    public static class Outcome {
        private final boolean success;
        private final String commitHash;
        private final String logText;

        public Outcome(boolean success, String commitHash, String logText) {
            this.success = success;
            this.commitHash = commitHash;
            this.logText = logText;
        }
    }

    /**
     * 체크아웃 후 배포 스크립트를 실행한다.
     *
     * @param repoUrl     git 저장소 URL (file://, http(s)://, ssh)
     * @param branch      기본 브랜치
     * @param ref         배포 대상 ref (태그/브랜치/커밋). 비어있으면 branch 사용
     * @param dir         체크아웃 대상 디렉터리
     * @param script      체크아웃 후 실행할 배포 스크립트(없으면 생략)
     * @param timeoutSec  스크립트 실행 타임아웃(초)
     * @param env         스크립트 실행 환경변수
     */
    public Outcome run(String repoUrl, String branch, String ref, File dir,
                       String script, int timeoutSec, Map<String, String> env) {
        StringBuilder sb = new StringBuilder();
        String commitHash = null;
        boolean success = false;
        try {
            String refToUse = (ref != null && !ref.isBlank()) ? ref.trim()
                    : (branch != null && !branch.isBlank() ? branch.trim() : "HEAD");

            File gitDir = new File(dir, ".git");
            if (!gitDir.exists()) {
                sb.append("[git] clone ").append(repoUrl).append(" -> ").append(dir.getAbsolutePath()).append('\n');
                Git cloned = Git.cloneRepository().setURI(repoUrl).setDirectory(dir).call();
                cloned.close();
            } else {
                sb.append("[git] open ").append(dir.getAbsolutePath()).append(" / fetch\n");
                try (Git git = Git.open(dir)) {
                    git.fetch().call();
                }
            }

            try (Git git = Git.open(dir)) {
                Repository repo = git.getRepository();
                ObjectId target = repo.resolve(refToUse);
                if (target == null) {
                    target = repo.resolve("origin/" + refToUse);
                }
                if (target == null) {
                    target = repo.resolve("refs/tags/" + refToUse);
                }
                if (target == null) {
                    sb.append("[git] ref 를 찾을 수 없습니다 : ").append(refToUse).append('\n');
                    return new Outcome(false, null, sb.toString());
                }
                git.checkout().setName(target.getName()).call();
                commitHash = target.getName();
                sb.append("[git] checkout ").append(refToUse)
                        .append(" -> ").append(shortHash(commitHash)).append('\n');
            }

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
            log.warn("Git 배포 실패", e);
            sb.append("[error] ").append(e.getClass().getSimpleName())
                    .append(" : ").append(e.getMessage()).append('\n');
            success = false;
        }
        return new Outcome(success, commitHash, sb.toString());
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

    private String shortHash(String hash) {
        return hash != null && hash.length() > 8 ? hash.substring(0, 8) : hash;
    }
}
