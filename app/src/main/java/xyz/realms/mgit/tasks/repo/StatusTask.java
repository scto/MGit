package xyz.realms.mgit.tasks.repo;

import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;

import java.util.Set;

import xyz.realms.mgit.database.Repo;
import xyz.realms.mgit.errors.StopTaskException;
import xyz.realms.mgit.tasks.MGitAsyncTask;

public class StatusTask extends MGitAsyncTask implements MGitAsyncTask.MGitAsyncCallBack {

    private final GetStatusCallback mCallback;
    private final StringBuffer mResult = new StringBuffer();

    public StatusTask(Repo repo, GetStatusCallback callback) {
        super(repo);
        mGitAsyncCallBack = this;
        mCallback = callback;
    }

    @Override
    public boolean doInBackground(Void... params) {
        return status();
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
        if (mCallback != null && isSuccess) {
            mCallback.postStatus(mResult.toString());
        }
    }

    private boolean status() {
        try {
            Status status = mRepo.getGit().status().call();
            convertStatus(status);
        } catch (NoWorkTreeException e) {
            setException(e);
            return false;
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

    private void convertStatus(Status status) {
        if (!status.hasUncommittedChanges() && status.isClean()) {
            mResult.append("Nothing to commit, working directory clean");
            return;
        }
        // TODO if working dir not clean
        convertStatusSet("Added files:", status.getAdded());
        convertStatusSet("Changed files:", status.getChanged());
        convertStatusSet("Removed files:", status.getRemoved());
        convertStatusSet("Missing files:", status.getMissing());
        convertStatusSet("Modified files:", status.getModified());
        convertStatusSet("Conflicting files:", status.getConflicting());
        convertStatusSet("Untracked files:", status.getUntracked());

    }

    private void convertStatusSet(String type, Set<String> status) {
        if (status.isEmpty()) return;
        mResult.append(type);
        mResult.append("\n\n");
        for (String s : status) {
            mResult.append('\t');
            mResult.append(s);
            mResult.append('\n');
        }
        mResult.append("\n");
    }

    public interface GetStatusCallback {
        void postStatus(String result);
    }

}
