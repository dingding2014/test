package cn.edu.zju.academic_search.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.edu.zju.academic_search.model.Entity;


public class IdToId_Service {
    public Long Id1,Id2;
    private List<List<Long>> routes;  
    
    public List<List<Long>> routes1,routes2;
    public List<Entity> entities1,entities2,Refed_entities2;
    public Entity[] list_RFId;
    
    public IdToId_Service(Long id1, Long id2, List<Entity> list_Entity1, List<Entity> list_Entity2,Entity[] entities_RFId1, List<Entity> Refed_Entities2) {
    	this.Id1=id1;
    	this.Id2=id2;
    	entities1=list_Entity1;
    	entities2=list_Entity2;
    	list_RFId = entities_RFId1;
    	Refed_entities2 = Refed_Entities2;
    	
    	routes=new ArrayList<List<Long>>();
    }
        
    public List<List<Long>> getRoutes() {
    	List<List<Long>> oneHop = getOneHop();
    	List<List<Long>> twoHop = getTwoHop(Id1,Id2);
    	List<List<Long>> threeHop = getThreeHop();
    	routes.addAll(oneHop);
    	routes.addAll(twoHop);
    	routes.addAll(threeHop);
		return routes;
    	
    }    
	
    public List<List<Long>> getOneHop() {
    	List<List<Long>> oneHop=new ArrayList<List<Long>>();
    	
    	//Id->Id
    	Entity et1 = entities1.get(0);
    	Set<Long> RId=et1.getRId();
    	List<Long> path;
    	if(RId!=null&&RId.contains(Id2)) {
    		path=new ArrayList<Long>();
    		path.add(Id1);
    		path.add(Id2);
    		oneHop.add(path);
    	}
    	return oneHop;
    }
    
    public List<List<Long>> getTwoHop(Long idx,Long idy) {
    	List<List<Long>> twoHop=new ArrayList<List<Long>>();
    	
    	Entity et1=entities1.get(0);
    	List<Entity> listEntity = Refed_entities2;
    	Set<Long> RId=et1.getRId();
    	//Id->Id->Id
    	if(RId!=null) {
    		if(listEntity!=null) {
    			for(Entity et2:listEntity) {
        		    if(RId.contains(et2.getId())) {       		    	
        			    List<Long> path=new ArrayList<Long>();
        			    path.add(idx);
        			    path.add(et2.getId());
        			    path.add(idy);
        			    twoHop.add(path);
        		    }
        	    }
    		}
    	}
    	
    	Entity et2=entities2.get(0);
    	
    	//id1,id2属于同一个研究领域、作者、会议、杂志(journal)
    	
    	//同一个研究领域
    	Set<Map<String,Long>> Fid1 = et1.getF();
    	Set<Map<String,Long>> tmp_Fid1 = new HashSet<Map<String,Long>>();
    	Set<Map<String,Long>> Fid2=et2.getF();
    	if(Fid1!=null&&Fid2!=null) {
    		tmp_Fid1.addAll(Fid1);
    	    tmp_Fid1.retainAll(Fid2); 
    	    if(!tmp_Fid1.isEmpty()) {
        		for(Map<String,Long> map:tmp_Fid1) {
        			List<Long> path=new ArrayList<Long>();
        			path.add(idx);
        			path.add(map.get("FId"));
        			path.add(idy);
        			twoHop.add(path);
        		}
        	}
    	}
    	
    	//同一个作者
    	Set<Map<String,Long>> AuId1 = et1.getAA();
    	Set<Map<String,Long>> tmp_AuId1 = new HashSet<Map<String,Long>>();
    	Set<Map<String,Long>> AuId2=et2.getAA();
    	if(AuId1!=null&&AuId2!=null) {
    		tmp_AuId1.addAll(AuId1);
    		tmp_AuId1.retainAll(AuId2);
    	    if(!tmp_AuId1.isEmpty()) {
    		   for(Map<String,Long> map:tmp_AuId1) {
    			   List<Long> path=new ArrayList<Long>();
    			   path.add(idx);
    			   path.add(map.get("AuId"));
    			   path.add(idy);
    			   twoHop.add(path);
    		   }
    	    }
    	}
    	
    	
    	//同一个会议
    	Map<String,Long> CId1=et1.getC();
    	Map<String,Long> CId2=et2.getC();
    	if(CId1!=null&&CId2!=null) {
    	    if(!CId2.isEmpty()&&CId1.get("CId").equals(CId2.get("CId"))) {
    		    List<Long> path=new ArrayList<Long>();
			    path.add(idx);
			    path.add(CId1.get("CId"));
			    path.add(idy);
			    twoHop.add(path);
    	    }
    	}
    	
    	//同一个 journal
    	Map<String,Long> JId1=et1.getJ();
    	Map<String,Long> JId2=et2.getJ();
    	if(JId1!=null&&JId2!=null) {
    	    if(!JId2.isEmpty()&&JId1.get("JId").equals(JId2.get("JId"))) { 	
    		    List<Long> path=new ArrayList<Long>();
			    path.add(idx);
			    path.add(JId1.get("JId"));
			    path.add(idy);
			    twoHop.add(path);
    	   }
    	}	
    	return twoHop;
    }
          
