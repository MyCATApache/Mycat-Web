package org.mycat.web.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.BaseService;
import org.mycat.web.model.JitModel;
import org.mycat.web.model.MySqlRep;
import org.mycat.web.model.MySqlServer;
import org.mycat.web.model.ZtreeModel;
import org.mycat.web.util.ZookeeperCuratorHandler;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;

@Lazy
@Service("jitService")
public class JitService extends BaseService {

	
	

	public RainbowContext query(RainbowContext context) throws Exception {
		System.out.println("JitService.query()");
		JitModel root = new JitModel("root","Server","root");
		List<String> childs = ZookeeperService.getInstance().getChilds("/");
		for (String n : childs) {
			if(!n.equals("mycat-cluster"))
				continue;
			JitModel s=new JitModel(n,n,"mycat");
			List<String> childs2 = ZookeeperService.getInstance().getChilds("/"+n);
			for (String n2 : childs2) {
				JitModel s2=new JitModel(n2,n2,"mycat");
				List<String> childs3 = ZookeeperService.getInstance().getChilds("/"+n+"/"+n2);
				for (String n3 : childs3) {
					JitModel s3=new JitModel(n3,n3,"mycat");
					List<String> childs4 = ZookeeperService.getInstance().getChilds("/"+n+"/"+n2+"/"+n3);
					for (String n4 : childs4) {
//						List<String> childs5 = ZookeeperService.getInstance().getChilds("/"+n+"/"+n2+"/"+n3+"/"+n4);
						JitModel s4=new JitModel(n4,n4,"mycat");
						s3.addChildren(s4);
					}
					s2.addChildren(s3);
				}
				s.addChildren(s2);
			}
			root.addChildren(s);
		}
		context.addAttr("jit", root);
		context.setSuccess(true);
		return context;
	}
}
