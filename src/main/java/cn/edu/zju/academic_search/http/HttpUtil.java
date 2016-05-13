package cn.edu.zju.academic_search.http;

import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HttpUtil {

	public String getMethod(String queryExpr,String queryAttr) {
		HttpClient httpclient = HttpClients.createDefault();
		String json=null;
		try {
			URIBuilder builder = new URIBuilder("https://oxfordhk.azure-api.net/academic/v1.0/evaluate");
            builder.setParameter("expr", queryExpr);
            builder.setParameter("model", "latest");
            builder.setParameter("count", "100000");   //这个属性必须有，因为不填的话默认为10
            builder.setParameter("attributes", queryAttr);
            builder.setParameter("subscription-key", "f7cc29509a8443c5b3a5e56b0e38b5a6");
            URI uri = builder.build();
            HttpGet request = new HttpGet(uri);
            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) 
            {
            	json=EntityUtils.toString(entity);
                System.out.println(json);
            }
		}
		catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
		return json;
	}

}
