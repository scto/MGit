package xyz.realms.mgit.actions;

import android.os.Handler;
import android.os.Looper;

import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
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
                        mActivity.showToastMessage("fetchResult = null, 无法获取远程或本地提交。");
                        return;
                    }

                    Ref remoteRef = fetchResult.getAdvertisedRef(mRepo.getBranchName());
                    if (remoteRef == null) {
                        mActivity.showToastMessage("remoteRef = null, 无法获取remoteCommit。");
                        return;
                    }

                    // 比较时间仅用于两种情况，仅本地有更新或仅远程有更新，但存在计算机时间错乱导致比原来的提交时间晚。
                    Status status = mRepo.getGit().status().call();
                    RevWalk revWalk = new RevWalk(repository);
                    ObjectId remoteHeadObjectID = remoteRef.getObjectId();
                    ObjectId localHeadObjectID = repository.resolve("HEAD");
                    RevCommit remoteHeadCommit = revWalk.parseCommit(remoteHeadObjectID);
                    RevCommit localHeadCommit = revWalk.parseCommit(localHeadObjectID);
                    if (localHeadCommit.equals(remoteHeadCommit)) {
                        this.result = SyncStatus.synced;
                    } else if (revWalk.isMergedInto(localHeadCommit, remoteHeadCommit)) {
                        this.result = SyncStatus.remote_new;
                    } else if (revWalk.isMergedInto(remoteHeadCommit, localHeadCommit)) {
                        this.result = SyncStatus.local_new;
                    } else {
                        this.result = SyncStatus.diverged;
                    }
                    boolean localChanged = status.hasUncommittedChanges() || !status.isClean();
                    switch (this.result) {
                        case synced -> {
                            mActivity.showToastMessage("远程和本地仓库的提交是同步的。");
                            if (localChanged) {
                                mActivity.showToastMessage("本地有未提交的更改。");
                                // 暂存 提交，推送
                            }
                        }
                        case local_new -> {
                            mActivity.showToastMessage("本地仓库有新的提交。");
                            if (localChanged) {
                                mActivity.showToastMessage("本地有未提交的更改。");
                                // 暂存、提交、推送，这时是推送多个commit。
                            }
                        }
                        case remote_new -> {
                            mActivity.showToastMessage("远程仓库有新的提交。");
                            if (localChanged) {
                                mActivity.showToastMessage("本地有未提交的更改。");
                                // 有两种情况，冲突和无冲突。建议在修改之前先拉取，即可避免这种情况。
                            }
                            // 拉取。
                        }
                        case diverged -> {
                            mActivity.showToastMessage("本地和远程仓库已经分叉。");
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e.fillInStackTrace());
                } catch (GitAPIException | StopTaskException e) {
                    throw new RuntimeException(e);
                }
            }));
            fetchTask.executeTask();

        } catch (StopTaskException e) {
            throw new RuntimeException(e);
        }

    }

    enum SyncStatus {
        synced, local_new, remote_new, diverged
    }
}
//    public void execute() {
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

//    private static String getConventionalCommitMessage(RevCommit commit) {
//        StringBuilder stringBuilder = new StringBuilder();
//        /*
//        commit 55ff76907496b4a701c7ff83457a3faba9f2e44b
//        Author:	Zacharia2 <1947114574@qq.com>
//        Date:	周二 12月 31 21:29:17 2024 +0800
//
//            1231
//        */
//
//        // Prepare the pieces
//        final String justTheAuthorNoTime =
//            commit.getAuthorIdent().toExternalString().split(">")[0] + ">";
//        final Instant commitInstant = Instant.ofEpochSecond(commit.getCommitTime());
//        final ZoneId zoneId = commit.getAuthorIdent().getTimeZone().toZoneId();
//        final ZonedDateTime authorDateTime = ZonedDateTime.ofInstant(commitInstant, zoneId);
//        final String gitDateTimeFormatString = "EEE MMM dd HH:mm:ss yyyy Z";
//        final String formattedDate =
//            authorDateTime.format(DateTimeFormatter.ofPattern(gitDateTimeFormatString));
//        final String tabbedCommitMessage =
//            Arrays.stream(commit.getFullMessage().split("\\r?\\n")) // split it up by line
//            .map(s -> "\t" + s + "\n") // add a tab on each line
//            .collect(Collectors.joining()); // put it back together
//
//        // Put pieces together
//        stringBuilder.append("commit ").append(commit.getName()).append("\n").append
//        ("Author:\t").append(justTheAuthorNoTime).append("\n").append("Date:\t").append
//        (formattedDate).append("\n\n").append(tabbedCommitMessage);
//
//        try {
//            String remote = "https://github.com/Zacharia2/KCMS.git";
//            System.out.println("Listing remote repository " + remote);
//            Collection<Ref> refs =
//                Git.lsRemoteRepository().setHeads(true).setTags(true).setRemote(remote).call();
//            for (Ref ref : refs) {
//                System.out.println("Ref: " + ref);
//            }
////            Ref: Ref[refs/heads/main=55ff76907496b4a701c7ff83457a3faba9f2e44b(-1)]
//
//            final Map<String, Ref> map =
//                Git.lsRemoteRepository().setHeads(true).setTags(true).setRemote(remote)
//                .callAsMap();
//            System.out.println("As map");
//            for (Map.Entry<String, Ref> entry : map.entrySet()) {
//                System.out.println("Key: " + entry.getKey() + ", Ref: " + entry.getValue());
//            }
////            Key: refs/heads/main, Ref:
////            Ref[refs/heads/main=55ff76907496b4a701c7ff83457a3faba9f2e44b(-1)]
//
//            refs = Git.lsRemoteRepository().setRemote(remote).call();
//            System.out.println("All refs");
//            for (Ref ref : refs) {
//                System.out.println("Ref: " + ref);
//            }
////            Ref: SymbolicRef[HEAD -> refs/heads/main=55ff76907496b4a701c7ff83457a3faba9f2e44b
// (-1)]
////            Ref: Ref[refs/heads/main=55ff76907496b4a701c7ff83457a3faba9f2e44b(-1)]
////            Ref: Ref[refs/pull/1/head=45a00df312fe475fa82e0a96dd0b00518a7c1abc(-1)]
//        } catch (GitAPIException e) {
//            throw new RuntimeException(e);
//        }
//
//        return stringBuilder.toString();
//    }

//https://github.com/centic9/jgit-cookbook/blob/master/src/main/java/org/dstadler/jgit/porcelain/ShowLog.java
//                    Iterable<RevCommit> commits = mRepo.getGit().log().all().call();
//                    Iterable<RevCommit> commits = mRepo.getGit().log()
//                        .add(repository.resolve("remotes/origin/main"))
//                        .call();
//                    for (RevCommit commit : commits) {
//                        System.out.println(getConventionalCommitMessage(commit));
//                    }


//                    ArrayList<String> remoteBranchNames = mRepo.getRemoteBranchName();
//                    Collection<Ref> ref = fetchResult.getAdvertisedRefs();
//                    for (String refname : remoteBranchNames) {
//                        remoteRef = fetchResult.getAdvertisedRef(refname);
//                        if (remoteRef != null) {
//                            break;
//                        }
//                    }
//                    Ref localRef = repository.findRef(mRepo.getBranchName());
