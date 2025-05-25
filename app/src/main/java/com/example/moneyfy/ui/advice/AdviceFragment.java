package com.example.moneyfy.ui.advice;


import android.os.Bundle;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyfy.R;

import java.util.ArrayList;
import java.util.List;

import com.example.moneyfy.data.model.ChatMessage;
import java.util.List;


public class AdviceFragment extends Fragment {
    private AdviceViewModel viewModel;
    private ChatAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_advice, container, false);

        // 1. RecyclerView 초기화
        RecyclerView recyclerView = view.findViewById(R.id.rv_chat);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // List<ChatMessage> messages = new ArrayList<>();
        adapter = new ChatAdapter(new java.util.ArrayList<>());
        recyclerView.setAdapter(adapter);

        // 2. ViewModel 연결
        viewModel = new ViewModelProvider(this).get(AdviceViewModel.class);

        // 3. LiveData 관찰 → 메시지 UI 업데이트
        viewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            adapter.setMessages(messages);
            adapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(messages.size() - 1);
        });

        // 4. GPT 조언 요청
        viewModel.fetchGptAdvice();

        return view;
    }
}
