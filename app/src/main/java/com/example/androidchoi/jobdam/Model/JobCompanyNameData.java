package com.example.androidchoi.jobdam.Model;

import com.begentgroup.xmlparser.DefaultValueField;
import com.begentgroup.xmlparser.SerializedName;

import java.io.Serializable;

/**
 * Created by Choi on 2015-11-09.
 */
public class JobCompanyNameData implements Serializable {
    @SerializedName("href")
    private String link;

    @DefaultValueField
    private String value;

    public String getLink() {
        return link;
    }
    public String getValue() {
        return value;
    }

    public JobCompanyNameData(){
        link = "";
        value = "";
    }

}
