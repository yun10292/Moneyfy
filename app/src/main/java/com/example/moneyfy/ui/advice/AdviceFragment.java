package com.example.moneyfy.ui.advice;


import android.os.Bundle;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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
        TextView placeholder = view.findViewById(R.id.tv_placeholder);

        viewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            adapter.setMessages(messages);
            adapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(messages.size() - 1);

            // 메시지가 생기면 안내 문구 숨기기
            if (!messages.isEmpty()) {
                placeholder.setVisibility(View.GONE);
            }
        });

        // 5. 추천 버튼 클릭 이벤트 설정
        LinearLayout promptLayout = view.findViewById(R.id.prompt_container);
        setPromptButtonListeners(promptLayout);

        // 6. 입력창 처리
        EditText input = view.findViewById(R.id.et_message);
        view.findViewById(R.id.btn_send).setOnClickListener(v -> {
            String userInput = input.getText().toString();
            if (!userInput.trim().isEmpty()) {
                viewModel.handleUserPrompt(userInput, false);
                input.setText("");
            }
        });


//        // 4. GPT 조언 요청
//        viewModel.fetchGptAdvice();

        return view;
    }

    // 추천 버튼들을 위한 클릭 이벤트 설정 함수
    private void setPromptButtonListeners(View promptLayout) {
        if (promptLayout instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) promptLayout;
            for (int i = 0; i < group.getChildCount(); i++) {
                View child = group.getChildAt(i);
                if (child instanceof Button) {
                    child.setOnClickListener(v -> {
                        String keyword = ((Button) v).getText().toString();
                        viewModel.handleUserPrompt(keyword, true);
                    });
                }
            }
        }
    }
}
