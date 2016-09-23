package com.company;

/**
 * Created by LENOVO on 22/09/2016.
 */
public class Publication {
    private int id;
    private String pubKey;
    private String title;
    private String conference;
    private int year;
    private String journal;
    private boolean isArticle,isPhdThesis,isBook,isProceedings,isWebsite,isInCollection,isMasterThesis,isInProceedings;

    public Publication(int id,String pubKey,String title,String conference,int year, String journal,boolean isArticle,
    boolean isPhdThesis,boolean isBook,boolean isProceedings,boolean isWebsite,boolean isInCollection,boolean isMasterThesis,
                       boolean isInProceedings){
        this.id = id;
        this.pubKey = pubKey;
        this.title = title;
        this.conference= conference;
        this.year = year;
        this.journal = journal;
        this.isArticle = isArticle;
        this.isPhdThesis = isPhdThesis;
        this.isBook = isBook;
        this.isProceedings = isProceedings;
        this.isWebsite = isWebsite;
        this.isInCollection = isInCollection;
        this.isMasterThesis = isMasterThesis;
        this.isInProceedings = isInProceedings;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPubKey() {
        return pubKey;
    }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getConference() {
        return conference;
    }

    public void setConference(String conference) {
        this.conference = conference;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getJournal() {
        return journal;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }

    public boolean isArticle() {
        return isArticle;
    }

    public void setArticle(boolean article) {
        isArticle = article;
    }

    public boolean isPhdThesis() {
        return isPhdThesis;
    }

    public void setPhdThesis(boolean phdThesis) {
        isPhdThesis = phdThesis;
    }

    public boolean isBook() {
        return isBook;
    }

    public void setBook(boolean book) {
        isBook = book;
    }

    public boolean isProceedings() {
        return isProceedings;
    }

    public void setProceedings(boolean proceedings) {
        isProceedings = proceedings;
    }

    public boolean isWebsite() {
        return isWebsite;
    }

    public void setWebsite(boolean website) {
        isWebsite = website;
    }

    public boolean isInCollection() {
        return isInCollection;
    }

    public void setInCollection(boolean inCollection) {
        isInCollection = inCollection;
    }

    public boolean isMasterThesis() {
        return isMasterThesis;
    }

    public void setMasterThesis(boolean masterThesis) {
        isMasterThesis = masterThesis;
    }

    public boolean isInProceedings() {
        return isInProceedings;
    }

    public void setInProceedings(boolean inProceedings) {
        isInProceedings = inProceedings;
    }
}
