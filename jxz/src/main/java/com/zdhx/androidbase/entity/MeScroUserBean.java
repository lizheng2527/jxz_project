
package com.zdhx.androidbase.entity;

import java.io.Serializable;

/**
 * 用户实体类
 */
public class MeScroUserBean implements Serializable {
    private String scro;
    private String scroNumbers;
    private String ranking;
    private String rankingNumbers;


    public MeScroUserBean() {
    }

    public MeScroUserBean(String scro, String scroNumbers, String ranking, String rankingNumbers) {
        this.scro = scro;
        this.scroNumbers = scroNumbers;
        this.ranking = ranking;
        this.rankingNumbers = rankingNumbers;
    }

    public String getScro() {
        return scro;
    }

    public void setScro(String scro) {
        this.scro = scro;
    }

    public String getScroNumbers() {
        return scroNumbers;
    }

    public void setScroNumbers(String scroNumbers) {
        this.scroNumbers = scroNumbers;
    }

    public String getRanking() {
        return ranking;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public String getRankingNumbers() {
        return rankingNumbers;
    }

    public void setRankingNumbers(String rankingNumbers) {
        this.rankingNumbers = rankingNumbers;
    }
}
