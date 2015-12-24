package org.mycat.web.service;

import java.util.List;

import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.BaseService;
import org.mycat.web.model.JitModel;
import org.mycat.web.util.Constant;
import org.mycat.web.util.ZookeeperCuratorHandler;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service("jitService")
public class JitService extends BaseService {

	
	private ZookeeperCuratorHandler zkHander=  ZookeeperCuratorHandler.getInstance();

	public RainbowContext query(RainbowContext context) throws Exception {
		System.out.println("JitService.query()");
		JitModel root = new JitModel("root","Server","root");
		//List<String> childs = ZookeeperService.getInstance().getChilds("/");
		List<String> childs = zkHander.getChildrenName("/");
		for (String n : childs) {
			if(!n.equals(Constant.MYCAT_CLUSTER_KEY))
				continue;
			JitModel s=new JitModel(n,n,Constant.LOCAL_ZK_URL_NAME);
			//List<String> childs2 = ZookeeperService.getInstance().getChilds("/"+n);
			List<String> childs2 = zkHander.getChildrenName("/"+n);
			for (String n2 : childs2) {
				JitModel s2=new JitModel(n2,n2,Constant.LOCAL_ZK_URL_NAME);
				//List<String> childs3 = ZookeeperService.getInstance().getChilds("/"+n+"/"+n2);
				List<String> childs3 = zkHander.getChildrenName("/"+n+"/"+n2);
				for (String n3 : childs3) {
					JitModel s3=new JitModel(n3,n3,Constant.LOCAL_ZK_URL_NAME);
					//List<String> childs4 = ZookeeperService.getInstance().getChilds("/"+n+"/"+n2+"/"+n3);
					List<String> childs4 = zkHander.getChildrenName("/"+n+"/"+n2+"/"+n3);
					for (String n4 : childs4) {
						//List<String> childs5 = ZookeeperService.getInstance().getChilds("/"+n+"/"+n2+"/"+n3+"/"+n4);
						JitModel s4=new JitModel(n4,n4,Constant.LOCAL_ZK_URL_NAME);
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
