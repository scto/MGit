package xyz.realms.mgit.ui.fragments;

import xyz.realms.mgit.ui.explorer.RepoDetailActivity;

public abstract class RepoDetailFragment extends BaseFragment {

    public RepoDetailActivity getRawActivity() {
        return (RepoDetailActivity) super.getRawActivity();
    }

}
