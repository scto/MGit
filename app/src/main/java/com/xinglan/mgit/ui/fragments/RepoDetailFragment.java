package com.xinglan.mgit.ui.fragments;

import com.xinglan.mgit.ui.RepoDetailActivity;

public abstract class RepoDetailFragment extends BaseFragment {

    public RepoDetailActivity getRawActivity() {
        return (RepoDetailActivity) super.getRawActivity();
    }

}
