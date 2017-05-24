
package com.mcp.smyrilline.model.parentmodel;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Excerpt implements Serializable
{

    @SerializedName("rendered")
    @Expose
    private String rendered;
    private final static long serialVersionUID = -8092079210629672479L;

    public String getRendered() {
        return rendered;
    }

    public void setRendered(String rendered) {
        this.rendered = rendered;
    }

    public Excerpt withRendered(String rendered) {
        this.rendered = rendered;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
