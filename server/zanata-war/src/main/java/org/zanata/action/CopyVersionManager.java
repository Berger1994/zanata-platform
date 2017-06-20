package org.zanata.action;

import java.io.Serializable;
import java.util.Objects;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import org.zanata.async.AsyncTaskHandleManager;
import org.zanata.async.handle.CopyVersionTaskHandle;
import org.zanata.security.ZanataIdentity;
import org.zanata.service.CopyVersionService;

/**
 * Manages copy version tasks.
 *
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Dependent
public class CopyVersionManager implements Serializable {
    private static final org.slf4j.Logger log =
            org.slf4j.LoggerFactory.getLogger(CopyVersionManager.class);

    private static final long serialVersionUID = 3414395834255069870L;
    @Inject
    @SuppressFBWarnings(value = "SE_BAD_FIELD",
            justification = "CDI proxies are Serializable")
    private AsyncTaskHandleManager asyncTaskHandleManager;
    @Inject
    private CopyVersionService copyVersionServiceImpl;
    @Inject
    private ZanataIdentity identity;

    /**
     * Copy existing version to new version
     *
     * @param projectSlug
     *            - existing project identifier
     * @param versionSlug
     *            - existing version identifier
     * @param newVersionSlug
     *            - new version identifier
     */
    public void startCopyVersion(String projectSlug, String versionSlug,
            String newVersionSlug) {
        CopyVersionKey key = CopyVersionKey.getKey(projectSlug, newVersionSlug);
        CopyVersionTaskHandle handle = new CopyVersionTaskHandle();
        asyncTaskHandleManager.registerTaskHandle(handle, key);
        copyVersionServiceImpl.startCopyVersion(projectSlug, versionSlug,
                newVersionSlug, handle);
    }

    /**
     * Cancel running copy version task
     *
     * @param projectSlug
     *            - target project identifier
     * @param versionSlug
     *            - target version identifier
     */
    public void cancelCopyVersion(String projectSlug, String versionSlug) {
        if (isCopyVersionRunning(projectSlug, versionSlug)) {
            CopyVersionTaskHandle handle =
                    getCopyVersionProcessHandle(projectSlug, versionSlug);
            handle.cancel(true);
            handle.setCancelledTime(System.currentTimeMillis());
            handle.setCancelledBy(identity.getCredentials().getUsername());
            log.info("Copy version cancelled- {}:{}", projectSlug, versionSlug);
        }
    }

    public CopyVersionTaskHandle getCopyVersionProcessHandle(String projectSlug,
            String versionSlug) {
        return (CopyVersionTaskHandle) asyncTaskHandleManager.getHandleByKey(
                CopyVersionKey.getKey(projectSlug, versionSlug));
    }

    public boolean isCopyVersionRunning(String projectSlug,
            String versionSlug) {
        CopyVersionTaskHandle handle =
                getCopyVersionProcessHandle(projectSlug, versionSlug);
        return handle != null && !handle.isDone();
    }

    /**
     * Key used for copy version task
     *
     */
    public static final class CopyVersionKey implements
            AsyncTaskHandleManager.AsyncTaskKey {
        private static final String KEY_NAME = "copyVersion";
        private static final long serialVersionUID = 3889349239078033373L;
        // target project identifier
        private final String projectSlug;
        // target version identifier
        private final String versionSlug;
        private final String id;

        /**
         *
         * @param projectSlug
         *            - target project identifier
         * @param versionSlug
         *            - target version identifier
         */
        public static CopyVersionKey getKey(String projectSlug,
                String versionSlug) {
            return new CopyVersionKey(projectSlug, versionSlug);
        }

        public String getProjectSlug() {
            return this.projectSlug;
        }

        public String getVersionSlug() {
            return this.versionSlug;
        }

        @java.beans.ConstructorProperties({ "projectSlug", "versionSlug" })
        public CopyVersionKey(final String projectSlug,
                final String versionSlug) {
            this.projectSlug = projectSlug;
            this.versionSlug = versionSlug;
            this.id = joinFields(KEY_NAME, projectSlug, versionSlug);
        }

        @Override
        public String id() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CopyVersionKey that = (CopyVersionKey) o;
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }
}
