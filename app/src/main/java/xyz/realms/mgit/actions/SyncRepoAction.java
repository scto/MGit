package xyz.realms.mgit.actions;

import android.os.Handler;
import android.os.Looper;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.IOException;

import xyz.realms.mgit.R;
import xyz.realms.mgit.database.Repo;
import xyz.realms.mgit.errors.StopTaskException;
import xyz.realms.mgit.tasks.repo.FetchTask;
import xyz.realms.mgit.ui.explorer.RepoDetailActivity;

public class SyncRepoAction extends RepoAction {
    private SyncStatus result;

    public SyncRepoAction(Repo repo, RepoDetailActivity activity) {
        super(repo, activity);
    }

    @Override
    public void execute() {
        try {
            Repository repository = mRepo.getGit().getRepository();
            String[] originRemotes = mRepo.getRemotes().toArray(new String[0]);
            FetchTask fetchTask = new FetchTask(originRemotes, mRepo,
                mActivity.new ProgressCallback(R.string.fetch_msg_init),
                fetchResult -> new Handler(Looper.getMainLooper()).post(() -> {
                try {
                    if (fetchResult == null) {
                        return;
                    }
                    Ref remoteRef = fetchResult.getAdvertisedRef("refs/heads/main");
                    Ref localRef = repository.findRef("refs/heads/main");
                    ObjectId remoteObjectid = remoteRef.getObjectId();
                    ObjectId localObjectid = localRef.getObjectId();
                    RevWalk revWalk = new RevWalk(repository);
                    RevCommit remoteCommit = revWalk.parseCommit(remoteObjectid);
                    RevCommit localCommit = revWalk.parseCommit(localObjectid);
                    System.out.print("本地最新提交: " + localCommit);
                    System.out.print("远程最新提交: " + remoteCommit);
                    if (localCommit.equals(remoteCommit)) {
                        this.result = SyncStatus.synced;
                    }
//                    其它情况。
                } catch (IOException e) {
                    throw new RuntimeException(e.fillInStackTrace());
                }
            }));
            fetchTask.executeTask();

        } catch (StopTaskException e) {
            throw new RuntimeException(e);
        }
        /*
         * 本地有修改，远程无修改 -> add暂存、commit提交、push推送
         * 本地无修改，远程有修改 -> 拉取
         * 本地无修改，远程无修改 -> 提示全部最新
         * AddAllAction、CommitAction
         * 本地有修改、远程有修改，极易产生冲突。暂存、拉取、在恢复暂存的时候有些文件版本不一样了必然导致冲突。
         */
//        try {
//            // 获取当前仓库的状态
//            Status status = mRepo.getGit().status().call();
//            // 检查是否有更改
//            if (status.hasUncommittedChanges()) {
////                不能通过action进行链式调用，即使不能重用action的代码。
////                只能通过task进行链式调用。
////                AddToStageTask addTask = new AddToStageTask(mRepo, ".",
// (isAddToStageTaskSuccess) -> {
////                    new CheckoutFileTask(mRepo, null, (isCheckoutFileTaskSuccess) -> {
////                        new RebaseTask(mRepo, "true", (isRebaseTaskSuccess) -> {
////                            new RebaseTask(mRepo, "true", null).execute();
////                        }).execute();
////                    }).execute();
////                });
////                addTask.executeTask();
//            } else {
//                mActivity.showToastMessage(R.string.alert_uncommitted_changes);
//            }
//        } catch (StopTaskException | GitAPIException e) {
//            throw new RuntimeException(e);
//        }
    }

    enum SyncStatus {
        synced, local_old, remote_old, conflict
    }
}
