package xyz.realms.mgit.actions;

import android.content.Intent;

import xyz.realms.mgit.database.Repo;
import xyz.realms.mgit.ui.RepoDetailActivity;
import xyz.realms.mgit.ui.ViewFileActivity;

/**
 * Created by phcoder on 05.12.15.
 */
public class RawConfigAction extends RepoAction {

    public RawConfigAction(Repo repo, RepoDetailActivity activity) {
        super(repo, activity);
    }

    @Override
    public void execute() {
        Intent intent = new Intent(mActivity, ViewFileActivity.class);
        intent.putExtra(ViewFileActivity.TAG_FILE_NAME,
            mRepo.getDir().getAbsoluteFile() + "/.git/config");
        mActivity.startActivity(intent);
    }
}
