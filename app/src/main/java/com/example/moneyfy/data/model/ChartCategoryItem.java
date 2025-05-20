package com.example.moneyfy.data.model;
public class ChartCategoryItem {     // 아이콘 리소스 ID (예: R.drawable.ic_add)
    private String categoryMethod; // 카테고리 및 결제수단 (예: "건강/현금")
    private String cost;           // 금액 문자열 (예: "3233000원")

    public ChartCategoryItem(String categoryMethod, String cost) {
        this.categoryMethod = categoryMethod;
        this.cost = cost;
    }

    public String getCategoryMethod() {
        return categoryMethod;
    }

    public String getCost() {
        return cost;
    }
}
