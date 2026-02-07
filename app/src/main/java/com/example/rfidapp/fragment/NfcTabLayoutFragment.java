package com.example.rfidapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


import com.example.rfidapp.R;
import com.example.rfidapp.activity.MainActivity;
import com.example.rfidapp.databinding.FragmentNfcTabLayoutBinding;

import java.util.ArrayList;

public class NfcTabLayoutFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FragmentNfcTabLayoutBinding binding;
    public MainActivity mContext;
    private String mParam1;
    private String mParam2;

    public static NfcTabLayoutFragment newInstance(String str, String str2) {
        NfcTabLayoutFragment nfcTabLayoutFragment = new NfcTabLayoutFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, str);
        bundle.putString(ARG_PARAM2, str2);
        nfcTabLayoutFragment.setArguments(bundle);
        return nfcTabLayoutFragment;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            this.mParam1 = getArguments().getString(ARG_PARAM1);
            this.mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.binding = FragmentNfcTabLayoutBinding.inflate(getLayoutInflater(), viewGroup, false);
        this.mContext = (MainActivity) getActivity();
        prepareViewPager();
        this.binding.tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.m_green));
        return this.binding.getRoot();
    }

    private void prepareViewPager() {
        this.mContext.setTitle("NFC");
        if (this.binding.tabLayout != null && this.binding.viewPager != null) {
            ArrayList arrayList = new ArrayList();
            arrayList.add("Read");
            arrayList.add("Write");
            this.binding.viewPager.setAdapter(new TabLayoutAdapter(getChildFragmentManager(), arrayList));
            this.binding.tabLayout.setupWithViewPager(this.binding.viewPager);
        }
    }

    private static class TabLayoutAdapter extends FragmentPagerAdapter {
        private final ArrayList<String> titles;

        public TabLayoutAdapter(FragmentManager fragmentManager, ArrayList<String> arrayList) {
            super(fragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.titles = arrayList;
        }

        public Fragment getItem(int i) {
            if (i == 0) {
                return NfcReader.newInstance("Read Fragment", "");
            }
            return NfcWriter.newInstance("Write Fragment", "");
        }

        public int getCount() {
            return this.titles.size();
        }

        public CharSequence getPageTitle(int i) {
            return this.titles.get(i);
        }
    }
}
