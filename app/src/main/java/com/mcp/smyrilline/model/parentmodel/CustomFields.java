
package com.mcp.smyrilline.model.parentmodel;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class CustomFields implements Serializable
{

    @SerializedName("mcp_should_synchronize")
    @Expose
    private List<String> mcpShouldSynchronize = null;
    private final static long serialVersionUID = 3379262011378142567L;

    public List<String> getMcpShouldSynchronize() {
        return mcpShouldSynchronize;
    }

    public void setMcpShouldSynchronize(List<String> mcpShouldSynchronize) {
        this.mcpShouldSynchronize = mcpShouldSynchronize;
    }

    public CustomFields withMcpShouldSynchronize(List<String> mcpShouldSynchronize) {
        this.mcpShouldSynchronize = mcpShouldSynchronize;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
