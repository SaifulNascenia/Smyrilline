package com.mcp.smyrilline.model.parentmodel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by saiful on 5/24/17.
 */

public class ParentModelList implements Serializable {

    private List<ParentModel> parentModelList;

    public List<ParentModel> getParentModelList() {
        return parentModelList;
    }

    public void setParentModelList(List<ParentModel> parentModelList) {
        this.parentModelList = parentModelList;
    }
}
