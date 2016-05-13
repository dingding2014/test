package cn.edu.zju.academic_search.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.alibaba.fastjson.JSON;

import cn.edu.zju.academic_search.http.HttpUtil;
import cn.edu.zju.academic_search.model.Entities;
import cn.edu.zju.academic_search.model.Entity;



public class AuIdToId_Service {
	private Long AuId1,Id2;
    private List<List<Long>> routes;
    
    public static List<Entity> entities1,entities2,Refed_entities2;
    
    public AuIdToId_Service(Long id1, Long id2, List<Entity> list_Entity1, List<Entity> list_Entity2, List<Entity> Refed_Entities2) {
    	this.AuId1=id1;
    	this.Id2=id2;
    	entities1=list_Entity1;
    	entities2=list_Entity2;
    	Refed_entities2 = Refed_Entities2;
    	
    	routes=new ArrayList<List<Long>>();
    }
    
    public List<List<Long>> getRoutes() {
    	
    	if(entities1==null||entities2==null) return routes;
    	
    	getOneTwoThreeHop();
    	
    	return routes;
    }
	
    public void getOneTwoThreeHop() {
    	List<List<Long>> oneHop=new ArrayList<List<Long>>();
    	List<List<Long>> twoHop=new ArrayList<List<Long>>();
    	List<List<Long>> threeHop=new ArrayList<List<Long>>();
 
    	Entity et2=entities2.get(0);
    	List<Entity> Refed2_entities=Refed_entities2;
    	Set<Long> Refed2 = new HashSet<Long>();
    	if(Refed2_entities!=null) {            //如果Id2论文没有被引用过呢？？？
    		for(Entity refed: Refed2_entities) {   
    		    Refed2.add(refed.getId());
    	    }
    	}
    	
    	Set<Map<String,Long>> Author2=et2.getAA();
    	
    	Set<Long> list_AuId2 = new HashSet<Long>(); //写过Id2论文的所有作者集合
    	
    	if(Author2!=null) {
    		for(Map<String,Long> author:Author2) {
    			Long auid=author.get("AuId");
    			if(auid!=null) {
    				list_AuId2.add(auid);
    			}
    		}
    	}
    	
    	Set<Map<String,Long>> F2=et2.getF();
    	Map<String,Long> J2=et2.getJ();
    	Map<String,Long> C2=et2.getC();
    	
    	Set<Long> list_AfId1 = new HashSet<Long>();  //AuId1作者的所有机构集合
    	
    	for(Entity et1:entities1) {
			Long idx=et1.getId();
			if(idx.equals(AuId1)) continue; //有必要的检查
			List<Long> path;
    		//AuId->Id
    		if(idx.equals(Id2)) {
    			path=new ArrayList<Long>();
    			path.add(AuId1);
    			path.add(Id2);
    			oneHop.add(path);
    		}
    		Set<Long> RId1 = et1.getRId();
			Set<Map<String,Long>> Author1 = et1.getAA();
			Set<Map<String,Long>> F1=et1.getF();
	    	Map<String,Long> J1=et1.getJ();
	    	Map<String,Long> C1=et1.getC();
	    	
	    	Set<Long> list_AuIdx = new HashSet<Long>(); //写Idx论文的作者集合
	    	for(Map<String,Long> author:Author1) { //这里Author1不会为null，因为有AuId1
	    		Long auid = author.get("AuId");
	    		Long afid = author.get("AfId");
	    		if(auid!=null) {
	    			list_AuIdx.add(auid);
	    			if(auid.equals(AuId1)&&afid!=null) {
	    				list_AfId1.add(afid);      //找到AuId1作者所属的机构集合
	    			}
	    		}
	    	}
	    	
	    	//AuId->Id->AuId->Id
	    	list_AuIdx.retainAll(list_AuId2);
	    	for(Long nexIdx:list_AuIdx) {
	    		path = new ArrayList<Long>();
	    		path.add(AuId1);
	    		path.add(idx);
	    		path.add(nexIdx);
	    		path.add(Id2);
	    		threeHop.add(path);
	    	}
	    	
			if(RId1!=null) {
				//AuId->Id->Id
				if(RId1.contains(Id2)) {
					path=new ArrayList<Long>();
	    			path.add(AuId1);
	    			path.add(idx);
	    			path.add(Id2);
	    			twoHop.add(path);
				}
				//AuId->Id->Id->Id
				RId1.retainAll(Refed2);
				for(Long idy:RId1) {
					path=new ArrayList<Long>();
	    			path.add(AuId1);
	    			path.add(idx);
	    			path.add(idy);
	    			path.add(Id2);
	    			threeHop.add(path);
				}

				//AuId->Id->CommonType->Id
				
    	    	if(F1!=null&&F2!=null) {
    	    		F1.retainAll(F2);
    	    		if(!F1.isEmpty()) {
    	    			for(Map<String,Long> field: F1) {
    	    				path=new ArrayList<Long>();
    	    				path.add(AuId1);
    	    				path.add(idx);
    	    				path.add(field.get("FId"));
    	    				path.add(Id2);
    	    				threeHop.add(path);
    	    			}
    	    		}
    	    	}
    	    	if(J1!=null&&J2!=null) {
    	    		if(J1.get("JId").equals(J2.get("JId"))) {
    	    			path=new ArrayList<Long>();
    	    			path.add(AuId1);
    	    			path.add(idx);
    	    			path.add(J1.get("JId"));
    	    			path.add(Id2);
    	    			threeHop.add(path);
    	    		}
    	    	}
    	    	if(C1!=null&&C2!=null) {
    	    		if(C1.get("CId").equals(C2.get("CId"))) {
    	    			path=new ArrayList<Long>();
    	    			path.add(AuId1);
    	    			path.add(idx);
    	    			path.add(C1.get("CId"));
    	    			path.add(Id2);
    	    			threeHop.add(path);
    	    		}
    	    	}
			}
    			
    		
    	}
    	
    	//AuId->AfId->AuId->Id
    	
    	if(!list_AfId1.isEmpty()&&!list_AuId2.isEmpty()) { //多线程查询
    		ExecutorService exec = Executors.newCachedThreadPool();
        	for(Long auid:list_AuId2) {
        		getAfIdByAuId getAfIdRun = new getAfIdByAuId(auid,list_AfId1,threeHop);
        		exec.submit(getAfIdRun);
        	}
        	exec.shutdown();         //等待线程执行完
        	while(true) {
        		if(exec.isTerminated()) {
        			break;
        		}
        	}
    	}
    	
    	if(!oneHop.isEmpty()) {
    		routes.addAll(oneHop);	
    	}
    	if(!twoHop.isEmpty()) {
    		routes.addAll(twoHop);
    	}
    	if(!threeHop.isEmpty()) {
    		routes.addAll(threeHop);
    	}
    }
    
