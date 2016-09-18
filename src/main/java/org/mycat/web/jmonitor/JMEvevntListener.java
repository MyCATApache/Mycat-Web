package org.mycat.web.jmonitor;

import com.alibaba.fastjson.JSONObject;

public interface JMEvevntListener {

    void handle(JSONObject event);

}
