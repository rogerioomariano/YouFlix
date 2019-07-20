package com.paradoxo.youflix.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.paradoxo.youflix.R;

public class SearchFragment extends Fragment {


    private View view;

    public SearchFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);

        configurarToolbar();

        return view;
    }

    private void configurarToolbar() {
        Toolbar toolbar = view.findViewById(R.id.buscaToolbar);
        toolbar.inflateMenu(R.menu.menu_search);

    }

}


