    public void getCommonType(List<List<Long>> threeHop) {
        for(Entity et1:list_RFId) {
        	Long idx=et1.getId();
        	Set<Long> RId=et1.getRId();
        	if(RId!=null) {
        		List<Entity> listEntity = Refed_entities2;
        		if(listEntity!=null) {
        			for(Entity et2:listEntity) {
            		    if(RId.contains(et2.getId())) {       		    	
            			    List<Long> path=new ArrayList<Long>();
            			    path.add(Id1);
            			    path.add(idx);
            			    path.add(et2.getId());
            			    path.add(Id2);
            			    threeHop.add(path);
            		    }
            	    }
        		}
        	}
        	
        	Entity et2 = entities2.get(0);
     
        	//id1,id2属于同一个研究领域、作者、会议、杂志(journal)
        	
        	//同一个研究领域
        	Set<Map<String,Long>> Fid1 = et1.getF();
        	Set<Map<String,Long>> tmp_Fid1 = new HashSet<Map<String,Long>>();
        	Set<Map<String,Long>> Fid2=et2.getF();
        	if(Fid1!=null&&Fid2!=null) {
        		tmp_Fid1.addAll(Fid1);
        	    tmp_Fid1.retainAll(Fid2); 
        	    if(!tmp_Fid1.isEmpty()) {
            		for(Map<String,Long> map:tmp_Fid1) {
            			List<Long> path=new ArrayList<Long>();
            			path.add(Id1);
            			path.add(idx);
            			path.add(map.get("FId"));
            			path.add(Id2);
            			threeHop.add(path);
            		}
            	}
        	}
        	
        	//同一个作者
        	Set<Map<String,Long>> AuId1 = et1.getAA();
        	Set<Map<String,Long>> tmp_AuId1 = new HashSet<Map<String,Long>>();
        	Set<Map<String,Long>> AuId2=et2.getAA();
        	if(AuId1!=null&&AuId2!=null) {
        		tmp_AuId1.addAll(AuId1);
        		tmp_AuId1.retainAll(AuId2);
        	    if(!tmp_AuId1.isEmpty()) {
        		   for(Map<String,Long> map:tmp_AuId1) {
        			   List<Long> path=new ArrayList<Long>();
        			   path.add(Id1);
           			path.add(idx);
           			path.add(map.get("AuId"));
           			path.add(Id2);
           			threeHop.add(path);
        		   }
        	    }
        	}
        	
        	
        	//同一个会议
        	Map<String,Long> CId1=et1.getC();
        	Map<String,Long> CId2=et2.getC();
        	if(CId1!=null&&CId2!=null) {
        	    if(!CId2.isEmpty()&&CId1.get("CId").equals(CId2.get("CId"))) {
        		    List<Long> path=new ArrayList<Long>();
        		    path.add(Id1);
        			path.add(idx);
        			path.add(CId1.get("CId"));
        			path.add(Id2);
        			threeHop.add(path);
        	    }
        	}
        	
        	//同一个 journal
        	Map<String,Long> JId1=et1.getJ();
        	Map<String,Long> JId2=et2.getJ();
        	if(JId1!=null&&JId2!=null) {
        	    if(!JId2.isEmpty()&&JId1.get("JId").equals(JId2.get("JId"))) { 	
        	    	List<Long> path=new ArrayList<Long>();
        		    path.add(Id1);
        			path.add(idx);
        			path.add(JId1.get("JId"));
        			path.add(Id2);
        			threeHop.add(path);
        	   }
        	}	
        }
    }
    
