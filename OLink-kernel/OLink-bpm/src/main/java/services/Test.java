package services;

import java.util.ArrayList;

import eWAP.core.ResourcePool;
import eWAP.core.dbaccess.ConnectionFactory;

public class Test{

	ResourcePool  req;
	public void test()
	{   
		ConnectionFactory dbclass=new ResourcePool().getConnectionFactory(1);
		String sql="insert into mytest values(?,?,?)";
        Object [] o=new Object[3];
        o[0]="aaa";
        o[1]="bbb";
        o[2]=120;
		if(dbclass.executeUpdate(sql,o))
		{
			req.setTransFlag(0);
			req.setResultObj("成功");
		}
		else
		{
			req.setTransFlag(-1);
			req.setResultObj("失败");
		}
		return;
	}
}
