
package com.mcp.smyrilline.model.parentmodel;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ParentModel implements Serializable
{

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("guid")
    @Expose
    private Guid guid;
    @SerializedName("modified")
    @Expose
    private String modified;
    @SerializedName("modified_gmt")
    @Expose
    private String modifiedGmt;
    @SerializedName("slug")
    @Expose
    private String slug;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("title")
    @Expose
    private Title title;
    @SerializedName("content")
    @Expose
    private Content content;
    @SerializedName("excerpt")
    @Expose
    private Excerpt excerpt;
    @SerializedName("author")
    @Expose
    private long author;
    @SerializedName("featured_image")
    @Expose
    private long featuredImage;
    @SerializedName("parent")
    @Expose
    private long parent;
    @SerializedName("menu_order")
    @Expose
    private long menuOrder;
    @SerializedName("comment_status")
    @Expose
    private String commentStatus;
    @SerializedName("ping_status")
    @Expose
    private String pingStatus;
    @SerializedName("template")
    @Expose
    private String template;
    @SerializedName("custom_fields")
    @Expose
    private CustomFields customFields;
    @SerializedName("featured_image_source_url")
    @Expose
    private String featuredImageSourceUrl;
    @SerializedName("_links")
    @Expose
    private Links links;
    private final static long serialVersionUID = 4953262856444897507L;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ParentModel withId(int id) {
        this.id = id;
        return this;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ParentModel withDate(String date) {
        this.date = date;
        return this;
    }

    public Guid getGuid() {
        return guid;
    }

    public void setGuid(Guid guid) {
        this.guid = guid;
    }

    public ParentModel withGuid(Guid guid) {
        this.guid = guid;
        return this;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public ParentModel withModified(String modified) {
        this.modified = modified;
        return this;
    }

    public String getModifiedGmt() {
        return modifiedGmt;
    }

    public void setModifiedGmt(String modifiedGmt) {
        this.modifiedGmt = modifiedGmt;
    }

    public ParentModel withModifiedGmt(String modifiedGmt) {
        this.modifiedGmt = modifiedGmt;
        return this;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public ParentModel withSlug(String slug) {
        this.slug = slug;
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ParentModel withType(String type) {
        this.type = type;
        return this;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public ParentModel withLink(String link) {
        this.link = link;
        return this;
    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public ParentModel withTitle(Title title) {
        this.title = title;
        return this;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public ParentModel withContent(Content content) {
        this.content = content;
        return this;
    }

    public Excerpt getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(Excerpt excerpt) {
        this.excerpt = excerpt;
    }

    public ParentModel withExcerpt(Excerpt excerpt) {
        this.excerpt = excerpt;
        return this;
    }

    public long getAuthor() {
        return author;
    }

    public void setAuthor(long author) {
        this.author = author;
    }

    public ParentModel withAuthor(long author) {
        this.author = author;
        return this;
    }

    public long getFeaturedImage() {
        return featuredImage;
    }

    public void setFeaturedImage(long featuredImage) {
        this.featuredImage = featuredImage;
    }

    public ParentModel withFeaturedImage(long featuredImage) {
        this.featuredImage = featuredImage;
        return this;
    }

    public long getParent() {
        return parent;
    }

    public void setParent(long parent) {
        this.parent = parent;
    }

    public ParentModel withParent(long parent) {
        this.parent = parent;
        return this;
    }

    public long getMenuOrder() {
        return menuOrder;
    }

    public void setMenuOrder(long menuOrder) {
        this.menuOrder = menuOrder;
    }

    public ParentModel withMenuOrder(long menuOrder) {
        this.menuOrder = menuOrder;
        return this;
    }

    public String getCommentStatus() {
        return commentStatus;
    }

    public void setCommentStatus(String commentStatus) {
        this.commentStatus = commentStatus;
    }

    public ParentModel withCommentStatus(String commentStatus) {
        this.commentStatus = commentStatus;
        return this;
    }

    public String getPingStatus() {
        return pingStatus;
    }

    public void setPingStatus(String pingStatus) {
        this.pingStatus = pingStatus;
    }

    public ParentModel withPingStatus(String pingStatus) {
        this.pingStatus = pingStatus;
        return this;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public ParentModel withTemplate(String template) {
        this.template = template;
        return this;
    }

    public CustomFields getCustomFields() {
        return customFields;
    }

    public void setCustomFields(CustomFields customFields) {
        this.customFields = customFields;
    }

    public ParentModel withCustomFields(CustomFields customFields) {
        this.customFields = customFields;
        return this;
    }

    public String getFeaturedImageSourceUrl() {
        return featuredImageSourceUrl;
    }

    public void setFeaturedImageSourceUrl(String featuredImageSourceUrl) {
        this.featuredImageSourceUrl = featuredImageSourceUrl;
    }

    public ParentModel withFeaturedImageSourceUrl(String featuredImageSourceUrl) {
        this.featuredImageSourceUrl = featuredImageSourceUrl;
        return this;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public ParentModel withLinks(Links links) {
        this.links = links;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
