package com.xinglan.mgit.activities.delegate;

import com.xinglan.android.utils.FsUtils;
import com.xinglan.mgit.activities.RepoDetailActivity;
import com.xinglan.mgit.activities.delegate.actions.AddAllAction;
import com.xinglan.mgit.activities.delegate.actions.AddRemoteAction;
import com.xinglan.mgit.activities.delegate.actions.CherryPickAction;
import com.xinglan.mgit.activities.delegate.actions.CommitAction;
import com.xinglan.mgit.activities.delegate.actions.CommitPushAction;
import com.xinglan.mgit.activities.delegate.actions.ConfigAction;
import com.xinglan.mgit.activities.delegate.actions.DeleteAction;
import com.xinglan.mgit.activities.delegate.actions.DiffAction;
import com.xinglan.mgit.activities.delegate.actions.FetchAction;
import com.xinglan.mgit.activities.delegate.actions.MergeAction;
import com.xinglan.mgit.activities.delegate.actions.NewBranchAction;
import com.xinglan.mgit.activities.delegate.actions.NewDirAction;
import com.xinglan.mgit.activities.delegate.actions.NewFileAction;
import com.xinglan.mgit.activities.delegate.actions.PullAction;
import com.xinglan.mgit.activities.delegate.actions.PushAction;
import com.xinglan.mgit.activities.delegate.actions.RawConfigAction;
import com.xinglan.mgit.activities.delegate.actions.RebaseAction;
import com.xinglan.mgit.activities.delegate.actions.RemoveRemoteAction;
import com.xinglan.mgit.activities.delegate.actions.RepoAction;
import com.xinglan.mgit.activities.delegate.actions.ResetAction;
import com.xinglan.mgit.activities.delegate.actions.UndoAction;
import com.xinglan.mgit.database.models.Repo;
import com.xinglan.mgit.tasks.SheimiAsyncTask.AsyncTaskPostCallback;
import com.xinglan.mgit.tasks.repo.AddToStageTask;
import com.xinglan.mgit.tasks.repo.CheckoutFileTask;
import com.xinglan.mgit.tasks.repo.CheckoutTask;
import com.xinglan.mgit.tasks.repo.DeleteFileFromRepoTask;
import com.xinglan.mgit.tasks.repo.MergeTask;
import com.xinglan.mgit.tasks.repo.UpdateIndexTask;

import org.eclipse.jgit.lib.Ref;

import java.io.File;
import java.util.ArrayList;

public class RepoOperationDelegate {
    private Repo mRepo;
    private RepoDetailActivity mActivity;
    private ArrayList<RepoAction> mActions = new ArrayList<>();

    public RepoOperationDelegate(Repo repo, RepoDetailActivity activity) {
        mRepo = repo;
        mActivity = activity;
        initActions();
    }

    private void initActions() {
        mActions.add(new NewBranchAction(mRepo, mActivity));
        mActions.add(new PullAction(mRepo, mActivity));
        mActions.add(new PushAction(mRepo, mActivity));
        mActions.add(new AddAllAction(mRepo,mActivity));
        mActions.add(new CommitAction(mRepo, mActivity));
        mActions.add(new CommitPushAction(mRepo, mActivity));
        mActions.add(new UndoAction(mRepo, mActivity));
        mActions.add(new ResetAction(mRepo, mActivity));
        mActions.add(new MergeAction(mRepo, mActivity));
        mActions.add(new FetchAction(mRepo, mActivity));
        mActions.add(new RebaseAction(mRepo, mActivity));
        mActions.add(new CherryPickAction(mRepo, mActivity));
        mActions.add(new DiffAction(mRepo, mActivity));
        mActions.add(new NewFileAction(mRepo, mActivity));
        mActions.add(new NewDirAction(mRepo, mActivity));
        mActions.add(new AddRemoteAction(mRepo, mActivity));
        mActions.add(new RemoveRemoteAction(mRepo, mActivity));
        mActions.add(new DeleteAction(mRepo, mActivity));
        mActions.add(new RawConfigAction(mRepo, mActivity));
        mActions.add(new ConfigAction(mRepo, mActivity));
    }

    public void executeAction(int key) {
        RepoAction action = mActions.get(key);
        if (action == null)
            return;
        action.execute();
    }

    public void checkoutCommit(final String commitName) {
        CheckoutTask checkoutTask = new CheckoutTask(mRepo, commitName,
            null, new AsyncTaskPostCallback() {
            @Override
            public void onPostExecute(Boolean isSuccess) {
                mActivity.reset(commitName);
            }
        });
        checkoutTask.executeTask();
    }

    public void checkoutCommit(final String commitName, final String branch) {
        CheckoutTask checkoutTask = new CheckoutTask(mRepo, commitName, branch,
            new AsyncTaskPostCallback() {
                @Override
                public void onPostExecute(Boolean isSuccess) {
                    mActivity.reset(branch);
                }
            });
        checkoutTask.executeTask();
    }

    public void mergeBranch(final Ref commit, final String ffModeStr,
                            final boolean autoCommit) {
        MergeTask mergeTask = new MergeTask(mRepo, commit, ffModeStr,
            autoCommit, new AsyncTaskPostCallback() {
            @Override
            public void onPostExecute(Boolean isSuccess) {
                mActivity.reset();
            }
        });
        mergeTask.executeTask();
    }

    public void addToStage(String filepath) {
        String relative = getRelativePath(filepath);
        AddToStageTask addToStageTask = new AddToStageTask(mRepo, relative, mActivity);
        addToStageTask.executeTask();
    }

    public void checkoutFile(String filepath) {
        String relative = getRelativePath(filepath);
        CheckoutFileTask task = new CheckoutFileTask(mRepo, relative, null);
        task.executeTask();
    }

    public void deleteFileFromRepo(String filepath, DeleteFileFromRepoTask.DeleteOperationType deleteOperationType) {
        String relative = getRelativePath(filepath);
        DeleteFileFromRepoTask task = new DeleteFileFromRepoTask(mRepo,
            relative, deleteOperationType, new AsyncTaskPostCallback() {
            @Override
            public void onPostExecute(Boolean isSuccess) {
                // TODO Auto-generated method stub
                mActivity.getFilesFragment().reset();
            }
        });
        task.executeTask();
    }

    private String getRelativePath(String filepath) {
        File base = mRepo.getDir();
        return FsUtils.getRelativePath(new File(filepath), base);
    }


    public void updateIndex(final String mFilePath, final int newMode) {
        String relative = getRelativePath(mFilePath);
        UpdateIndexTask task = new UpdateIndexTask(mRepo, relative, newMode);
        task.executeTask();
    }
}
