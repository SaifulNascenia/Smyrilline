
package com.mcp.smyrilline.model.parentmodel;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Up implements Serializable
{

    @SerializedName("embeddable")
    @Expose
    private boolean embeddable;
    @SerializedName("href")
    @Expose
    private String href;
    private final static long serialVersionUID = -4726274870498745360L;

    public boolean isEmbeddable() {
        return embeddable;
    }

    public void setEmbeddable(boolean embeddable) {
        this.embeddable = embeddable;
    }

    public Up withEmbeddable(boolean embeddable) {
        this.embeddable = embeddable;
        return this;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public Up withHref(String href) {
        this.href = href;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}