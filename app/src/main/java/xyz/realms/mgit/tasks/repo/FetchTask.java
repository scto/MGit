package xyz.realms.mgit.tasks.repo;

import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.TransportException;

import xyz.realms.mgit.R;
import xyz.realms.mgit.database.Repo;
import xyz.realms.mgit.errors.StopTaskException;
import xyz.realms.mgit.tasks.MGitAsyncTask;
import xyz.realms.mgit.tasks.RepoRemoteOpTask;
import xyz.realms.mgit.transport.ssh.SgitTransportCallback;
import xyz.realms.mgit.ui.RepoDetailActivity;

public class FetchTask extends RepoRemoteOpTask implements MGitAsyncTask.MGitAsyncCallBack {

    private final RepoDetailActivity.ProgressCallback mCallback;
    private final String[] mRemotes;

    public FetchTask(String[] remotes, Repo repo, RepoDetailActivity.ProgressCallback callback) {
        super(repo);
        mGitAsyncCallBack = this;
        mCallback = callback;
        mRemotes = remotes;
    }

    @Override
    public boolean doInBackground(Void... params) {
        boolean result = true;
        for (final String remote : mRemotes) {
            result = fetchRepo(remote) & result;
            if (mCallback != null) {
                result = mCallback.doInBackground(params) & result;
            }
        }
        return result;
    }

    @Override
    public void onProgressUpdate(String... progress) {
        if (mCallback != null) {
            mCallback.onProgressUpdate(progress);
        }
    }

    @Override
    public void onPreExecute() {
        if (mCallback != null) {
            mCallback.onPreExecute();
        }
    }

    @Override
    public void onPostExecute(Boolean isSuccess) {
        super.onPostExecute(isSuccess);
        if (mCallback != null) {
            mCallback.onPostExecute(isSuccess);
        }
    }

    private boolean fetchRepo(String remote) {
        Git git;
        try {
            git = mRepo.getGit();
        } catch (StopTaskException e) {
            return false;
        }

        final FetchCommand fetchCommand =
            git.fetch().setProgressMonitor(new BasicProgressMonitor()).setTransportConfigCallback(new SgitTransportCallback()).setRemote(remote);

        setCredentials(fetchCommand);

        try {
            fetchCommand.call();
        } catch (TransportException e) {
            setException(e);
            handleAuthError(this);
            return false;
        } catch (Exception e) {
            setException(e, R.string.error_pull_failed);
            return false;
        } catch (OutOfMemoryError e) {
            setException(e, R.string.error_out_of_memory);
            return false;
        } catch (Throwable e) {
            setException(e);
            return false;
        }
        mRepo.updateLatestCommitInfo();
        return true;
    }

    @Override
    public RepoRemoteOpTask getNewTask() {
        return new FetchTask(mRemotes, mRepo, mCallback);
    }
}
