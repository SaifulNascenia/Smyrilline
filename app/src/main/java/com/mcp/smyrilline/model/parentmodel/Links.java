
package com.mcp.smyrilline.model.parentmodel;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Links implements Serializable
{

    @SerializedName("self")
    @Expose
    private List<Self> self = null;
    @SerializedName("collection")
    @Expose
    private List<Collection> collection = null;
    @SerializedName("author")
    @Expose
    private List<Author> author = null;
    @SerializedName("replies")
    @Expose
    private List<Reply> replies = null;
    @SerializedName("version-history")
    @Expose
    private List<VersionHistory> versionHistory = null;
    @SerializedName("up")
    @Expose
    private List<Up> up = null;
    @SerializedName("http://v2.wp-api.org/attachment")
    @Expose
    private List<HttpV2WpApiOrgAttachment> httpV2WpApiOrgAttachment = null;
    @SerializedName("http://v2.wp-api.org/term")
    @Expose
    private List<HttpV2WpApiOrgTerm> httpV2WpApiOrgTerm = null;
    @SerializedName("http://v2.wp-api.org/meta")
    @Expose
    private List<HttpV2WpApiOrgMetum> httpV2WpApiOrgMeta = null;
    private final static long serialVersionUID = 7557261145457199652L;

    public List<Self> getSelf() {
        return self;
    }

    public void setSelf(List<Self> self) {
        this.self = self;
    }

    public Links withSelf(List<Self> self) {
        this.self = self;
        return this;
    }

    public List<Collection> getCollection() {
        return collection;
    }

    public void setCollection(List<Collection> collection) {
        this.collection = collection;
    }

    public Links withCollection(List<Collection> collection) {
        this.collection = collection;
        return this;
    }

    public List<Author> getAuthor() {
        return author;
    }

    public void setAuthor(List<Author> author) {
        this.author = author;
    }

    public Links withAuthor(List<Author> author) {
        this.author = author;
        return this;
    }

    public List<Reply> getReplies() {
        return replies;
    }

    public void setReplies(List<Reply> replies) {
        this.replies = replies;
    }

    public Links withReplies(List<Reply> replies) {
        this.replies = replies;
        return this;
    }

    public List<VersionHistory> getVersionHistory() {
        return versionHistory;
    }

    public void setVersionHistory(List<VersionHistory> versionHistory) {
        this.versionHistory = versionHistory;
    }

    public Links withVersionHistory(List<VersionHistory> versionHistory) {
        this.versionHistory = versionHistory;
        return this;
    }

    public List<Up> getUp() {
        return up;
    }

    public void setUp(List<Up> up) {
        this.up = up;
    }

    public Links withUp(List<Up> up) {
        this.up = up;
        return this;
    }

    public List<HttpV2WpApiOrgAttachment> getHttpV2WpApiOrgAttachment() {
        return httpV2WpApiOrgAttachment;
    }

    public void setHttpV2WpApiOrgAttachment(List<HttpV2WpApiOrgAttachment> httpV2WpApiOrgAttachment) {
        this.httpV2WpApiOrgAttachment = httpV2WpApiOrgAttachment;
    }

    public Links withHttpV2WpApiOrgAttachment(List<HttpV2WpApiOrgAttachment> httpV2WpApiOrgAttachment) {
        this.httpV2WpApiOrgAttachment = httpV2WpApiOrgAttachment;
        return this;
    }

    public List<HttpV2WpApiOrgTerm> getHttpV2WpApiOrgTerm() {
        return httpV2WpApiOrgTerm;
    }

    public void setHttpV2WpApiOrgTerm(List<HttpV2WpApiOrgTerm> httpV2WpApiOrgTerm) {
        this.httpV2WpApiOrgTerm = httpV2WpApiOrgTerm;
    }

    public Links withHttpV2WpApiOrgTerm(List<HttpV2WpApiOrgTerm> httpV2WpApiOrgTerm) {
        this.httpV2WpApiOrgTerm = httpV2WpApiOrgTerm;
        return this;
    }

    public List<HttpV2WpApiOrgMetum> getHttpV2WpApiOrgMeta() {
        return httpV2WpApiOrgMeta;
    }

    public void setHttpV2WpApiOrgMeta(List<HttpV2WpApiOrgMetum> httpV2WpApiOrgMeta) {
        this.httpV2WpApiOrgMeta = httpV2WpApiOrgMeta;
    }

    public Links withHttpV2WpApiOrgMeta(List<HttpV2WpApiOrgMetum> httpV2WpApiOrgMeta) {
        this.httpV2WpApiOrgMeta = httpV2WpApiOrgMeta;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
