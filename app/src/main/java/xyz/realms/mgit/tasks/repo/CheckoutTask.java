package xyz.realms.mgit.tasks.repo;

import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import xyz.realms.mgit.database.Repo;
import xyz.realms.mgit.errors.StopTaskException;

public class CheckoutTask extends RepoOpTask {

    private final AsyncTaskPostCallback mCallback;
    private final String mCommitName;
    private final String mBranch;

    public CheckoutTask(Repo repo, String name, String branch, AsyncTaskPostCallback callback) {
        super(repo);
        mCallback = callback;
        mCommitName = name; //refs/heads/master
        mBranch = branch;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return checkout(mCommitName, mBranch);
    }

    protected void onPostExecute(Boolean isSuccess) {
        super.onPostExecute(isSuccess);
        if (mCallback != null) {
            mCallback.onPostExecute(isSuccess);
        }
    }

    public boolean checkout(String name, String newBranch) {
        List<String> checkoutFiles = new ArrayList<>();
        try {
            // 将小于50MB的文件添加到checkoutFiles列表中等待稀疏检出。
            Repository repository = mRepo.getGit().getRepository();
            RevWalk revPool = new RevWalk(repository);
            ObjectReader reader = revPool.getObjectReader();
            TreeWalk treeWalk = new TreeWalk(reader);
            treeWalk.addTree(repository.resolve("HEAD^{tree}"));
            treeWalk.setRecursive(true);
            while (treeWalk.next()) {
                ObjectLoader ol = reader.open(treeWalk.getObjectId(0));
                long fileSize = ol.getSize();
                if (fileSize <= 50 * 1024 * 1024) { // 文件大小小于等于50MB
                    checkoutFiles.add(treeWalk.getPathString());
                }
            }

            if (name == null) {
                checkoutNewBranch(newBranch);
            } else {
                if (Repo.COMMIT_TYPE_REMOTE == Repo.getCommitType(name)) {
                    checkoutFromRemote(name, newBranch == null || newBranch.isEmpty() ?
                        Repo.getCommitName(name) : newBranch);
                } else if (newBranch == null || newBranch.isEmpty()) {
                    //在克隆远程仓库时，只checkout小于50mb的文件。
                    checkoutFromLocal(name, checkoutFiles);
                } else {
                    checkoutFromLocal(name, newBranch);
                }
            }
        } catch (StopTaskException e) {
            return false;
        } catch (GitAPIException | JGitInternalException | IOException e) {
            setException(mException);
            return false;
        }
        mRepo.updateLatestCommitInfo();
        return true;
    }

    public void checkoutNewBranch(String name) throws GitAPIException,
        JGitInternalException, StopTaskException {
        mRepo.getGit().checkout().setName(name).setCreateBranch(true).call();
    }

    public void checkoutFromLocal(String name, List<String> checkoutFiles)
        throws GitAPIException, JGitInternalException, StopTaskException {
        //在克隆远程仓库时，只checkout小于50mb的文件。
        if (checkoutFiles.isEmpty()) return;
        mRepo.getGit().checkout().addPaths(checkoutFiles).setStartPoint(name).call();
    }

    public void checkoutFromLocal(String name, String branch) throws GitAPIException,
        JGitInternalException, StopTaskException {
        mRepo.getGit().checkout().setCreateBranch(true).setName(branch)
            .setStartPoint(name).call();
    }

    public void checkoutFromRemote(String remoteBranchName, String branchName)
        throws GitAPIException, JGitInternalException, StopTaskException {
        mRepo.getGit().checkout().setCreateBranch(true).setName(branchName)
            .setStartPoint(remoteBranchName).call();
        mRepo.getGit()
            .branchCreate()
            .setUpstreamMode(
                CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM)
            .setStartPoint(remoteBranchName).setName(branchName)
            .setForce(true).call();
    }
}
