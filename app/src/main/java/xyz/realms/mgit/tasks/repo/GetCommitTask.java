package xyz.realms.mgit.tasks.repo;

import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;
import java.util.List;

import xyz.realms.mgit.database.Repo;
import xyz.realms.mgit.errors.StopTaskException;

public class GetCommitTask extends RepoOpTask {

    private final GetCommitCallback mCallback;
    private List<RevCommit> mResult;
    private final String mFile;

    public GetCommitTask(Repo repo, String file, GetCommitCallback callback) {
        super(repo,true);
        mFile = file;
        mCallback = callback;
    }

    public void executeTask() {
        execute();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return getCommitsList();
    }

    protected void onPostExecute(Boolean isSuccess) {
        super.onPostExecute(isSuccess);
        if (mCallback != null) {
            mCallback.postCommits(mResult);
        }
    }

    public boolean getCommitsList() {
        try {
            LogCommand cmd = mRepo.getGit().log();
            if (mFile != null)
                cmd.addPath(mFile);
            Iterable<RevCommit> commits = cmd.call();
            mResult = new ArrayList<RevCommit>();
            for (RevCommit commit : commits) {
                mResult.add(commit);
            }
        } catch (GitAPIException e) {
            setException(e);
            return false;
        } catch (StopTaskException e) {
            return false;
        } catch (Throwable e) {
            setException(e);
            return false;
        }
        return true;
    }

    public interface GetCommitCallback {
        void postCommits(List<RevCommit> commits);
    }

}
