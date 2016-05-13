package cn.edu.zju.academic_search.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import cn.edu.zju.academic_search.service.AcademicSearch_Service;

public class AcademicSearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private AcademicSearch_Service as_service; 
	
	public AcademicSearchServlet() {
		super();
		as_service = new AcademicSearch_Service();
		
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Long id1 = Long.valueOf(request.getParameter("id1"));
		Long id2 = Long.valueOf(request.getParameter("id2"));
		List<List<Long>> res = as_service.search(id1, id2);
		PrintWriter out = response.getWriter();
		out.write(JSON.toJSONString(res));
		out.flush();
	}
}
