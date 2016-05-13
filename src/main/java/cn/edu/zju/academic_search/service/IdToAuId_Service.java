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


public class IdToAuId_Service {
	private Long Id1,AuId2;
    private List<List<Long>> routes;
    
    private List<Entity> entities1,entities2;
    public static Entity[] list_RFId;
    
    public IdToAuId_Service(Long id1, Long id2, List<Entity> list_Entity1, List<Entity> list_Entity2,Entity[] entities_RFId1) {
    	this.Id1=id1;
    	this.AuId2=id2;
    	entities1 = list_Entity1;
    	entities2 = list_Entity2;
    	list_RFId = entities_RFId1;
    	
    	routes=new ArrayList<List<Long>>();
    }
    
    public List<List<Long>> getRoutes() {
    	
    	List<List<Long>> oneHop=getOneHop();
    	List<List<Long>> twoHop=getTwoHop(entities1.get(0),entities2);
    	List<List<Long>> threeHop=getThreeHop();
    	if(!oneHop.isEmpty()) {
    		routes.addAll(oneHop);
    	}
    	if(!twoHop.isEmpty()) {
    		routes.addAll(twoHop);
    	}
    	if(!threeHop.isEmpty()) {
    		routes.addAll(threeHop);
    	}
    	return routes;
    }
	
    public List<List<Long>> getOneHop() {
    	List<List<Long>> oneHop=new ArrayList<List<Long>>();
    	//Id->AuId
    	Entity et1=entities1.get(0);
    	Set<Map<String,Long>> list_Author=et1.getAA();
    	for(Map<String,Long> author:list_Author) {
    		Long auid= author.get("AuId");
    		if(auid!=null&&auid.equals(AuId2)) {
    			List<Long> path = new ArrayList<>();
    			path.add(Id1);
    			path.add(AuId2);
    			oneHop.add(path);
    			break;
    		}
    	}
    	return oneHop;
    }
    
