package cn.edu.zju.academic_search.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Entity {
	private Long Id; 
	private Set<Long> RId;// = new HashSet<Long>(); //为防止Null异常，这里都生成了对象
    private Set<Map<String,Long>> AA;// = new HashSet<Map<String,Long>>();
    private Set<Map<String,Long>> F;// = new HashSet<Map<String,Long>>();
    private Map<String,Long> J;// =  new HashMap<String,Long>();
    private Map<String,Long> C;// = new HashMap<String,Long>();
    
	public Long getId() {
		return Id;
	}
	public void setId(Long id) {
		Id = id;
	}
	public Set<Long> getRId() {
		return RId;
	}
	public void setRId(Set<Long> rId) {
		RId = rId;
	}
	public Set<Map<String,Long>> getAA() {
		return AA;
	}
	public void setAA(Set<Map<String,Long>> aA) {
		AA = aA;
	}
	public Set<Map<String,Long>> getF() {
		return F;
	}
	public void setF(Set<Map<String,Long>> f) {
		F = f;
	}
	public Map<String,Long> getJ() {
		return J;
	}
	public void setJ(Map<String,Long> j) {
		J = j;
	}
	public Map<String,Long> getC() {
		return C;
	}
	public void setC(Map<String,Long> c) {
		C = c;
	}
}
