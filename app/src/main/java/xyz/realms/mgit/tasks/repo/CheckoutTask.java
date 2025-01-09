package xyz.realms.mgit.tasks.repo;

import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;

import xyz.realms.mgit.database.Repo;
import xyz.realms.mgit.errors.StopTaskException;
import xyz.realms.mgit.tasks.MGitAsyncTask;

public class CheckoutTask extends MGitAsyncTask implements MGitAsyncTask.MGitAsyncCallBack {

    private final MGitAsyncPostCallBack mCallback;
    private final String mCommitName;
    private final String mBranch;

    public CheckoutTask(Repo repo, String name, String branch, MGitAsyncPostCallBack callback) {
        super(repo);
        mGitAsyncCallBack = this;
        mCallback = callback;
        mCommitName = name;
        mBranch = branch;
    }

    @Override
    public void onPreExecute() {
    }

    @Override
    public boolean doInBackground(Void... params) {
        return checkout(mCommitName, mBranch);
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

    public boolean checkout(String name, String newBranch) {
        try {
            if (name == null) {
                checkoutNewBranch(newBranch);
            } else {
                if (Repo.COMMIT_TYPE_REMOTE == Repo.getCommitType(name)) {
                    checkoutFromRemote(name, newBranch == null || newBranch.equals("") ?
                        Repo.getCommitName(name) : newBranch);
                } else if (newBranch == null || newBranch.equals("")) {
                    checkoutFromLocal(name);
                } else {
                    checkoutFromLocal(name, newBranch);
                }
            }
        } catch (StopTaskException e) {
            return false;
        } catch (GitAPIException e) {
            setException(mException);
            return false;
        } catch (JGitInternalException e) {
            setException(mException);
            return false;
        } catch (Throwable e) {
            setException(mException);
            return false;
        }
        mRepo.updateLatestCommitInfo();
        return true;
    }

    public void checkoutNewBranch(String name) throws GitAPIException, JGitInternalException,
        StopTaskException {
        mRepo.getGit().checkout().setName(name).setCreateBranch(true).call();
    }

    public void checkoutFromLocal(String name) throws GitAPIException, JGitInternalException,
        StopTaskException {
        mRepo.getGit().checkout().setName(name).call();
    }

    public void checkoutFromLocal(String name, String branch) throws GitAPIException,
        JGitInternalException, StopTaskException {
        mRepo.getGit().checkout().setCreateBranch(true).setName(branch).setStartPoint(name).call();
    }

    public void checkoutFromRemote(String remoteBranchName, String branchName) throws GitAPIException, JGitInternalException, StopTaskException {
        mRepo.getGit().checkout().setCreateBranch(true).setName(branchName).setStartPoint(remoteBranchName).call();
        mRepo.getGit().branchCreate().setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM).setStartPoint(remoteBranchName).setName(branchName).setForce(true).call();
    }


}
