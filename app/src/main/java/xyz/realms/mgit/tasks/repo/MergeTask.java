package xyz.realms.mgit.tasks.repo;

import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;

import xyz.realms.android.utils.BasicFunctions;
import xyz.realms.mgit.R;
import xyz.realms.mgit.database.Repo;
import xyz.realms.mgit.errors.StopTaskException;
import xyz.realms.mgit.tasks.MGitAsyncTask;

public class MergeTask extends MGitAsyncTask implements MGitAsyncTask.MGitAsyncCallBack {

    private final MGitAsyncPostCallBack mCallback;
    private final Ref mCommit;
    private final String mFFModeStr;
    private final boolean mAutoCommit;

    public MergeTask(Repo repo, Ref commit, String ffModeStr, boolean autoCommit,
                     MGitAsyncPostCallBack callback) {
        super(repo);
        mGitAsyncCallBack = this;
        mCallback = callback;
        mCommit = commit;
        mFFModeStr = ffModeStr;
        mAutoCommit = autoCommit;

    }

    @Override
    public boolean doInBackground(Void... params) {
        return mergeBranch();
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onProgressUpdate(String... progress) {

    }

    @Override
    public void onPostExecute(Boolean isSuccess) {
        super.onPostExecute(isSuccess);
        if (mCallback != null) {
            mCallback.onPostExecute(isSuccess);
        }
    }

    public boolean mergeBranch() {
        String[] stringArray =
            BasicFunctions.getActiveActivity().getResources().getStringArray(R.array.merge_ff_type);
        MergeCommand.FastForwardMode ffMode = MergeCommand.FastForwardMode.FF;
        if (mFFModeStr.equals(stringArray[1])) {
            // FF Only
            ffMode = MergeCommand.FastForwardMode.FF_ONLY;
        } else if (mFFModeStr.equals(stringArray[2])) {
            // No FF
            ffMode = MergeCommand.FastForwardMode.NO_FF;
        }
        try {
            mRepo.getGit().merge().include(mCommit).setFastForward(ffMode).call();
        } catch (GitAPIException e) {
            setException(e);
            return false;
        } catch (StopTaskException e) {
            return false;
        } catch (Throwable e) {
            setException(e);
            return false;
        }
        if (mAutoCommit) {
            String b1 = mRepo.getBranchName();
            String b2 = mCommit.getName();
            String msg = null;
            if (b1 == null) {
                msg = String.format("Merge branch '%s'", Repo.getCommitDisplayName(b2));
            } else {
                msg = String.format("Merge branch '%s' into %s", Repo.getCommitDisplayName(b2),
                    Repo.getCommitDisplayName(b1));
            }
            try {
                CommitChangesTask.commit(mRepo, false, false, msg, null, null);
            } catch (GitAPIException e) {
                setException(e);
                return false;
            } catch (StopTaskException e) {
                return false;
            } catch (Throwable e) {
                setException(e);
                return false;
            }
        }
        mRepo.updateLatestCommitInfo();
        return true;
    }
}
