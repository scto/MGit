package xyz.realms.mgit.actions;

import android.content.DialogInterface;

import xyz.realms.mgit.R;
import xyz.realms.mgit.database.Repo;
import xyz.realms.mgit.ui.RepoDetailActivity;

public class DeleteAction extends RepoAction {

    public DeleteAction(Repo repo, RepoDetailActivity activity) {
        super(repo, activity);
    }

    @Override
    public void execute() {
        mActivity.showMessageDialog(R.string.dialog_delete_repo_title,
            R.string.dialog_delete_repo_msg, R.string.label_delete,
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mRepo.deleteRepo();
                    mActivity.finish();
                }
            });
        mActivity.closeOperationDrawer();
    }
}
