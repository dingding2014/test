package cn.edu.zju.academic_search.service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.alibaba.fastjson.JSON;

import cn.edu.zju.academic_search.http.HttpUtil;
import cn.edu.zju.academic_search.model.Entities;
import cn.edu.zju.academic_search.model.Entity;

public class AcademicSearch_Service {

	public static HttpUtil httpUtil;
	public static ExecutorService exec; 
	public static List<Entity> entities1,entities2,Refed_entities2 ;
	public static Entity[] entities_RFId1;
	public static Long Id1,Id2;
	public static int type1,type2;
	public static volatile boolean flag1,flag2;
	
	public AcademicSearch_Service() {
		httpUtil = new HttpUtil();
	}
	
	public List<List<Long>> search(Long id1, Long id2) {
		type1=1;
		type2=1;
		Id1 = id1;
		Id2= id2;
		
		flag1=flag2=false;
		exec = Executors.newCachedThreadPool();
		getEntities1Run getRunId1 = new getEntities1Run("Or(Id="+id1+",Composite(AA.AuId="+id1+"))");
		getEntities2Run getRunId2 = new getEntities2Run("Or(Id="+id2+",Composite(AA.AuId="+id2+"))");
		exec.submit(getRunId1);
		exec.submit(getRunId2);
		while(true) {
			if(flag1&&flag2) break;
		}
		exec.shutdown();
        while(true) {
        	if(exec.isTerminated()) { //等待所有线程执行完毕
        		break;
        	} 
        }       
       
		if(type1==0&&type2==0) {
			IdToId_Service idToid = new IdToId_Service(id1,id2,entities1,entities2,entities_RFId1,Refed_entities2);
			return idToid.getRoutes();
		}
		else if(type1==0&&type2==1) {
			IdToAuId_Service idToAuid = new IdToAuId_Service(id1,id2,entities1,entities2,entities_RFId1);
			return idToAuid.getRoutes();
		}
		else if(type1==1&&type2==0) {
			AuIdToId_Service AuidToid = new AuIdToId_Service(id1,id2,entities1,entities2,Refed_entities2);
			return AuidToid.getRoutes();
		}
		else {
			AuIdToAuId_Service AuidToAuid = new AuIdToAuId_Service(id1,id2,entities1,entities2);
			return AuidToAuid.getRoutes();
		}
	}
}

class getEntities1Run implements Runnable {
    private String query_str;
    
    public getEntities1Run(String Query_str) {
    	query_str=Query_str;
    }
	
	@Override
	public void run() {	
		String json = AcademicSearch_Service.httpUtil.getMethod(query_str,"Id,RId,F.FId,AA.AuId,AA.AfId,C.CId,J.JId"); //这里认为需要AA.AfId,因为ID1可能是作者
		AcademicSearch_Service.entities1=(JSON.parseObject(json,Entities.class)).getEntities();
		if(AcademicSearch_Service.entities1.size()==1&&AcademicSearch_Service.entities1.get(0).getId().equals(AcademicSearch_Service.Id1)) {
			AcademicSearch_Service.type1 = 0;   //Id1是论文的情况
			Entity et1 = AcademicSearch_Service.entities1.get(0);
			Set<Long> RId1 = et1.getRId();
			if(RId1!=null) {
				AcademicSearch_Service.entities_RFId1 = new Entity[RId1.size()];
				int pos=0;
				for(Long idx:RId1) {
					getEntitiesOfRFId1Run getRun = new getEntitiesOfRFId1Run(pos++,"Id="+idx);
					AcademicSearch_Service.exec.submit(getRun);
				}
			}
        }
		else AcademicSearch_Service.type1 = 1;	
		AcademicSearch_Service.flag1 = true;
	}
}

class getEntitiesOfRFId1Run implements Runnable {  //对于Id1为论文的情况，求其RID集合所有实体
    private int pos;                        
    private String query_str;
    
    public getEntitiesOfRFId1Run(int Pos, String Query_str) {
    	pos=Pos;
    	query_str=Query_str;
    }
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		String json = AcademicSearch_Service.httpUtil.getMethod(query_str,"Id,RId,F.FId,AA.AuId,AA.AfId,C.CId,J.JId");//这里AA.AfId可能不需要
		Entity et = (JSON.parseObject(json,Entities.class)).getEntities().get(0);
		AcademicSearch_Service.entities_RFId1[pos]=et;
	}	
}

class getEntities2Run implements Runnable {
    private String query_str;
    
    public getEntities2Run(String Query_str) {
    	query_str=Query_str;
    }
	
	@Override
	public void run() {	
		String json = AcademicSearch_Service.httpUtil.getMethod(query_str,"Id,RId,F.FId,AA.AuId,AA.AfId,C.CId,J.JId");
		AcademicSearch_Service.entities2=(JSON.parseObject(json,Entities.class)).getEntities();
		if(AcademicSearch_Service.entities2.size()==1&&AcademicSearch_Service.entities2.get(0).getId().equals(AcademicSearch_Service.Id2)) {
			AcademicSearch_Service.type2 = 0;   //Id2是论文的情况
			getRefed_entities2Run getRun = new getRefed_entities2Run("RId="+AcademicSearch_Service.Id2);
			AcademicSearch_Service.exec.submit(getRun);
        }
		else AcademicSearch_Service.type2 = 1;
		AcademicSearch_Service.flag2 = true;
	}
}

class getRefed_entities2Run implements Runnable {
    private String query_str;
    
    public getRefed_entities2Run(String Query_str) {
    	query_str=Query_str;
    }
	
	@Override
	public void run() {	
		String json = AcademicSearch_Service.httpUtil.getMethod(query_str,"Id,RId,F.FId,AA.AuId,C.CId,J.JId");
		AcademicSearch_Service.Refed_entities2=(JSON.parseObject(json,Entities.class)).getEntities();
	}
}