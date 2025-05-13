package com.example.moneyfy.ui.advice;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyfy.R;

import java.util.ArrayList;
import java.util.List;

import com.example.moneyfy.data.model.ChatMessage;
public class AdviceFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_advice, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.rv_chat);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<ChatMessage> sampleMessages = new ArrayList<>();
        sampleMessages.add(new ChatMessage("안녕!", ChatMessage.TYPE_USER));
        sampleMessages.add(new ChatMessage("안녕하세요! 무엇을 도와드릴까요?", ChatMessage.TYPE_GPT));
        sampleMessages.add(new ChatMessage("가계부 추천해줘", ChatMessage.TYPE_USER));
        sampleMessages.add(new ChatMessage("Moneyfy 앱을 사용해보세요!", ChatMessage.TYPE_GPT));

        ChatAdapter adapter = new ChatAdapter(sampleMessages);
        recyclerView.setAdapter(adapter);

        return view;
    }


}
