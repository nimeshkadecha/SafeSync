package com.nimeshkadecha.safesync.ui.details;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nimeshkadecha.safesync.R;
import com.nimeshkadecha.safesync.databinding.FragmentBranchDetailsBinding;
import com.nimeshkadecha.safesync.databinding.FragmentNgoDetailBinding;

public class BranchDetails extends Fragment {

    private FragmentBranchDetailsBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBranchDetailsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        String NgoName = getArguments().getString("NgoName", "");

       String  NgoEmail = getArguments().getString("NgoEmail", "");
        Toast.makeText(getContext(), "Branch = "+NgoName, Toast.LENGTH_SHORT).show();

        return root;

    }
}