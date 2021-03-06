package com.example.androidchoi.jobdam.Model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Choi on 2015-11-18.
 */
public class Article implements Serializable {

    private String user_id; // 게시글 작성한 유저 id
    private int emotionIndex; // 감정 인덱스
    private boolean likebool; // 유저가 해당 게시글을 좋아요 했는지 안했는지 여부
    private String content; // 게시글 내용
    private int like_cnt; // 게시글 좋아요 수
    private ArrayList<String> like_user = new ArrayList<String>();
    private long writeTimeStamp; //  게시글 작성 시간

    public Article() {
        CurrentTime currentTime = new CurrentTime();
        writeTimeStamp = Calendar.getInstance().getTimeInMillis();
        emotionIndex = 0;
        like_cnt = 0;
        content = "";
        likebool = false;
    }

    public String getUser(){return user_id;}
    public String getContent() { return content; }
    public int getLikeCount() { return like_cnt; }
    public boolean getLikeBool(){ return likebool;}
    public int getEmotionIndex(){
        return emotionIndex;
    }
    public long getWriteTimeStamp() {return writeTimeStamp;}
    public void setArticle(Article article){
        setArticle(article.getUser(), article.emotionIndex,
                article.getLikeBool(), article.getContent(), article.getLikeCount());
    }
    public void setArticle(String user_id,
                           int emotionIndex,
                           boolean likebool,
                           String content,
                           int like_cnt){
        this.user_id = user_id;
        this.emotionIndex = emotionIndex;
        this.likebool = likebool;
        this.content = content;
        this.like_cnt = like_cnt;
    }
}
