package com.example.androidchoi.jobdam.Model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Choi on 2015-11-04.
 */
public class QuestionData implements ChildData, Serializable {
//    private List<String> mQuestions = new ArrayList<String>();
//
//    public String getQuestion(int index) {
//        return mQuestions.get(index);
//    }
    private String question;
    private String limit;
    private ArrayList<MyCards> cardList = new ArrayList<MyCards>();
    public String getQuestion() {  return question; }
    public QuestionData() { }
    public ArrayList<MyCards> getCardList() { return cardList; }
}