    public List<List<Long>> getTwoHop(Entity etx,List<Entity> listEntity) {
    	List<List<Long>> twoHop=new ArrayList<List<Long>>();
    	
    	//ID->ID->AuID
    	
    	Set<Long> RId=etx.getRId();
    	if(RId==null) return twoHop;
    	
    	for(Entity ety:listEntity) {
    		if(RId.contains(ety.getId())) {
    			List<Long> path=new ArrayList<Long>();
    			path.add(etx.getId());
    			path.add(ety.getId());
    			path.add(AuId2);
    			twoHop.add(path);
    		}
    	}
    	return twoHop;
    }
    
    
    public List<List<Long>> getThreeHop() {
    	List<List<Long>> threeHop=new ArrayList<List<Long>>();
    	//Id->Id->Id->AuId
    	Entity et1=entities1.get(0);
    	Set<Long> RId= et1.getRId();
    	if(RId!=null) {
    		for(Entity etx:list_RFId) {    //list_RFId已经计算过
    			List<List<Long>> twoHop=getTwoHop(etx,entities2); 
    			if(!twoHop.isEmpty()) {
    				for(List<Long> nex:twoHop) {
    					List<Long> path=new ArrayList<Long>();
    				    path.add(Id1);
    				    path.addAll(nex);
    				    threeHop.add(path);
    				}
    			}
    		}
    	}
    	
    	Set<Map<String,Long>> Author1=et1.getAA();
    	Set<Map<String,Long>> F1=et1.getF();
    	Map<String,Long> J1=et1.getJ();
    	Map<String,Long> C1=et1.getC();
    	
        Set<Long> list_AuId1 = new HashSet<Long>();  //Id1论文的所有作者集合
        Set<Long> list_AfId2 = new HashSet<Long>();  //AuId2的所有所属机构集合
        
        if(Author1!=null) {
        	for(Map<String,Long> author: Author1) {  //获得Id1论文的所有作者Id集合
        	    if(author.get("AuId")!=null) {
        		   list_AuId1.add(author.get("AuId"));
        	    }
            }
        }
        
    	//Id-CommonType->Id->AuId
    	for(Entity et2:entities2) { //遍历AuId2的每篇论文
    		Long idx=et2.getId();
    		if(idx.equals(AuId2)) continue;
    	    Set<Map<String,Long>> Author2=et2.getAA();
    	    Set<Long> list_AuIdx = new HashSet<Long>();//和AuId2作者合作过Idx论文的作者集合
    	    if(Author2!=null) {
    	    	for(Map<String,Long> author: Author2) {
    	    		Long auId=author.get("AuId");
    	    		Long afId=author.get("Afld");
        	    	if(auId!=null) {
        	    		list_AuIdx.add(auId);
        	    		if(afId!=null&&auId.equals(AuId2)) { //AuId2作者所属机构
            	    		list_AfId2.add(afId);
            	    	}
        	    	}	
        	    }
    	    }
    	  
    	    List<Long> path;
    	    
    	    //Id->AuId->Id->AuId
    	    list_AuIdx.retainAll(list_AuId1);
    	    if(!list_AuIdx.isEmpty()) {
    	    	for(Long pre_Idx:list_AuIdx) {
    	    		path=new ArrayList<Long>();
    	    		path.add(Id1);
    	    		path.add(pre_Idx);
    	    		path.add(idx);
    	    		path.add(AuId2);
    	    		threeHop.add(path);
    	    	}
    	    }
    	    
    	    Set<Map<String,Long>> F2=et2.getF();
    	    Map<String,Long> J2=et2.getJ();
    	    Map<String,Long> C2=et2.getC();
    	    
    	    if(F1!=null&&F2!=null) {
    	    	F2.retainAll(F1);
    	    	if(!F2.isEmpty()) {
    	    		for(Map<String,Long> field: F2) {
    	    			path=new ArrayList<Long>();
    	    			path.add(Id1);
    	    			path.add(field.get("FId"));
    	    			path.add(idx);
    	    			path.add(AuId2);
    	    			threeHop.add(path);
    	    		}
    	    	}
    	    }
    	    if(J1!=null&&J2!=null) {
    	    	if(J1.get("JId").equals(J2.get("JId"))) {
    	    		path=new ArrayList<Long>();
    	    		path.add(Id1);
    	    		path.add(J1.get("JId"));
    	    		path.add(idx);
    	    		path.add(AuId2);
    	    		threeHop.add(path);
    	    	}
    	    }
    	    if(C1!=null&&C2!=null) {
    	    	if(C1.get("CId").equals(C2.get("CId"))) {
    	    		path=new ArrayList<Long>();
    	    		path.add(Id1);
    	    		path.add(C1.get("CId"));
    	    		path.add(idx);
    	    		path.add(AuId2);
    	    		threeHop.add(path);
    	    	}
    	    }
    	}
    	
    	//Id->AuId->AFId->AuId
    	
    	//list_AuId1   Id1论文的所有作者集合
        //list_AfId2   AuId2的所有所属机构集合
    	
    	if(list_AuId1.isEmpty()||list_AfId2.isEmpty()) return threeHop;
    	ExecutorService exec = Executors.newCachedThreadPool();
    	for(Long auid:list_AuId1) {
    		getAfIdByAuId getAfIdRun = new getAfIdByAuId(auid,list_AfId2,threeHop);
    		exec.submit(getAfIdRun);
    	}
    	exec.shutdown();         //等待线程执行完
    	while(true) {
    		if(exec.isTerminated()) {
    			break;
    		}
    	}
    	
    	return threeHop;
    }
    
    class getAfIdByAuId implements Runnable {
    	private Long Query_auid;
    	private Set<Long> list_AfId2;
    	private List<List<Long>> threeHop;

    	public getAfIdByAuId(Long AuId,Set<Long> list_afid2,List<List<Long>> ThreeHop) {
    		Query_auid = AuId;
    		list_AfId2 = list_afid2;
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
			AfIds.retainAll(list_AfId2);
			List<Long> path;
			for(Long afid:AfIds) {
				path = new ArrayList<Long>();
				path.add(Id1);
				path.add(Query_auid);
				path.add(afid);
				path.add(AuId2);
				synchronized(threeHop) {
					threeHop.add(path);
				}
			}	
		}
    	
    }
}
