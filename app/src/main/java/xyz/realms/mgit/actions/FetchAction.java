package xyz.realms.mgit.actions;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

import java.util.ArrayList;

import xyz.realms.mgit.R;
import xyz.realms.mgit.database.Repo;
import xyz.realms.mgit.tasks.repo.FetchTask;
import xyz.realms.mgit.ui.RepoDetailActivity;
import xyz.realms.mgit.ui.dialogs.DummyDialogListener;

public class FetchAction extends RepoAction {
    public FetchAction(Repo repo, RepoDetailActivity activity) {
        super(repo, activity);
    }

    @Override
    public void execute() {
        fetchDialog().show();
        mActivity.closeOperationDrawer();
    }

    private void fetch(String[] remotes) {
        final FetchTask fetchTask = new FetchTask(remotes, mRepo, mActivity.new ProgressCallback(R.string.fetch_msg_init));
        fetchTask.executeTask();
    }

    private Dialog fetchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        final String[] originRemotes = mRepo.getRemotes().toArray(new String[0]);
        final ArrayList<String> remotes = new ArrayList<>();
        return builder.setTitle(R.string.dialog_fetch_title)
            .setMultiChoiceItems(originRemotes, null, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int index, boolean isChecked) {
                    if (isChecked) {
                        remotes.add(originRemotes[index]);
                    } else {
                        for (int i = 0; i < remotes.size(); ++i) {
                            if (remotes.get(i) == originRemotes[index]) {
                                remotes.remove(i);
                            }
                        }
                    }
                }
            })
            .setPositiveButton(R.string.dialog_fetch_positive_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    fetch(remotes.toArray(new String[0]));
                }
            })
            .setNeutralButton(R.string.dialog_fetch_all_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    fetch(originRemotes);
                }
            })
            .setNegativeButton(android.R.string.cancel, new DummyDialogListener())
            .create();
    }
}
