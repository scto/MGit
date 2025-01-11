package xyz.realms.mgit.tasks;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.StringRes;

import org.eclipse.jgit.api.TransportCommand;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.util.concurrent.CompletableFuture;

import timber.log.Timber;
import xyz.realms.android.utils.BasicFunctions;
import xyz.realms.mgit.R;
import xyz.realms.mgit.database.Repo;
import xyz.realms.mgit.ui.SheimiFragmentActivity;

public abstract class MGitAsyncTask {
    protected int mErrorRes = 0;
    protected Throwable mException;
    protected Repo mRepo;
    protected MGitAsyncCallBack mGitAsyncCallBack;
    protected boolean mIsTaskAdded;
    private int mSuccessMsg = 0;
    private boolean mIsCanceled = false;
    private CompletableFuture<Void> completableExecuteTaskFuture;
    private String[] values;

    public MGitAsyncTask(Repo repo) {
        mRepo = repo;
        mIsTaskAdded = repo.addTask(this);
    }

    protected void onPostExecute(Boolean isSuccess) {
        mRepo.removeTask(this);
        if (!isSuccess && !isTaskCanceled()) {
            if (mException == null) {
                BasicFunctions.showError(BasicFunctions.getActiveActivity(), mErrorRes,
                    getErrorTitleRes());
            } else {
                BasicFunctions.showException(BasicFunctions.getActiveActivity(), mException,
                    mErrorRes, getErrorTitleRes());
            }
        }
        if (isSuccess && mSuccessMsg != 0) {
            BasicFunctions.getActiveActivity().showToastMessage(mSuccessMsg);
        }
    }

    protected void setSuccessMsg(int successMsg) {
        mSuccessMsg = successMsg;
    }

    protected void setCredentials(TransportCommand command) {
        String username = mRepo.getUsername();
        String password = mRepo.getPassword();

        if (username != null && password != null && !username.trim().isEmpty() && !password.trim().isEmpty()) {
            UsernamePasswordCredentialsProvider auth =
                new UsernamePasswordCredentialsProvider(username, password);
            command.setCredentialsProvider(auth);
        } else {
            Timber.d("no CredentialsProvider when no username/password provided");
        }

    }

    protected void handleAuthError(SheimiFragmentActivity.OnPasswordEntered onPassEntered) {
        String msg = mException.getMessage();
        Timber.w("clone Auth error: %s", msg);

        if (msg == null || ((!msg.contains("Auth fail")) && (!msg.toLowerCase().contains("auth")))) {
            return;
        }

        String errorInfo = null;
        if (msg.contains("Auth fail")) {
            errorInfo =
                BasicFunctions.getActiveActivity().getString(R.string.dialog_prompt_for_password_title_auth_fail);
        }
        BasicFunctions.getActiveActivity().promptForPassword(onPassEntered, errorInfo);
    }

    protected void setException(Throwable e) {
        Timber.e(e, "set exception");
        mException = e;
    }

    protected void setException(Throwable e, int errorRes) {
        Timber.e(e, "set error [%d] exception", errorRes);
        mException = e;
        mErrorRes = errorRes;
    }

    protected void setError(int errorRes) {
        Timber.e("set error res id: %d", errorRes);
        mErrorRes = errorRes;
    }

    @StringRes
    public int getErrorTitleRes() {
        return R.string.dialog_error_title;
    }

    public boolean isTaskCanceled() {
        return mIsCanceled;
    }

    public final CompletableFuture executeTask(Void... params) {
        if (mIsTaskAdded) {
            mGitAsyncCallBack.onPreExecute();
            completableExecuteTaskFuture = CompletableFuture.runAsync(() -> {
                boolean isSuccess = mGitAsyncCallBack.doInBackground(params);
                Handler uiThread = new Handler(Looper.getMainLooper());
                uiThread.post(() -> {
                    mGitAsyncCallBack.onPostExecute(isSuccess);
                    if (this.values != null && this.values.length != 0) {
                        mGitAsyncCallBack.onProgressUpdate(values);
                    }
                });
            });
            return completableExecuteTaskFuture;
        }
        BasicFunctions.getActiveActivity().showToastMessage(R.string.error_task_running);
        return completableExecuteTaskFuture;
    }

    public void cancelTask() {
        completableExecuteTaskFuture.cancel(false);
        mIsCanceled = completableExecuteTaskFuture.isCancelled();
    }

    protected final void publishProgress(String... values) {
        // 从异步线程doInBackground中调用，回到主线程执行。
        Handler uiThread = new Handler(Looper.getMainLooper());
        uiThread.post(() -> {
            this.values = values;
        });
    }

    public interface MGitAsyncCallBack {
        void onPreExecute();

        boolean doInBackground(Void... params);

        void onProgressUpdate(String... progress);

        void onPostExecute(Boolean isSuccess);
    }

    public interface MGitPostCallBack {
        void onPostExecute(Boolean isSuccess);
    }

    public class BasicProgressMonitor implements ProgressMonitor {

        private int mTotalWork;
        private int mWorkDone;
        private int mLastProgress;
        private String mTitle;

        @Override
        public void start(int i) {
        }

        @Override
        public void beginTask(String title, int totalWork) {
            mTotalWork = totalWork;
            mWorkDone = 0;
            mLastProgress = 0;
            if (title != null) {
                mTitle = title;
            }
            setProgress();
        }

        @Override
        public void update(int i) {
            mWorkDone += i;
            if (mTotalWork != ProgressMonitor.UNKNOWN && mTotalWork != 0 && mTotalWork - mLastProgress >= 1) {
                setProgress();
                mLastProgress = mWorkDone;
            }
        }

        @Override
        public void endTask() {
        }

        @Override
        public boolean isCancelled() {
            return isTaskCanceled();
        }

        @Override
        public void showDuration(boolean enabled) {
        }

        private void setProgress() {
            String msg = mTitle;
            int showedWorkDown = Math.min(mWorkDone, mTotalWork);
            int progress = 0;
            String rightHint = "0/0";
            String leftHint = "0%";
            if (mTotalWork != 0) {
                progress = 100 * showedWorkDown / mTotalWork;
                rightHint = showedWorkDown + "/" + mTotalWork;
                leftHint = progress + "%";
            }
            //  将进度传递从异步线程给主线程里面。
            publishProgress(msg, leftHint, rightHint, Integer.toString(progress));
        }
    }
}
