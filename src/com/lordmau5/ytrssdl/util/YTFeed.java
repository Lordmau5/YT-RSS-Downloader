package com.lordmau5.ytrssdl.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lordmau5 on 15.08.2014.
 */
public class YTFeed {

    final List<YTFeedMsg> entries = new ArrayList<YTFeedMsg>();

    public List<YTFeedMsg> getMessages() {
        return entries;
    }

}
