package cn.edu.zju.academic_search.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.edu.zju.academic_search.model.Entity;

public class AuIdToAuId_Service {
	private Long AuId1,AuId2;
    private List<List<Long>> routes;
    public static List<Entity> entities1,entities2;
    
    public AuIdToAuId_Service(Long id1, Long id2, List<Entity> list_Entity1, List<Entity> list_Entity2) {
    	this.AuId1=id1;
    	this.AuId2=id2;
    	entities1 = list_Entity1;
    	entities2 = list_Entity2;
    	
    	routes=new ArrayList<List<Long>>();
    }
    
    public List<List<Long>> getRoutes() {
    	getTwoThreeHop();
    	
    	return routes;
    }
    
    public void getTwoThreeHop() {
    	List<List<Long>> twoHop=new ArrayList<List<Long>>();
    	List<List<Long>> threeHop=new ArrayList<List<Long>>();
    	
    	Set<Long> entities_id2 = new HashSet<Long>();   //AuId2的所有文论Id集合
    	Set<Long> AFId2=new HashSet<Long>();            //AuId2的所有机构
    	for(Entity et2:entities2) {
    		if(et2.getId().equals(AuId2)) continue;  //AuId也可能会出现在ID,所以要检查
    		entities_id2.add(et2.getId());
    		Set<Map<String,Long>> AA2=et2.getAA();
    		//为了得到该AuId对应的AFId
    		for(Map<String,Long> map:AA2) {
    			Long auid = map.get("AuId");
    			Long afid = map.get("AfId");
    			if(auid!=null&&auid.equals(AuId2)) {
    				if(afid!=null) {
    					AFId2.add(afid);
    				}
    				break;
    			}
    		}
    	}
    	
    	Set<Long> entities_id1 = new HashSet<Long>();
    	Set<Long> AFId1=new HashSet<Long>();
    	for(Entity et1:entities1) {
    		if(et1.getId().equals(AuId1)) continue;  //AuId也可能会出现在ID,所以要检查
    		entities_id1.add(et1.getId());
    		//为了得到该AuId对应的AFId
    		Set<Map<String,Long>> AA1=et1.getAA();
    		Set<Long> RFId1=et1.getRId();
    		//AuId->Id->Id->AuId
    		if(RFId1!=null) {
    			RFId1.retainAll(entities_id2);
        		for(Long idx:RFId1) {
        			List<Long> path = new ArrayList<Long>();
        			path.add(AuId1);
        			path.add(et1.getId());
        			path.add(idx);
        			path.add(AuId2);
        			threeHop.add(path);
        		}
    		}
    		
    		for(Map<String,Long> map:AA1) {
    			Long auid = map.get("AuId");
    			Long afid = map.get("AfId");
    			if(auid!=null&&auid.equals(AuId1)) {
    				if(afid!=null) {
    					AFId1.add(afid);
    				}
    				break;
    			}
    		}
    	}
    	
    	//AuId->Id->AuId
    	entities_id1.retainAll(entities_id2);
    	for(Long idx : entities_id1) {
    		List<Long> path=new ArrayList<>();
    		path.add(AuId1);
    		path.add(idx);
    		path.add(AuId2);
    		twoHop.add(path);
    	}
    	
    	//AuId->AuFId->AuId
    	AFId1.retainAll(AFId2);
    	for(Long AuFIdx:AFId1) {
    		List<Long> path=new ArrayList<>();
    		path.add(AuId1);
    		path.add(AuFIdx);
    		path.add(AuId2);
    		twoHop.add(path);
    	}
    	if(!twoHop.isEmpty()) {
    		routes.addAll(twoHop);    	
    	}
    	if(!threeHop.isEmpty()) {
    		routes.addAll(threeHop);
    	}
    }
}