    class getAfIdByAuId implements Runnable {
    	private Long Query_auid;
    	private Set<Long> list_AfId1;
    	private List<List<Long>> threeHop;

    	public getAfIdByAuId(Long AuId,Set<Long> list_afid1,List<List<Long>> ThreeHop) {
    		Query_auid = AuId;
    		list_AfId1 = list_afid1;
    		threeHop = ThreeHop;
    	}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			HttpUtil httpUtil = new HttpUtil();
			String json = httpUtil.getMethod("Composite(AA.AuId="+Query_auid+")","Id,AA.AuId,AA.AfId");
			List<Entity> entities = JSON.parseObject(json,Entities.class).getEntities();
			Set<Long> AfIds = new HashSet<Long>();
			for(Entity et:entities) {
				Set<Map<String,Long>> Authors =et.getAA();	
				for(Map<String,Long> author: Authors) {
					Long a_auid = author.get("AuId");
					Long a_afid = author.get("AfId");
					if(a_auid!=null&&a_auid.equals(Query_auid)) {
						if(a_afid!=null) {
							AfIds.add(a_afid);
						}	
						break;
					}
				}
			}
			AfIds.retainAll(list_AfId1);
			List<Long> path;
			for(Long afid:AfIds) {
				path = new ArrayList<Long>();
				path.add(AuId1);
				path.add(afid);
				path.add(Query_auid);
				path.add(Id2);
				synchronized(threeHop) {
					threeHop.add(path);
				}
			}	
		}
    	
    }
}