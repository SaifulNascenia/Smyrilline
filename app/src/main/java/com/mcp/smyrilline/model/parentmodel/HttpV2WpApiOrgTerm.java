
package com.mcp.smyrilline.model.parentmodel;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class HttpV2WpApiOrgTerm implements Serializable
{

    @SerializedName("taxonomy")
    @Expose
    private String taxonomy;
    @SerializedName("embeddable")
    @Expose
    private boolean embeddable;
    @SerializedName("href")
    @Expose
    private String href;
    private final static long serialVersionUID = 7215458500757418899L;

    public String getTaxonomy() {
        return taxonomy;
    }

    public void setTaxonomy(String taxonomy) {
        this.taxonomy = taxonomy;
    }

    public HttpV2WpApiOrgTerm withTaxonomy(String taxonomy) {
        this.taxonomy = taxonomy;
        return this;
    }

    public boolean isEmbeddable() {
        return embeddable;
    }

    public void setEmbeddable(boolean embeddable) {
        this.embeddable = embeddable;
    }

    public HttpV2WpApiOrgTerm withEmbeddable(boolean embeddable) {
        this.embeddable = embeddable;
        return this;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public HttpV2WpApiOrgTerm withHref(String href) {
        this.href = href;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