    public List<List<Long>> getThreeHop() {
    	List<List<Long>> threeHop=new ArrayList<List<Long>>();
    	Entity et1 = entities1.get(0);
    	Set<Long> RId=et1.getRId();
    	List<Long> path;
    	//Id->Id->Id->Id、Id->Id->CommonType->Id
    	
    	//多线程发请求
    	if(RId!=null) {
    		//list_RFId集合已经计算好
            getCommonType(threeHop);
    	}
    	
    	List<Entity> listEntity = Refed_entities2;
    	
    	//Id->CommomType->Id->Id
    	
    	if(listEntity!=null) {
    		Set<Map<String,Long>> Fid1=et1.getF();
    		
    		Set<Map<String,Long>> AuId1=et1.getAA();
    		Map<String,Long> CId1=et1.getC();
    		Map<String,Long> JId1=et1.getJ();
    		for(Entity et:listEntity) {
    			Set<Map<String,Long>> Fidx=et.getF();
        		Set<Map<String,Long>> AuIdx=et.getAA();
        		Map<String,Long> CIdx=et.getC();
        		Map<String,Long> JIdx=et.getJ();
        		if(Fid1!=null&&Fidx!=null) {
        			Set<Map<String,Long>> tmp_Fidx = new HashSet<Map<String,Long>>();
        			tmp_Fidx.addAll(Fidx);
        			tmp_Fidx.retainAll(Fid1);
        			for(Map<String,Long> FId: tmp_Fidx) {
        				if(FId.get("FId")!=null) {
        					path=new ArrayList<Long>();
        					path.add(Id1);
        					path.add(FId.get("FId"));
        					path.add(et.getId());
        					path.add(Id2);
        					threeHop.add(path);
        				}
        			}
        		}
        		if(AuId1!=null&&AuIdx!=null) {
        			Set<Map<String,Long>> tmp_AuIdx = new HashSet<Map<String,Long>>();
        			tmp_AuIdx.addAll(AuIdx);
            		tmp_AuIdx.retainAll(AuId1);
        			for(Map<String,Long> AuId: tmp_AuIdx) {
        				if(AuId.get("AuId")!=null) {
        					path=new ArrayList<Long>();
        					path.add(Id1);
        					path.add(AuId.get("AuId"));
        					path.add(et.getId());
        					path.add(Id2);
        					threeHop.add(path);
        				}
        			}
        		}
        		if(CId1!=null&&CIdx!=null) {
        			if(CId1.get("CId")!=null&&CIdx.get("CId").equals(CId1.get("CId"))) {
        				path=new ArrayList<Long>();
    					path.add(Id1);
    					path.add(CId1.get("CId"));
    					path.add(et.getId());
    					path.add(Id2);
    					threeHop.add(path);	
        			}
        		}
        		if(JId1!=null&&JIdx!=null) {
        			if(JId1.get("JId")!=null&&JIdx.get("JId").equals(JId1.get("JId"))) {
        				path=new ArrayList<Long>();
    					path.add(Id1);
    					path.add(JId1.get("JId"));
    					path.add(et.getId());
    					path.add(Id2);
    					threeHop.add(path);	
        			}
        		}
    		}
    	}
    	return threeHop;
    }   
    
}
