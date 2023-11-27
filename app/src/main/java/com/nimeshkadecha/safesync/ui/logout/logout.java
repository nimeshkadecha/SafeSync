package com.nimeshkadecha.safesync.ui.logout;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nimeshkadecha.safesync.MainActivity2;
import com.nimeshkadecha.safesync.R;
import com.nimeshkadecha.safesync.databinding.FragmentGalleryBinding;
import com.nimeshkadecha.safesync.databinding.FragmentLogoutBinding;

public class logout extends Fragment {

    private FragmentLogoutBinding binding;

    public static final String SHARED_PREFS = "sharedPrefs";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentLogoutBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SharedPreferences sp = getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("token", "");
        editor.putString("user_type", "");
        editor.putString("NGO_Email", "");
        editor.putString("Branch_Email", "");
        editor.apply();


        Toast.makeText(getActivity(), "You have been Logged Out!", Toast.LENGTH_SHORT).show();

        getActivity().finish();

        return root;

    }
}