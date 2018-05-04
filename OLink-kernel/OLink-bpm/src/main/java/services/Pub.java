/*
 * ��������: 2007-11-27
 * ��Ŀ���: eWAPƽ̨
 * ��Ŀ�ص�: �����������ϵͳ��˾
 * ����˵��: ��������
 */
package services;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

import org.apache.log4j.Logger;
import org.jdom.Element;

import eWAP.core.ResourcePool;
import eWAP.core.Tools;
import eWAP.core.dbaccess.ConnectionFactory;


public class Pub extends Base {
    
    private static final Logger logger = Logger.getLogger(Pub.class);   

    public Pub() {
        super();
    }

	/**
	 * Method��	public Element getElementByAttribute(Element element,String attributeName,String attributeValue)
	 * Function:
	 * 		��ݽڵ�����ֵ�����ڵ�
	 * �������
	 * 			element��Դ�ڵ�
	 * 			attributeName���������
	 * 			attributeValue������ֵ
	 * �������: ��������Ľڵ�
	 * 
	 */	
	@SuppressWarnings("rawtypes")
	static public Element getElementByAttribute(Element element,String attributeName,String attributeValue)
	{
		if(element==null)return null;
		if(attributeName==null)return null;
		if(attributeValue==null)return null;
		String tmpValue = element.getAttributeValue(attributeName);
		if(tmpValue!=null&&tmpValue.equals(attributeValue))
			return element;
		List childList = element.getChildren();
		for(int i=0;i<childList.size();i++)
		{
			Element child = (Element)childList.get(i);
			Element tmpElement = getElementByAttribute(child,attributeName,attributeValue);
			if(tmpElement!=null)return tmpElement;
		}
		return null;
	}

 	

	/**
	 * д�ļ�
	 * @param:fileName:�ļ���   
	 * @param:text:����
	 * 
	 * */
	public static void write(String fileName, String text) throws IOException {
		//logger.info("text="+text);     
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
		out.print(text);
		out.close();
	}
    /**
     * ȥ��ĩβ���ַ�
     * @author tys
     * @created 2010-3-17
     * @param str Ҫ������ַ�
     * @param endchar ��β���ַ�
     * @return �������ַ�
     */
    static public String removeTheEndChar(String str,String endchar){
    	int charLength=endchar.getBytes().length;
    	if(str.endsWith(endchar)){
    		str=str.substring(0,str.length()-charLength);
    	}
    	
    	return str;
    }
    
      
       /**
	     * ������λС��
	     * @author tys
	     * @created 2010-3-29
	     * @param str
	     * @return
	     */
	   static public String saveTwoBit(String str){
	    	 if("".equals(str)){
	    		 str="0";
	    	 }
	    	 DecimalFormat df = new DecimalFormat("#.##");
			 BigDecimal b1 = new BigDecimal(df.format(Double.parseDouble(str)));
			 return b1.toString();
	    }
    
    private static String GetWenStr(String iStr)
    {
     	int endflag=0;
    	int cnt=0,i=0;
    	for( i=0;i<iStr.length() && endflag==0;i++)
    	{
           	switch(iStr.charAt(i))
			{
           	    case '(':
           	      	cnt++;
           	    	break;
           	    case ')':
           	    	cnt--;
          	    	break;
           	    case '+':
            	case '-':
           	    case '*':
           	    case '/':
           	    	if(cnt==0) endflag=1;
           	    	break;
			}
   		
    	}
    	if(endflag==0) return iStr;
    	     else      return iStr.substring(0,i);
    }
    
    	
	public static String SendCommandToServer(String command)
	{	 
        String tmpStr=Pub.commServerByTcp(command);        
        if(tmpStr==null)
        {
	          return "���Ӻ�̨����ʧ��";	  		
        }
        tmpStr=tmpStr.substring(0,4);
        if(tmpStr.equals("0000"))
        {
			return "";        	
        }
        else
        {
    	     if(tmpStr.equals("0009"))
		      {
		          return "ϵͳȡ���У��Ժ�����";
		      }
    	      else
		      {
		          return "ִ��ʧ��";
		      }
    	}   
	}

	static public double round(double v,int scale)
   {

    	if(scale<0)  return v;

    	BigDecimal b = new BigDecimal(Double.toString(v));

    	BigDecimal one = new BigDecimal("1");

    	return b.divide(one,scale,BigDecimal.ROUND_HALF_UP).doubleValue();

    }
   
   static public int getNewPage(String CurrPage,String flag)
   {
	   int newpage=0;
	   if (flag.equalsIgnoreCase("0")) newpage=Integer.parseInt(CurrPage);
	   if (flag.equalsIgnoreCase("1")) newpage=Integer.parseInt(CurrPage)-1;
	   if (flag.equalsIgnoreCase("2")) newpage=Integer.parseInt(CurrPage)+1;
       if(newpage<1) newpage=1;
	   return newpage;
   }

 	static public String GetSysDate(ConnectionFactory dbclass){
	     
	     String  queryStr = "";
	     String dateStr="";
	     
	     try 
	     {
	     	 queryStr = "select to_char(sysdate,'yyyymmdd') bg_systime from dual";
	     
	     	dateStr = dbclass.doQueryString(queryStr,null);
	        dateStr=dateStr.replaceAll("-","");
	     }catch(Exception e) {
	         
	         e.printStackTrace();
	         return null;
	     }
	     
	     return dateStr;

	 }
			
	static public double GetCurRMBRate(ConnectionFactory dbclass,String month,int sele){
        
        String  queryStr = "";
        
        double rate = 0;//������Ԫ������һ���
        if(sele==0)
            queryStr = "select ExchRate  from T_RATE where CurrNo='001' and Month=?";
        else
            queryStr = "select ExchRate"+sele+" as ExchRate from T_RATE where CurrNo='001' and Month=?";
        
         try {       
        	rate = Double.parseDouble(dbclass.doQueryString(queryStr,new Object[] {month}));           
        }catch(Exception e) {
            
            e.printStackTrace();
    		 System.out.println("��Ԫ������һ���ʧ�ܣ�"+dbclass.getMsg());  
            rate = 0;
        }
        
        return rate;

     }
    
    /**
     * ��ȡ��Ӧ���ֶ���Ԫ����
     * 
     * @param dbclass
     * @param month
     * @param currNo
     * @param sele
     * @return
     */
    
	@SuppressWarnings("rawtypes")
	static public String GetCurMYRate(ConnectionFactory dbclass, String month,int sele) {

		String queryStr = "";
		ArrayList tmpv1 = null;
        
		String currRate = "";
		String ExchRate="";
		 
		if (sele == 1){
			ExchRate="ExchRate1";
		}else{
			ExchRate="ExchRate";
		}
		
		queryStr = "select b.currname currname,a."+ExchRate+" as ExchRate " +
				" from T_RATE a,T_CURRENCY b," +
				" (select distinct currno from T_FRDK_INFO where DataDate like '"+month+"%' and currno!='001' )c " +
				" where  a.Month= "+month+" " +
				" and a.currno=b.currno and a.currno=c.currno ";
		
		logger.debug("���ʲ�ѯ:" + queryStr);

		try {  
             
			tmpv1 = dbclass.doQuery(queryStr,0,0);

			if (tmpv1 == null) {
                
				System.out.println("���ʻ�ȡʧ�ܣ�" + queryStr);
				return currRate;
			}
			if (tmpv1.size() == 0) {

				System.out.println("���ʻ�ȡʧ�ܣ�" + queryStr);
				return currRate;
			} else {
				for(int i=0;i<tmpv1.size();i++){
					Hashtable tmph = (Hashtable) tmpv1.get(i);
					String strName = (String) tmph.get("currname".toUpperCase());
					String strValue = (String) tmph.get("ExchRate".toUpperCase());
					
					while(true){
						if(strValue.endsWith("0")){
							strValue=strValue.substring(0,strValue.length()-1);
						}else{
							break;
						}
					}
					
					currRate += strName.trim()+":"+strValue.trim()+"; ";
				}
                
			}
		} catch (Exception e) {

			e.printStackTrace();
			System.out.println("��Ԫ������һ���ʧ�ܣ�" + dbclass.getMsg());
			currRate = "";
		}
		 
		logger.debug("������Ϣ��"+currRate);
		
		return currRate;

	}
    
    
	static public String GetAccItemDate(ConnectionFactory dbclass,String accnote){ 
    	    
    	    String  queryStr = "";
   	    
    	    queryStr = "select ItemNo from T_ACCOUNT_NOTE where AccItemNo=?";
    	    
    	    try { 
    	    
    	        return dbclass.doQueryString(queryStr,new Object[] {accnote});
    	    }catch(Exception e) {
    	        
    	        e.printStackTrace();
    			 System.out.println("ȡ��Ŀ������������ʧ�ܣ�"+dbclass.getMsg());
    			 return null;
    	    }
    	 }
     
     
       
 	static public int GetAuthPasswd(ConnectionFactory dbclass,String passwd,String inst_no,String user_code){
        
        String  queryStr = "";
        String authpasswd="";
        String inputpasswd="";
        
        queryStr = "select User_Password  from T_MGT_OPERATOR_Y where Inst_No='"+inst_no+
    	           "' and User_Code=(select Auth_User from T_MGT_OPERATOR_Y where Inst_No='"+inst_no+"' and User_Code='"+user_code+"')";
        try {
        
        	authpasswd = dbclass.doQueryString(queryStr,null);
            
            
            if(authpasswd==null)
            {

    			System.out.println("ȡ��Ȩ����ʧ�ܣ�"+queryStr);
                return 1;
            }
            authpasswd =authpasswd.trim(); 
            inputpasswd  = Tools.cryptMd5Str(passwd);
            if(!inputpasswd.equals(authpasswd))
            {
    			System.out.println("ȡ��Ȩ����ʧ�ܣ���Ȩ���벻��");
            	return 3;
            }
        }catch(Exception e) {
            
            e.printStackTrace();
    		 System.out.println("ȡ��Ȩ����ʧ�ܣ�"+dbclass.getMsg());  
        }
        
        return 0;
     }

	static public Boolean isSpecialAccount(ConnectionFactory dbclass,String inst_no,String user_code)
      {
        String  queryStr = "";
        queryStr = "select Specil_Flag SFlag from T_MGT_OPERATOR_Y where Inst_No=? and User_Code=?";
        try {
        	String sflag = dbclass.doQueryString(queryStr,new Object[]{inst_no,user_code}); 
            if(sflag==null) return false;
            sflag =sflag.trim(); 
            if(sflag.equals("1"))  return true;
         }catch(Exception e) {
        	return false;
        }        
        return false;
     }

       
      static public String DateSub(String start,int days)
      {
      	int  y,m,d;
      	int  mdays;
      	String mm,dd;
     	
      	y=Integer.parseInt(start.substring(0,4));
      	m=Integer.parseInt(start.substring(4,6));
      	d=Integer.parseInt(start.substring(6));
     
      	d-=days;

      	while(d<1)
      	{
      		if(--m<1)
      		{
      			y--;
      			m=12;
      		}
       		mdays=30+(m<8? m%2 : (m+1)%2)-(m==2? 1+(((y%400 != 0) && (((y%4) != 0) || ((y%100)) == 0)) ? 1:0) : 0);
      		d=mdays+d;
      	}
     
      	if (d<10) dd="0"+d;	
      	else      dd=""+d;
    	if (m<10) mm="0"+m;
    	else      mm=""+m;
     
      	return ""+y+mm+dd;

      }
     
  
    
    
    /**
	 * Method��String ToMoneyFormat(String str_temp)
	 * Function ��str_tempת����RMB�ĸ�ʽ
	 * �����б?
	 *     String  str_temp:  Ҫת�����ַ�
	 * 
	 * ��������ת����RMB��ʽ���ַ�
	 * 
	 */
	static public String ToMoneyFormat(String str_temp)
	{
	    boolean isNegative = false; //�Ƿ��Ǹ���
	    int i=0;  //ѭ������
	    
	    if(str_temp.equals(""))
	    {  //����Ϊ�յ����
			str_temp="0";
		}
	    
	    if(str_temp.substring(0,1).equals("-")){
			str_temp = str_temp.substring(1,str_temp.length());
			isNegative = true; //���?��
		}

		if(str_temp.substring(0,1).equals(".")){
			if(str_temp.length() == 1){  //���� "."�����
				str_temp += "00";
			}
			str_temp = "0" + str_temp; //����".12"�����
		}
		
	    String[] Array_temp=str_temp.split("\\.");
	    if(Array_temp.length == 1)      //�������Ĵ�С��1��˵��û��С��㣬+ ��.00������ʾ��Ϊ����
	    {
	    	str_temp += ".00";    
	    }
	    else{
	    	if(Array_temp[1].length() == 1) //���ֻ��һλС����һ����
	    		str_temp += "0";	    	
	    }
	
	    //������ת����ɵ���ת��Ϊ������ʽ���磺123456��Ϊ��123��456
	    Array_temp=str_temp.split("\\.");
        String str1=Array_temp[0];
        String str2=Array_temp[1];
        
       int signa=0;  //��ǰ����˼�����
       int interlen=str1.length();
       
       if(interlen%3==1)
       {
          str1="00"+str1;
          signa=2;
       }
       if(interlen%3==2)
       {
	       str1="0"+str1;
	       signa=1;
       }
       
       int tt=(str1.length())/3; //Ҫ�Ӽ�������      
       String[] mm=new String[tt]; //�ֱ�涺�ŷֿ����ַ�
       for(i=0; i<tt; i++)
       {
           mm[i]=str1.substring(i*3,3+i*3);
       }
       
       String result="";
       
       for(i=0;i<mm.length;i++)
       {
       		result += mm[i];
       		result += ",";
       }
       
       result=result.substring(signa,result.length()-1); //ȥǰ��ӹ����
     //  System.out.println("���Ϊ��"+result);
        result=result+ "." + str2;
        
        if(isNegative == true)
            result = "-" + result;
      
	    return result;
	}
	
	/**��
	 * Method�� String GetNowDate(String format){
	 * Function: ȡ�õ�ǰ��ϵͳ����/ʱ��
	 * 		
	 * �������
	 * 		String format: ����/ʱ�� ��ʽ����"yyyyMMdd"��"yyyyMMddhhmmss"��
	 * 
	 * �������:
	 * 		��ǰ���ڵĸ�ʽ����
	 * 
	 **/
	static public String GetNowDate(String format){
	    Date nowDate = new Date();
		java.text.SimpleDateFormat formater = new java.text.SimpleDateFormat(format); 

		return formater.format(nowDate);
	}
	
	/**
	 * Method��String ToDateFormat(String str)
	 * 
	 * Function: ��strת���û��ɶ������ڸ�ʽ��1.yyyyMMddת��yyyy-MM-dd; 2.hhmmssת��hh:mm:ss; 3. yyyyMMddhhmmssת��yyyy-MM-dd hh:mm:ss��������ʶ��ԭ��ء�
	 * �����б?
	 *     String  str:  ���ڴ�
	 * 
	 * ���������û��ɶ���ʽ
	 * 
	 * @author fanxg
	 * 
	 */
	static public String ToDateFormat(String str)
	{
		//yyyyMMdd
		if(str.length() == 8){
			return str.substring(0,4) + "-" + str.substring(4,6) + "-" + str.substring(6,8);
		}
		//hhmmss
		if(str.length() == 6){
			return str.substring(0,2) + ":" + str.substring(2,4) + ":" + str.substring(4,6);
		}
		//yyyyMMddhhmmss
		if(str.length() == 14){
			return str.substring(0,4) + "-" + str.substring(4,6) + "-" + str.substring(6,8) + " " +str.substring(8,10) + ":" + str.substring(10,12) + ":" + str.substring(12,14);
		}
		
		return str; //����������ĸ�ʽ��ԭ���
	}
		
 

	  public static String EncodeStr(String sStr) 
	   {
		   String uniStr = "";       
           try
           {           
              byte[] tempByte = sStr.getBytes("GBK"); 
              uniStr = new String(tempByte,"ISO8859_1");       
              return  uniStr;        
           }
           catch(Exception ex)
           {            
              return null;       
           } 
	   }
	   public static String commServerByTcp(String tmpStr) 
	   {	       
	       String xmlFile=ResourcePool.getRootpath() + "/config/system.xml";
		       
	       String ServerIP=ResourcePool.GetConfigInfo( xmlFile ,"SockInfo","ServerIP")[0];
	       String ServerPortStr=ResourcePool.GetConfigInfo(xmlFile ,"SockInfo","ServerPort")[0];
	       String ConnectTimeStr=ResourcePool.GetConfigInfo( xmlFile ,"SockInfo","ConnectTime")[0];
	       String BlockTimeStr=ResourcePool.GetConfigInfo( xmlFile ,"SockInfo","BlockTime")[0];
	       
	       tmpStr=ResourcePool.ConnectToServer(ServerIP, ServerPortStr, ConnectTimeStr, BlockTimeStr,tmpStr);
	       return tmpStr;
	   }
	   /**
	    * 获取物理路径
	    * @return
	    */
	   public static String getRootPath(){
		   return ResourcePool.getRootpath();
	   }
	    /**
	     * ��ݴ���Ĳ���,�õ���ѯ�ַ� 
	     * @author tys
	     * @created 2010-3-15
	     * @param inst_type ������
	     * @param inst_no   ���
	     * @param net_no    ����
	     * @param UPINST_NO �ϲ����
	     * @param lastYear  ��������
	     * @param thisMonth ��������
	     * @param gr_gs_pj_Flag  gr:���� gs:��˾ pj:Ʊ�� hj:�ϼ���Ϣ
	     * @param curr_no   ������Ϣ   �ñ�־���Թ�˾������Ч
	     * 
	     * @return
	     */
	    public static String getQueryStr(String inst_type,String inst_no,String net_no,String UPINST_NO,String lastYear,String thisMonth,String gr_gs_pj_Flag,String curr_no){
	       	
	    	String gstable="(select DataDate,UpInst_No,Inst_No,CurrNo,Counter_BillNo,SubjectNo,Form,Bal,(case when substr(OUTDATE,1,4)||TJ_TYPE1 ='"+thisMonth.substring(0,4)+"A' then '1' else '0' end) xz "+
             "from T_FRDK_INFO a where DataDate='"+thisMonth+"' "+
             "union all "+
             "select DataDate,UpInst_No,Inst_No,CurrNo,Counter_BillNo,SubjectNo,Form,-Bal,'1' xz "+
             "from T_FRDK_ADD  where DataDate='"+thisMonth+"' and Status='1' "+
             "union all "+
             "select DataDate,UpInst_No,Inst_No,CurrNo,Old_Counter_BillNo,SubjectNo,Form,Bal,'0' xz "+
             "from T_FRDK_ADD  where DataDate='"+thisMonth+"' and Status='1' "+
             "union all "+
             "select DataDate,UpInst_No,Inst_No,CurrNo,Counter_BillNo,SubjectNo,Form,Bal,'0' xz "+
             "from T_FRDK_INFO  where DataDate='"+lastYear+"')";
	       	String whereStr = "where 1=1 ";
	   		String queryStr = "";
	       	
	        String linkInst="";
		    String T_INST_INFO="T_INST_INFO";
		    String outInst="Inst_No";   
		    String selInst="";   
	   		switch(inst_type.charAt(0))
	   		{
		   		case '1':
		   			linkInst += " '0000' ";
		   			T_INST_INFO="T_INST_INFO";
		   			break;
		   		case '2':
		   			linkInst += " a.UpInst_No ";
		   			T_INST_INFO="T_INST_INFO";
		   			outInst=UPINST_NO;
		   			if (!"".equalsIgnoreCase(inst_no)) {
		   				 selInst+=" and a.Inst_No='"+inst_no+"' ";
				   		 outInst="UpInst_No";		   				 
		   			}
		   			break;
		   		case '4':
		   			linkInst += " a.Inst_No ";
		   			T_INST_INFO="(select a.UpInst_No,a.UpINst_No1,b.Inst_No,a.LInst_No,a.Linst_Name,a.inst_Name from T_INST_DAY a,T_INST_DAY b where a.DataDate=b.DataDate and a.Inst_No=b."+UPINST_NO+" and a.Inst_Level<'3' and a.DataDate='"+thisMonth.substring(0,4)+"0101"+"')"; 
		   			outInst="UPINST_NO";
		   			if (!"".equalsIgnoreCase(inst_no)) {
		   				 selInst+=" and a."+UPINST_NO+"='"+inst_no+"' ";
		   			}
		   			break;
		   		case '5':
		   			linkInst += " a.Inst_No ";
		   			T_INST_INFO="(select a.UpInst_No,a.UpINst_No1,b.Inst_No,a.LInst_No,a.Linst_Name,a.inst_Name from T_INST_INFO a,T_INST_INFO b where a.Inst_No=b."+UPINST_NO+" and a.Inst_Level<'3' )"; 
		   			outInst="UPINST_NO";
		   			if (!"".equalsIgnoreCase(inst_no)) {
		   				 selInst+=" and a."+UPINST_NO+"='"+inst_no+"' ";
		   			}
		   			break;
		   		case '3':
		   			linkInst += " a.Inst_No ";
		   			T_INST_INFO="T_INST_INFO";
		   			 //��������
		   			 if (!"".equalsIgnoreCase(inst_no)) {
		   				 selInst+=" and a."+UPINST_NO+"='"+inst_no+"' ";
		   			 }
		   			 if(!"".equalsIgnoreCase(net_no)){
		   				selInst+=" and a.inst_no='"+net_no+"' ";
		   			 }
		   			break;
	   		}
	   	  	 	    
	     String gr_gs_pj_Info="";//��˾�����ˡ�Ʊ����Ϣ
	     String gr_info="";
	     String gs_info="";
	     String pj_info="";
	     
	     if("gr".equals(gr_gs_pj_Flag) || "hj".equals(gr_gs_pj_Flag)){//���˲���
	   	  
	   	  gr_info=
	   			" select "+ linkInst+ " inst,max(case when a.DataDate = '"+ lastYear+ "' then a.Form else null  end ) flag1 "
	   			+ ",max(case when a.DataDate = '"+ thisMonth+ "'  then a.Form else null end ) flag2 "
	   			+ ",max(case when a.datadate='"+lastYear+"' then a.bal else 0 end ) bal1"
	   			+ ",max(case when a.datadate='"+thisMonth+"' then a.bal else 0 end ) bal2"
	   			//����
	   			+ ",sum(case when a.datadate='"+lastYear+"' then  a.bal "   
	   			+ " when  a.datadate='"+thisMonth+"' then  -a.bal else 0 end ) bal_js "
	   			+ ",max(case when a.datadate='"+thisMonth+"' and a.OutDate like '"+thisMonth.substring(0,4)+"%' then a.bal else 0 end ) bal_xz "
	   			+ ",a.Account Account,a.JSno JSno,'' billno,'' Counter_BillNo"
	   			+ " from T_GRDK_INFO a " + whereStr+ " "
	   			+ " and (a.DataDate='" + thisMonth+ "' or a.DataDate ='" + lastYear+ "' ) " 
	   			+ " group by "+linkInst+",a.Account,a.JSno  ";	   	  
	     }
	     
	     if("gs".equals(gr_gs_pj_Flag) || "hj".equals(gr_gs_pj_Flag) ){//��˾����
	   	   
	   	    gs_info="  select "+ linkInst+ " inst,max(case when a.DataDate = '"+ lastYear+ "' then d.dictname else null end ) flag1 "
	   				+ ",max(case when a.DataDate = '"+ thisMonth+ "' then  d.dictname else null end ) flag2 "
	   				+ ",sum(case when a.datadate='"+lastYear+"'  then a.bal else 0 end ) bal1"         
	   				+ ",sum(case when a.datadate='"+thisMonth+"' then a.bal else 0 end ) bal2"         
	   				//����
	   				+ ",sum(case when a.datadate='"+lastYear+"' then  a.bal "   
	   				+"  when  a.datadate='"+thisMonth+"' then  -a.bal else 0 end ) bal_js "
	   				+ ",sum(decode(a.datadate,'"+thisMonth+"', case when xz='1' then a.bal else 0 end,0) ) bal_xz "
	   				+ ",'' Account,0 JSno,'' billno,a.Counter_BillNo Counter_BillNo "
	   				+ " from "+gstable+" a"
	   				+ ",(select * from t_sys_dict where dicttype='6b') d "
	   				+ whereStr+ " and a.form=d.dictno ";
	   	        
	   	       //�ӱ�������
	   		    if( (null!=curr_no) && (!"".equals(curr_no)) ){
	   		    	gs_info+=" and a.CurrNo='"+curr_no+"' ";
	   		    }
	   	    
//	   		   gs_info+=" and (a.DataDate='" + thisMonth+ "' or a.DataDate ='" + lastYear+ "' ) and a.form=d.dictno "  
	   			//��Ŀ����
	   		   gs_info+=" and (SUBJECTNO in(select SUBJECTNO from T_ITEM_SUBJECT where ITEMNO=603  and OPFLAG ='+') " 
	   			+ " OR (substr(SUBJECTNO,1,4) in(select SUBJECTNO from T_ITEM_SUBJECT where ITEMNO=603  and OPFLAG ='+') "
	   			+ " and not exists(select SUBJECTNO from T_ITEM_SUBJECT where ITEMNO=603 and OPFLAG ='-' and SubjectNo=a.SubjectNo))) "
	   			+ " group by "+linkInst+",a.Counter_BillNo ";
	     }
	     
	     if("pj".equals(gr_gs_pj_Flag) || "hj".equals(gr_gs_pj_Flag)){//Ʊ�ݲ���
	   	  pj_info=
	   		"  select "+ linkInst+ " inst,max(case when a.DataDate = '"+ lastYear+ "' then (case when a.overnum = 0 then  '��' "
	   		+ " when (a.overnum > 0 and a.overnum <= 90) then '��ע' when (a.overnum > 90 and a.overnum <= 180)"
	   		+ " then '�μ�' else '����' end  ) else null end ) flag1 "
	   		+ " ,max(case when a.DataDate = '"+ thisMonth+ "' then (case when a.overnum = 0 then  '��'"
	   		+ " when (a.overnum > 0 and a.overnum <= 90) then '��ע' when (a.overnum > 90 and a.overnum <= 180)"
	   		+ " then '�μ�' else '����' end  ) else null end ) flag2" 
	   		+ " ,sum(case when a.datadate='"+lastYear+"' then a.bal else 0 end ) bal1"
	   		+ ",sum(case when a.datadate='"+thisMonth+"' then a.bal else 0 end ) bal2"
	   		//����
	   		+ ",sum(case when a.datadate='"+lastYear+"' then  a.bal "   
	   		+"  when  a.datadate='"+thisMonth+"' then  -a.bal else 0 end ) bal_js "
	   		+ ",sum(case when a.datadate='"+thisMonth+"' and a.OpenDate like '"+thisMonth.substring(0,4)+"%' then a.bal else 0 end ) bal_xz "
	   		+" ,'' Account,0 JSno,billno,'' Counter_BillNo "
	   		
	   		+ " from T_BILL_INFO a " + whereStr+ " and (a.DataDate='" + thisMonth+ "' or a.DataDate ='" + lastYear+ "' ) "
	   		+" group by "+linkInst+",a.BillNo ";
	   	  
	     }
	     
	     if("gr".equals(gr_gs_pj_Flag)){//���˲���
	   	  
	   	   gr_gs_pj_Info=gr_info;
	   	  
	     }else if("gs".equals(gr_gs_pj_Flag)){//��˾����
	   	  
	   	   gr_gs_pj_Info=gs_info;
	   	  
	     }else if("pj".equals(gr_gs_pj_Flag)){//Ʊ�ݲ���
	   	  
	   	   gr_gs_pj_Info=pj_info;
	   	  
	     }else if("hj".equals(gr_gs_pj_Flag)){//�ϼ���Ϣ
	   	   
	       if(!"001".equals(curr_no)){//���˺�Ʊ�ݲ���û�б�����Ϣ��Ŀǰֻ�й�˾�������������Ϣ
	    	 
	    	 gr_gs_pj_Info= gs_info;
	    	 
	       }else{
	   	     gr_gs_pj_Info=gr_info
	   	                +" union all "
	   	                +gs_info
	   	                +" union all "
	   	                +pj_info;
	       }
	   	   
	     }else{
	   	  logger.error("����ı�־����:gr_gs_pj_Flag="+gr_gs_pj_Flag);
	     }
	     
	     
	     //�������ַ�
	   	queryStr="select /*+ordered*/ a."+outInst+" Inst_No,max(a.linst_no) linst_no,max(case when a.linst_name is null then a.inst_name else a.linst_name end ) Inst_Name " 
	   		    
	   			+", sum(case when b.flag1='��' then b.bal1 else 0 end ) zh_nc " //�����
	   		    +", sum(case when b.flag1='��' then b.bal_js else 0 end ) zh_js " //��(�������)
	   			+", sum(case when b.flag1='��' and b.flag2 ='��' then b.bal2 else 0 end ) zh_zc " //����
	   			+", sum(case when b.flag1='��' and b.flag2 ='��ע' then b.bal2 else 0 end ) zh_gz " //��ע���
	   			+", sum(case when b.flag1='��' and b.flag2 ='�μ�' then b.bal2 else 0 end ) zh_cj " //�μ����
	   			+", sum(case when b.flag1='��' and b.flag2 ='����' then b.bal2 else 0 end ) zh_ky " //���ɴ��
	   			+", sum(case when b.flag1='��' and b.flag2 ='��ʧ' then b.bal2 else 0 end ) zh_ss " //��ʧ���(��ǰû��)
	   			
	   			+", sum(case when b.flag1='��ע' then b.bal1 else 0 end ) gz_nc " //��ע���
	   			+", sum(case when b.flag1='��ע' then b.bal_js else 0 end ) gz_js " //��ע(�������)
	   			+", sum(case when b.flag1='��ע' and b.flag2 ='��' then b.bal2 else 0 end ) gz_zc " //����
	   			+", sum(case when b.flag1='��ע' and b.flag2 ='��ע' then b.bal2 else 0 end ) gz_gz " //��ע���
	   			+", sum(case when b.flag1='��ע' and b.flag2 ='�μ�' then b.bal2 else 0 end ) gz_cj " //�μ����
	   			+", sum(case when b.flag1='��ע' and b.flag2 ='����' then b.bal2 else 0 end ) gz_ky " //���ɴ��
	   			+", sum(case when b.flag1='��ע' and b.flag2 ='��ʧ' then b.bal2 else 0 end ) gz_ss " //��ʧ���(��ǰû��) 
	   			
	   			+", sum(case when b.flag1='�μ�' then b.bal1 else 0 end ) cj_nc " //�μ����
	   			+", sum(case when b.flag1='�μ�' then b.bal_js else 0 end ) cj_js " //�μ�(�������)
	   			+", sum(case when b.flag1='�μ�' and b.flag2 ='��' then b.bal2 else 0 end ) cj_zc " //����
	   			+", sum(case when b.flag1='�μ�' and b.flag2 ='��ע' then b.bal2 else 0 end ) cj_gz " //��ע���
	   			+", sum(case when b.flag1='�μ�' and b.flag2 ='�μ�' then b.bal2 else 0 end ) cj_cj " //�μ����
	   			+", sum(case when b.flag1='�μ�' and b.flag2 ='����' then b.bal2 else 0 end ) cj_ky " //���ɴ��
	   			+", sum(case when b.flag1='�μ�' and b.flag2 ='��ʧ' then b.bal2 else 0 end ) cj_ss " //��ʧ���(��ǰû��)
	   			
	   			+", sum(case when b.flag1='����' then b.bal1 else 0 end ) ky_nc " //�������
	   			+", sum(case when b.flag1='����' then b.bal_js else 0 end ) ky_js " //����(�������)
	   			+", sum(case when b.flag1='����' and b.flag2 ='��' then b.bal2 else 0 end ) ky_zc " //����
	   			+", sum(case when b.flag1='����' and b.flag2 ='��ע' then b.bal2 else 0 end ) ky_gz " //��ע���
	   			+", sum(case when b.flag1='����' and b.flag2 ='�μ�' then b.bal2 else 0 end ) ky_cj " //�μ����
	   			+", sum(case when b.flag1='����' and b.flag2 ='����' then b.bal2 else 0 end ) ky_ky " //���ɴ��
	   			+", sum(case when b.flag1='����' and b.flag2 ='��ʧ' then b.bal2 else 0 end ) ky_ss " //��ʧ���(��ǰû��)
	   			  
	   			+", sum(case when b.flag1='��ʧ' then b.bal1 else 0 end ) ss_nc " //��ʧ���
	   			+", sum(case when b.flag1='��ʧ' then b.bal_js else 0 end ) ss_js " //��ʧ(�������)
	   			+", sum(case when b.flag1='��ʧ' and b.flag2 ='��' then b.bal2 else 0 end ) ss_zc " //��ʧ���
	   			+", sum(case when b.flag1='��ʧ' and b.flag2 ='��ע' then b.bal2 else 0 end ) ss_gz " //��ʧ���
	   			+", sum(case when b.flag1='��ʧ' and b.flag2 ='�μ�' then b.bal2 else 0 end ) ss_cj " //��ʧ���
	   			+", sum(case when b.flag1='��ʧ' and b.flag2 ='����' then b.bal2 else 0 end ) ss_ky " //��ʧ���
	   			+", sum(case when b.flag1='��ʧ' and b.flag2 ='��ʧ' then b.bal2 else 0 end ) ss_ss " //��ʧ���(��ǰû��)
	   			 
	   			//�������弶�������
	   			+", sum(case when b.flag1='��' then b.bal1 else 0 end ) nc_zc " //�����
	   			+", sum(case when b.flag1='��ע' then b.bal1 else 0 end ) nc_gz " //��ע 
	   			+", sum(case when b.flag1='�μ�' then b.bal1 else 0 end ) nc_cj " //�μ����
	   			+", sum(case when b.flag1='����' then b.bal1 else 0 end ) nc_ky " //���ɴ��
	   			+", sum(case when b.flag1='��ʧ' then b.bal1 else 0 end ) nc_ss " //��ʧ���
	   			+", sum(case when b.flag1 is not null  then b.bal1 else 0 end ) nc_hj " //������ϼ�
	   			
	   			//��ĩ����弶�������
	   			+", sum(case when b.flag2='��' then b.bal2 else 0 end ) qm_zc " //�����
	   			+", sum(case when b.flag2='��ע' then b.bal2 else 0 end ) qm_gz " //��ע 
	   			+", sum(case when b.flag2='�μ�' then b.bal2 else 0 end ) qm_cj " //�μ����
	   			+", sum(case when b.flag2='����' then b.bal2 else 0 end ) qm_ky " //���ɴ��
	   			+", sum(case when b.flag2='��ʧ' then b.bal2 else 0 end ) qm_ss " //��ʧ���
	   			+", sum(case when b.flag2 is not null  then b.bal2 else 0 end ) qm_hj " //��ĩ���ϼ�
	   			
	   			//������������弶�������
	   			+", sum(case when b.flag1 is null and b.flag2='��' then b.bal_xz else 0 end ) xz_zc " //�����
	   			+", sum(case when b.flag1 is null and b.flag2='��ע' then b.bal_xz else 0 end ) xz_gz " //��ע 
	   			+", sum(case when b.flag1 is null and b.flag2='�μ�' then b.bal_xz else 0 end ) xz_cj " //�μ����
	   			+", sum(case when b.flag1 is null and b.flag2='����' then b.bal_xz else 0 end ) xz_ky " //���ɴ��
	   			+", sum(case when b.flag1 is null and b.flag2='��ʧ' then b.bal_xz else 0 end ) xz_ss " //��ʧ���
	   			+", sum(case when b.flag1 is null and b.flag2 is not null  then b.bal_xz else 0 end ) xz_hj " //�����������ϼ�
	   			
	   	        +" from "+T_INST_INFO+" a ,(select * from ( " +gr_gs_pj_Info+" ) ) b "  
	   		    +" where a.inst_no=b.inst "+selInst +" group by  a."+outInst+" order by  a."+outInst+" ";
		   	  logger.info(queryStr);
	       	return queryStr;
	       	
	       }
	    
	     
	     /**
	      * ��ȡ�ֹ����ֲ�ѯ�ַ�
	      * 
	      * @author tys
	      * @created 2010-3-15
	      * @param inst_type  ������
	      * @param inst_no    ���
	      * @param net_no     ����
	      * @param UPINST_NO  �ϲ����
	      * @param lastYear   ��������
	      * @param thisMonth  ��������
	      * @param queryDate  ��ѯ����
	      * @param ym         ��ѯ�·�
	      * @param rate       ����
	      * @param gr_gs_Flag gr:���� gs:��˾  pj:Ʊ�� hj:�ϼ���Ϣ
	      * @param curr_no    ������Ϣ 
	      * 
	      * @return
	      */
	      
	      public static String getQueryStr_SgInfo(String inst_type,String inst_no,String net_no,String UPINST_NO,String lastYear,String thisMonth,String queryDate,String gr_gs_Flag,String curr_no){
	   	   
	   	   
	           //��������������(��������Ϊ:�ֽ����ա�������ʡ����˺�������ʽ����)
	   		String queryStr;
	   		
	   		String whereStr=" where 1=1 ";
	   		String selStrOut="";
	   		String groupByStr="";
	   		
	   		String QSZH_Class_Info="";//�칤����ı�ı�ʾ
	   		String Loan_LoanSubject_Info="";//�ֹ����ֵı�ı�ʾ


	   		if("gr".equals(gr_gs_Flag)){//���˲���
	   			
	   			QSZH_Class_Info=" and Class!='���˴��' and Class!='Ʊ��' ";
	   			Loan_LoanSubject_Info=" and LoanSubject='���˴��' ";
	   			
	   		}else if("gs".equals(gr_gs_Flag)) {//��˾����
	   			
	   			QSZH_Class_Info=" and Class='���˴��' ";
	   			Loan_LoanSubject_Info=" and LoanSubject='��˾���' ";
	   			
	   		}else if("pj".equals(gr_gs_Flag)){
	   			QSZH_Class_Info=" and Class='Ʊ��' ";
	   			Loan_LoanSubject_Info=" and LoanSubject='Ʊ��' ";
	   		}else if("hj".equals(gr_gs_Flag)){
	   			QSZH_Class_Info=" ";
	   			Loan_LoanSubject_Info=" and LoanSubject in ('��˾���','���˴��','Ʊ��') ";
	   		}else{
	   			logger.error("���˹�˾��ʾ����:gr_gs_Flag="+gr_gs_Flag);
	   		}
	   		
	   		if( (null!=curr_no) && (!"".equals(curr_no) ) ){
	   			QSZH_Class_Info+=" and currno='"+curr_no+"' ";
	   			Loan_LoanSubject_Info+=" and currno='"+curr_no+"' "; 
	   		}
	   		
	   		String t_inst_info=" t_inst_info ";
	   		String linkStr="inst_no ";
	   		switch(inst_type.charAt(0))
	   		{
		   		case '1':
		   			selStrOut += " select '0000' inst ";
		   			t_inst_info="(select a.UpInst_No,b.Inst_No,a.LInst_No,a.Linst_Name,a.inst_Name from T_INST_INFO a,T_INST_INFO b where a.Inst_No='0000' and b.Inst_No<>'0000')"; 
		   			groupByStr += " group by c.upinst_no ";
		   			break;
		   		case '2':
		   			selStrOut += " select c.INST_NO inst ";
		   			if (!(inst_no.equals("") || inst_no == null)) {
		   				whereStr += " and c.Inst_No='" + inst_no + "' ";
		   			}  
		   			groupByStr += " group by c.INST_NO ";
			   		linkStr="upinst_no ";
		   			break;
		   		case '4':
		   			selStrOut += " select c."+UPINST_NO+" inst ";
		   			t_inst_info="(select a.UpInst_No,a.UpInst_No1,b.Inst_No,a.LInst_No,a.Linst_Name,a.inst_Name from T_INST_DAY a,T_INST_DAY b where a.DataDate=b.DataDate and a.Inst_No=b."+UPINST_NO+" and a.Inst_Level<'3' and a.DataDate='"+thisMonth.substring(0,4)+"0101"+"')"; 
		   			if (!(inst_no.equals("") || inst_no == null)) {
		   				whereStr += " and c."+UPINST_NO+"='" + inst_no + "' ";
		   			}  
		   			groupByStr += " group by c."+UPINST_NO+" ";
		   			break;
		   		case '5':
		   			selStrOut += " select c."+UPINST_NO+" inst ";
		   			t_inst_info="(select a.UpInst_No,a.UpInst_No1,b.Inst_No,a.LInst_No,a.Linst_Name,a.inst_Name from T_INST_INFO a,T_INST_INFO b where a.Inst_No=b."+UPINST_NO+" and a.Inst_Level<'3')"; 
		   			if (!(inst_no.equals("") || inst_no == null)) {
		   				whereStr += " and c."+UPINST_NO+"='" + inst_no + "' ";
		   			}  
		   			groupByStr += " group by c."+UPINST_NO+" ";
		   			break;
		   		case '3':
		   			selStrOut += " select c.Inst_No inst ";
		   			if (!(inst_no.equals("") || inst_no == null)) {
		   				whereStr += " and c."+UPINST_NO+"='" + inst_no + "' ";
		   			}
		   			if(net_no!=null && !net_no.equalsIgnoreCase("")){
		   				whereStr = whereStr + " and c.Inst_No = '" + net_no+ "' ";
		   			}
		   			groupByStr += " group by c.inst_no ";
		   			break;
	   		}
	   			   		
	   		queryStr=
	   			"select /*+ordered*/" +
	   			" b.nc_dz nc_dz  " +
	   			",b.nc_hx nc_hx  " +
	   			",b.nc_qt nc_qt  " +
	   			",a.nc_qs nc_hj  " +
	   			",a.nc_qs1 nc_hj1  " +
	   			
	   			",b.zcjs_dz zcjs_dz  " +
	   			",b.zcjs_hx zcjs_hx  " +
	   			",b.zcjs_qt zcjs_qt  "+
	   			",a.zcjs_qs zcjs_hj  " +
	   			",a.zcjs_qs1 zcjs_hj1  " +
	   			
	   			//����һ�㣬��Ϣ������
	   			",a.bnxf_js bnxf_js " +
	   			",(a.bnxf_qs-nvl(b.bnxf_qs,0)) bnxf_qs " +
	   			",b.bnxf_dz bnxf_dz  " +
	   			",b.bnxf_hx bnxf_hx  " +
	   			",b.bnxf_qt bnxf_qt  " +
	   			",a.bnxf_qs bnxf_hj  " +
	   			",a.bnxf_qs1 bnxf_hj1  " +
	   			
	   			//20100304 add
	   			//���մ���ʱΪ�μ����
	   			",b.qscz_cj_dz qscz_cj_dz  " +
	   			",b.qscz_cj_hx qscz_cj_hx  " +
	   			",b.qscz_cj_qt qscz_cj_qt  " +
	   			",a.qscz_cj_hj qscz_cj_hj  " +
	   			",a.qscz_cj_hj1 qscz_cj_hj1  " +
	   			//���մ���ʱΪ���ɴ��
	   			",b.qscz_ky_dz qscz_ky_dz  " +
	   			",b.qscz_ky_hx qscz_ky_hx  " +
	   			",b.qscz_ky_qt qscz_ky_qt  " +
	   			",a.qscz_ky_hj qscz_ky_hj  " +
	   			",a.qscz_ky_hj1 qscz_ky_hj1  " +
	   			//���մ���ʱΪ��ʧ���
	   			",b.qscz_ss_dz qscz_ss_dz  " +
	   			",b.qscz_ss_hx qscz_ss_hx  " +
	   			",b.qscz_ss_qt qscz_ss_qt  " +
	   			",a.qscz_ss_hj qscz_ss_hj  " +
	   			",a.qscz_ss_hj1 qscz_ss_hj1  " +
	   			//ʮ�ġ����մ���ʱΪ�μ����
	   			",a.qscz_cj_xz qscz_cj_xz"+
	   			",a.qscz_cj_nczc qscz_cj_nczc"+
	   			",a.qscz_cj_ncgz qscz_cj_ncgz"+
	   			",a.qscz_cj_nccj qscz_cj_nccj"+
	   			",a.qscz_cj_ncky qscz_cj_ncky"+
	   			",a.qscz_cj_ncss qscz_cj_ncss"+
	   			//ʮ�ġ����մ���ʱΪ���ɴ��
	   			",a.qscz_ky_xz qscz_ky_xz"+
	   			",a.qscz_ky_nczc qscz_ky_nczc"+
	   			",a.qscz_ky_ncgz qscz_ky_ncgz"+
	   			",a.qscz_ky_nccj qscz_ky_nccj"+
	   			",a.qscz_ky_ncky qscz_ky_ncky"+
	   			",a.qscz_ky_ncss qscz_ky_ncss"+
	   			//ʮ�ġ����մ���ʱΪ��ʧ���
	   			",a.qscz_ss_xz qscz_ss_xz"+
	   			",a.qscz_ss_nczc qscz_ss_nczc"+
	   			",a.qscz_ss_ncgz qscz_ss_ncgz"+
	   			",a.qscz_ss_nccj qscz_ss_nccj"+
	   			",a.qscz_ss_ncky qscz_ss_ncky"+
	   			",a.qscz_ss_ncss qscz_ss_ncss"+
	   			
	   			" from " +
	   			
	   			//�����̨ϵͳ�Զ������
	   			" (" +
	   			" "+selStrOut+"" +
	   			",sum( a.QSAMT ) nc_qs" +//�����������
	   			",sum( case when QsFlag='0' then a.QSAMT else 0 end) nc_qs1" +//�����������
	   			",sum( a.QSAMT1) zcjs_qs" +/*�����������*/
	   			",sum( case when QsFlag='0' then a.QSAMT1 else 0 end) zcjs_qs1" +//�����������
	   			",sum( a.QSAMT2) bnxf_qs " +/*��������*/
	   			",sum( case when QsFlag='0' then a.QSAMT2 else 0 end) bnxf_qs1 " +/*��������*/
	   			//20100304 add ���귢�ŵ����ջ� -->��������������(���ڼ���)
	   			",sum( a.HKAMT) bnxf_js " +/*���귢�ŵ����ջ�*/
	   			",sum( a.QSAMT3 ) qscz_cj_hj " +//���մ��ôμ�--�ϼ�
	   			",sum( case when QsFlag='0' then a.QSAMT3  else 0 end) qscz_cj_hj1 " +//���մ��ôμ�--�ϼ�
	   			",sum( a.QSAMT4 ) qscz_ky_hj " +//���մ��ÿ���--�ϼ�
	   			",sum( case when QsFlag='0' then a.QSAMT4   else 0 end) qscz_ky_hj1 " +//���մ��ÿ���--�ϼ�
	   			",sum( a.QSAMT5 ) qscz_ss_hj " +//���մ�����ʧ--�ϼ�
	   			",sum( case when QsFlag='0' then a.QSAMT5   else 0 end) qscz_ss_hj1 " +//���մ�����ʧ--�ϼ�
	   			//ʮ�ġ����մ���ʱΪ�μ����		
	   			",sum( case when QsFlag<>'0' then a.QSAMT6   else 0 end ) qscz_cj_xz " +//���մ���ʱΪ�μ�--��������
	   			",sum( case when QsFlag<>'0' then a.QSAMT9   else 0 end ) qscz_cj_nczc " +//���մ���ʱΪ�μ�--�����
	   			",sum( case when QsFlag<>'0' then a.QSAMT10   else 0 end ) qscz_cj_ncgz " +//���մ���ʱΪ�μ�--�����ע
	   			",sum( case when QsFlag<>'0' then a.QSAMT11  else 0 end ) qscz_cj_nccj " +//���մ���ʱΪ�μ�--����μ�
	   			",sum( case when QsFlag<>'0' then a.QSAMT12   else 0 end ) qscz_cj_ncky " +//���մ���ʱΪ�μ�--�������
	   			",sum( case when QsFlag<>'0' then a.QSAMT13   else 0 end ) qscz_cj_ncss " +//���մ���ʱΪ�μ�--�����ʧ
	   			
	   			//ʮ�ġ����մ���ʱΪ���ɴ��		
	   			",sum( case when QsFlag<>'0' then a.QSAMT7   else 0 end ) qscz_ky_xz " +//���մ���ʱΪ����--��������
	   			",sum( case when QsFlag<>'0' then a.QSAMT14   else 0 end ) qscz_ky_nczc " +//���մ���ʱΪ����--�����
	   			",sum( case when QsFlag<>'0' then a.QSAMT15   else 0 end ) qscz_ky_ncgz " +//���մ���ʱΪ����--�����ע
	   			",sum( case when QsFlag<>'0' then a.QSAMT16   else 0 end ) qscz_ky_nccj " +//���մ���ʱΪ����--����μ�
	   			",sum( case when QsFlag<>'0' then a.QSAMT17   else 0 end ) qscz_ky_ncky " +//���մ���ʱΪ����--�������
	   			",sum( case when QsFlag<>'0' then a.QSAMT18   else 0 end ) qscz_ky_ncss " +//���մ���ʱΪ����--�����ʧ
	   			
	   			//ʮ�ġ����մ���ʱΪ��ʧ���		
	   			",sum( case when QsFlag<>'0' then a.QSAMT8   else 0 end ) qscz_ss_xz " +//���մ���ʱΪ��ʧ--��������
	   			",sum( case when QsFlag<>'0' then a.QSAMT19   else 0 end ) qscz_ss_nczc " +//���մ���ʱΪ��ʧ--�����
	   			",sum( case when QsFlag<>'0' then a.QSAMT20   else 0 end ) qscz_ss_ncgz " +//���մ���ʱΪ��ʧ--�����ע
	   			",sum( case when QsFlag<>'0' then a.QSAMT21   else 0 end ) qscz_ss_nccj " +//���մ���ʱΪ��ʧ--����μ�
	   			",sum( case when QsFlag<>'0' then a.QSAMT22   else 0 end ) qscz_ss_ncky " +//���մ���ʱΪ��ʧ--�������
	   			",sum( case when QsFlag<>'0' then a.QSAMT23   else 0 end ) qscz_ss_ncss " +//���մ���ʱΪ��ʧ--�����ʧ
	   		    
	   			" from " +
	   			" (select a.* from T_QSZH_INFO a where DataDate>'"+lastYear+"' and DataDate<='"+thisMonth+"' "+QSZH_Class_Info+" ) a" +
	   			","+t_inst_info+" c " +
	   			" "+whereStr+" and a."+linkStr+"=c.inst_no " +
	   			"  "+groupByStr+" " +
	   			" ) a " +
	   			
	   			//�����ֹ�����
	   			",(" +
	   			" "+selStrOut+" " +
	   			",sum( case when a.BadPeriodKind='���Ϊ����' and a.HandleMode in ('����ִ�','���˺���','����ʽ') then a.Amount else 0 end) nc_qs " +
	   			",sum( case when a.BadPeriodKind='���Ϊ����' and a.HandleMode in ('����ִ�') then a.Amount else 0 end) nc_dz " +
	   			",sum( case when a.BadPeriodKind='���Ϊ����' and a.HandleMode in ('���˺���') then a.Amount else 0 end) nc_hx " +
	   			",sum( case when a.BadPeriodKind='���Ϊ����' and a.HandleMode in ('����ʽ') then a.Amount else 0 end) nc_qt " +
	   			
	   			",sum( case when a.BadPeriodKind='���Ϊ��ע��' and a.HandleMode in ('����ִ�','���˺���','����ʽ') then a.Amount else 0 end ) zcjs_qs"+
	   			",sum( case when a.BadPeriodKind='���Ϊ��ע��' and a.HandleMode in ('����ִ�') then a.Amount else 0 end ) zcjs_dz"+
	   			",sum( case when a.BadPeriodKind='���Ϊ��ע��' and a.HandleMode in ('���˺���') then a.Amount else 0 end ) zcjs_hx"+
	   			",sum( case when a.BadPeriodKind='���Ϊ��ע��' and a.HandleMode in ('����ʽ') then a.Amount else 0 end ) zcjs_qt"+
	   			
	   			",sum( case when a.BadPeriodKind='�����·���' and a.HandleMode in ('����ִ�','���˺���','����ʽ') then a.Amount else 0 end ) bnxf_qs"+
	   			",sum( case when a.BadPeriodKind='�����·���' and a.HandleMode in ('����ִ�') then a.Amount else 0 end ) bnxf_dz"+
	   			",sum( case when a.BadPeriodKind='�����·���' and a.HandleMode in ('���˺���') then a.Amount else 0 end ) bnxf_hx"+
	   			",sum( case when a.BadPeriodKind='�����·���' and a.HandleMode in ('����ʽ') then a.Amount else 0 end ) bnxf_qt"+
	   			
	   			//20100304 add
	   			//���մ���ʱΪ�μ����
	   			",sum( case when a.LoanState='�μ�' and a.HandleMode in ('����ִ�','���˺���','����ʽ') then a.Amount else 0 end ) qscz_cj_qs"+
	   			",sum( case when a.LoanState='�μ�' and a.HandleMode in ('����ִ�') then a.Amount else 0 end ) qscz_cj_dz"+
	   			",sum( case when a.LoanState='�μ�' and a.HandleMode in ('���˺���') then a.Amount else 0 end ) qscz_cj_hx"+
	   			",sum( case when a.LoanState='�μ�' and a.HandleMode in ('����ʽ') then a.Amount else 0 end ) qscz_cj_qt"+
	   			//���մ���ʱΪ���ɴ��
	   			",sum( case when a.LoanState='����' and a.HandleMode in ('����ִ�','���˺���','����ʽ') then a.Amount else 0 end ) qscz_ky_qs"+
	   			",sum( case when a.LoanState='����' and a.HandleMode in ('����ִ�') then a.Amount else 0 end ) qscz_ky_dz"+
	   			",sum( case when a.LoanState='����' and a.HandleMode in ('���˺���') then a.Amount else 0 end ) qscz_ky_hx"+
	   			",sum( case when a.LoanState='����' and a.HandleMode in ('����ʽ') then a.Amount else 0 end ) qscz_ky_qt"+
	   			//���մ���ʱΪ��ʧ���
	   			",sum( case when a.LoanState='��ʧ' and a.HandleMode in ('����ִ�','���˺���','����ʽ') then a.Amount else 0 end ) qscz_ss_qs"+
	   			",sum( case when a.LoanState='��ʧ' and a.HandleMode in ('����ִ�') then a.Amount else 0 end ) qscz_ss_dz"+
	   			",sum( case when a.LoanState='��ʧ' and a.HandleMode in ('���˺���') then a.Amount else 0 end ) qscz_ss_hx"+
	   			",sum( case when a.LoanState='��ʧ' and a.HandleMode in ('����ʽ') then a.Amount else 0 end ) qscz_ss_qt"+
	   			
	   			"  from  (select a.* from T_Loan_Cancel a where LoanCancelDate>'"+lastYear+"' and  LoanCancelDate<='"+queryDate+"' "+Loan_LoanSubject_Info+" ) a" +
	   			","+t_inst_info+" c " +
	   			" "+whereStr+"  and a."+linkStr+"=c.inst_no " +
	   			"  "+groupByStr+" " +
	   			" ) b " +
	   			
	   			" where a.inst=b.inst(+) ";
	   		 
	   		 
	   		//�������
		   	System.out.println(queryStr);

	   		return queryStr;
	   	   
	      }
	      
	      @SuppressWarnings("rawtypes")
		public static String getuserRole(String inst,String user, ConnectionFactory dbclass) 
	    {
    	    String sqlStr="select distinct Role_Name from T_user_private a,T_SYS_ROLE b where to_number(a.Private_Code)=b.Role_Code and a.Private_Type='1' and a.Inst_No='"+inst+"' and a.User_Code='"+user+"'";
            
            String value="";
    	    ArrayList tmpv = dbclass.doQuery(sqlStr,0,0);
			if (tmpv == null) return "";
			else 
			{
				for (int i = 0; i < tmpv.size(); i++) 
				{
					Hashtable tmph = (Hashtable) tmpv.get(i);
					String Role_Name = Tools.trimNull((String) tmph
							.get("Role_Name".toUpperCase()));
					if(i==0) value="'"+Role_Name+"'";
					else value+=",'"+Role_Name+"'";
				}
			}
         return value;   
	    }
	      
	      public static String DBSql_1(String reptdate,String inst_type,String inst_no,String net_no,String curr_no,String dataFlag,String dataFlag1,String dataNo,double rate)
	      {
	      	String sqlStr = "";
	      	String Str11="select nvl(round(sum(LendBal)/10000),0) as LendBal," +
				"nvl(round(sum(UpDayLendBalC)/10000),0) as UpDayLendBalC," +
				"nvl(round(sum(UpYearLendBalC)/10000),0) as UpYearLendBalC, " +
				"nvl(round(sum(UpMonthLendBalC)/10000),0) as UpMonthLendBalC," +
				"nvl(round(sum(UpTenDayLendBalC)/10000),0) as UpTenDayLendBalC," +
				"nvl(round(sum(UpYearSameLendBalC)/10000),0) as UpYearSameLendBalC ";
	      	String Str12="select nvl(round(sum(LoanBal)/10000),0) as LendBal," +
                "nvl(round(sum(UpDayLoanBalC)/10000),0) as UpDayLendBalC," +
                "nvl(round(sum(UpYearLoanBalC)/10000),0) as UpYearLendBalC, " +
				 "nvl(round(sum(UpMonthLoanBalC)/10000),0) as UpMonthLendBalC," +
				 "nvl(round(sum(UpTenDayLoanBalC)/10000),0) as UpTenDayLendBalC," +
				 "nvl(round(sum(UpYearSameLoanBalC)/10000),0) as UpYearSameLendBalC ";
	      	String Str21="select nvl(round(sum(LendBal*r.EXCHRATE)/10000),0) as LendBal," +
				"nvl(round(sum(UpDayLendBalC*r.EXCHRATE)/10000),0) as UpDayLendBalC," +
				"nvl(round(sum(UpYearLendBalC*r.EXCHRATE)/10000),0) as UpYearLendBalC, " +
				"nvl(round(sum(UpMonthLendBalC*r.EXCHRATE)/10000),0) as UpMonthLendBalC," +
				"nvl(round(sum(UpTenDayLendBalC*r.EXCHRATE)/10000),0) as UpTenDayLendBalC," +
				"nvl(round(sum(UpYearSameLendBalC*r.EXCHRATE)/10000),0) as UpYearSameLendBalC ";
	      	String Str22="select nvl(round(sum(LoanBal*r.EXCHRATE)/10000),0) as LendBal," +
			    "nvl(round(sum(UpDayLoanBalC*r.EXCHRATE)/10000),0) as UpDayLendBalC," +
				"nvl(round(sum(UpYearLoanBalC*r.EXCHRATE)/10000),0) as UpYearLendBalC, " +
				"nvl(round(sum(UpMonthLoanBalC*r.EXCHRATE)/10000),0) as UpMonthLendBalC," +
				"nvl(round(sum(UpTenDayLoanBalC*r.EXCHRATE)/10000),0) as UpTenDayLendBalC," +
				"nvl(round(sum(UpYearSameLoanBalC*r.EXCHRATE)/10000),0) as UpYearSameLendBalC ";
	      	String Str31="select nvl(round(sum(decode(t.CURRNO,'001',LendBal,LendBal*r.EXCHRATE*"+rate+"))/10000),0) as LendBal," +
				"nvl(round(sum(decode(t.CURRNO,'001',UpDayLendBalC,UpDayLendBalC*r.EXCHRATE*"+rate+"))/10000),0) as UpDayLendBalC," +
					"nvl(round(sum(decode(t.CURRNO,'001',UpYearLendBalC,UpYearLendBalC*r.EXCHRATE*"+rate+"))/10000),0) as UpYearLendBalC, " +
					"nvl(round(sum(decode(t.CURRNO,'001',UpMonthLendBalC,UpMonthLendBalC*r.EXCHRATE*"+rate+"))/10000),0) as UpMonthLendBalC, " +
					"nvl(round(sum(decode(t.CURRNO,'001',UpTenDayLendBalC,UpTenDayLendBalC*r.EXCHRATE*"+rate+"))/10000),0) as UpTenDayLendBalC, " +
					"nvl(round(sum(decode(t.CURRNO,'001',UpYearSameLendBalC,UpYearSameLendBalC*r.EXCHRATE*"+rate+"))/10000),0) as UpYearSameLendBalC ";
	      	String Str32="select nvl(round(sum(decode(t.CURRNO,'001',LoanBal,LoanBal*r.EXCHRATE*"+rate+"))/10000),0) as LendBal," +
              "nvl(round(sum(decode(t.CURRNO,'001',UpDayLoanBalC,UpDayLoanBalC*r.EXCHRATE*"+rate+"))/10000),0) as UpDayLendBalC," +
             	"nvl(round(sum(decode(t.CURRNO,'001',UpYearLoanBalC,UpYearLoanBalC*r.EXCHRATE*"+rate+"))/10000),0) as UpYearLendBalC, " +
				"nvl(round(sum(decode(t.CURRNO,'001',UpMonthLoanBalC,UpMonthLoanBalC*r.EXCHRATE*"+rate+"))/10000),0) as UpMonthLendBalC, " +
				"nvl(round(sum(decode(t.CURRNO,'001',UpTenDayLoanBalC,UpTenDayLoanBalC*r.EXCHRATE*"+rate+"))/10000),0) as UpTenDayLendBalC, " +
				"nvl(round(sum(decode(t.CURRNO,'001',UpYearSameLoanBalC,UpYearSameLoanBalC*r.EXCHRATE*"+rate+"))/10000),0) as UpYearSameLendBalC ";
	      	if (inst_type.equals("1"))
	  		{
	  			if (curr_no.equals("001"))
	  			{
	  				if(dataFlag1.equals("2"))
	  					sqlStr =  Str11+
	  			                 "from T_TOT " +
	  							 "where  CURRNO='001'" +
	  								   " and SubjectNo='"+dataNo+"'"+
	  								   " and DATADATE ='"+reptdate+"'";
	  				else
	  					sqlStr =  Str12+
	                               "from T_TOT " +
	  			                 "where CURRNO='001'" +
	  			                         " and SubjectNo='"+dataNo+"'"+
	  			                         " and DATADATE ='"+reptdate+"'";
	  					
	  			}
	  			else if (curr_no.equals("000"))
	  			{
	  				if(dataFlag1.equals("2"))
	  				    sqlStr =  Str21+
	  			                 "from T_TOT t,T_RATE r " +
	  			                 "where t.CURRNO=r.CURRNO"+
	  							    	" and t.CURRNO<>001 " +
	  							    	" and r.MONTH=substr(t.DATADATE,0,6)"+
	  								    " and t.SubjectNo='"+dataNo+"'"+
	  								    " and t.DATADATE ='"+reptdate+"'";
	  				else
	  					sqlStr =  Str22+
	  					         "from T_TOT t,T_RATE r " +
	  					         "where t.CURRNO=r.CURRNO"+
	  									" and t.CURRNO<>001 " +
	  									" and r.MONTH=substr(t.DATADATE,0,6)"+
	  								    " and t.SubjectNo='"+dataNo+"'"+
	  								    " and t.DATADATE ='"+reptdate+"'";
	  					
	  			}
	  			else if (curr_no.equals("111"))
	  			{
	  				if(dataFlag1.equals("2"))
	  				    sqlStr =  Str31+
	  					          "from T_TOT t,T_RATE r " +
	  					         "where t.CURRNO=r.CURRNO " +
	  									" and r.MONTH=substr(DATADATE,0,6)"+
	  								    " and t.SubjectNo='"+dataNo+"'"+
	  								    " and t.DATADATE ='"+reptdate+"'";
	  				else
	  				    sqlStr =  Str32+
	  	                         "from T_TOT t,T_RATE r " +
	  					         "where t.CURRNO=r.CURRNO " +
	  								    " and r.MONTH=substr(DATADATE,0,6)"+
	  								    " and t.SubjectNo='"+dataNo+"'"+
	  								    " and t.DATADATE ='"+reptdate+"'";
	  					
	  			}
	  		}
	  		else if(inst_type.equals("2"))
	  		{
	  			if (curr_no.equals("001"))
	  			{
	  				if(dataFlag1.equals("2"))
	  					sqlStr = Str11 +
	  			                 "from T_TOT " +
	  							 "where CURRNO='001' " +
	  							       " and UPINST_NO='"+inst_no+"'"+
	  								   " and SubjectNo='"+dataNo+"'"+
	  								   " and DATADATE ='"+reptdate+"'";
	  				else
	  					sqlStr = Str12 +
	                               "from T_TOT " +
	  							 "where CURRNO='001' " +
	  						       " and UPINST_NO='"+inst_no+"'"+
	  							   " and SubjectNo='"+dataNo+"'"+
	  							   " and DATADATE ='"+reptdate+"'";
	  					
	  			}
	  			else if (curr_no.equals("000"))
	  			{
	  				if(dataFlag1.equals("2"))
	  				    sqlStr = Str21 +
	  			                 "from T_TOT t,T_RATE r " +
	  			                 "where t.CURRNO=r.CURRNO"+
	  								    " and t.CURRNO<>'001'" +
	  							        " and t.UPINST_NO='"+inst_no+"'"+
	  									" and t.SubjectNo='"+dataNo+"'"+
	  									" and r.MONTH=substr(t.DATADATE,0,6)"+
	  								    " and t.DATADATE ='"+reptdate+"'";
	  				else
	  					sqlStr = Str22 +
	  				             "from T_TOT t,T_RATE r " +
	  				             "where t.CURRNO=r.CURRNO"+
	  								    " and t.CURRNO<>'001'" +
	  							        " and t.UPINST_NO='"+inst_no+"'"+
	  									" and t.SubjectNo='"+dataNo+"'"+
	  									" and r.MONTH=substr(t.DATADATE,0,6)"+
	  								    " and t.DATADATE ='"+reptdate+"'";
	  					
	  			}
	  			else if (curr_no.equals("111"))
	  			{
	  				if(dataFlag1.equals("2"))
	  				    sqlStr = Str31 +
	  					         "from T_TOT t,T_RATE r " +
	  				             "where t.CURRNO=r.CURRNO"+
	  						            " and t.UPINST_NO='"+inst_no+"'"+
	  									" and t.SubjectNo='"+dataNo+"'"+
	  								    " and r.MONTH=substr(t.DATADATE,0,6)"+
	  							        " and t.DATADATE ='"+reptdate+"'";
	  				else
	  				    sqlStr = Str32 +
	  	                         "from T_TOT t,T_RATE r " +
	  				             "where t.CURRNO=r.CURRNO"+
	  					                " and t.UPINST_NO='"+inst_no+"'"+
	  									" and t.SubjectNo='"+dataNo+"'"+
	  							        " and r.MONTH=substr(t.DATADATE,0,6)"+
	  						            " and t.DATADATE ='"+reptdate+"'";
	  					
	  			}

	  		}
	  		else if(inst_type.equals("5"))
	  		{
	  			if (curr_no.equals("001"))
	  			{
	  				if(dataFlag1.equals("2"))
	  					sqlStr = Str11 +
	  			                 "from T_TOTAL t,T_INST_INFO i " +
	  							 "where t.Inst_No=i.Inst_No and CURRNO='001' " +
	  							       " and i.UpINST_NO='"+inst_no+"'"+
	  								   " and SubjectNo='"+dataNo+"'"+
	  								   " and DATADATE ='"+reptdate+"'";
	  				else
	  					sqlStr = Str12 +
	                               "from T_TOTAL t,T_INST_INFO i " +
	  							 "where t.Inst_No=i.Inst_No and CURRNO='001' " +
	  						       " and i.UPINST_NO='"+inst_no+"'"+
	  							   " and SubjectNo='"+dataNo+"'"+
	  							   " and DATADATE ='"+reptdate+"'";
	  					
	  			}
	  			else if (curr_no.equals("000"))
	  			{
	  				if(dataFlag1.equals("2"))
	  				    sqlStr = Str21 +
	  			                 "from T_TOT t,T_INST_INFO i,T_RATE r " +
	  			                 "where t.Inst_No=i.Inst_No and t.CURRNO=r.CURRNO"+
	  								    " and t.CURRNO<>'001'" +
	  							        " and i.UPINST_NO='"+inst_no+"'"+
	  									" and t.SubjectNo='"+dataNo+"'"+
	  									" and r.MONTH=substr(t.DATADATE,0,6)"+
	  								    " and t.DATADATE ='"+reptdate+"'";
	  				else
	  					sqlStr = Str22 +
	  				             "from T_TOTAL t,T_INST_INFO i,T_RATE r " +
	  				             "where t.Inst_No=i.Inst_No and t.CURRNO=r.CURRNO"+
	  								    " and t.CURRNO<>'001'" +
	  							        " and i.UPINST_NO='"+inst_no+"'"+
	  									" and t.SubjectNo='"+dataNo+"'"+
	  									" and r.MONTH=substr(t.DATADATE,0,6)"+
	  								    " and t.DATADATE ='"+reptdate+"'";
	  					
	  			}
	  			else if (curr_no.equals("111"))
	  			{
	  				if(dataFlag1.equals("2"))
	  				    sqlStr = Str31 +
	  					         "from T_TOTAL t,T_INST_INFO i,T_RATE r " +
	  				             "where t.Inst_No=i.Inst_No and t.CURRNO=r.CURRNO"+
	  						            " and i.UPINST_NO='"+inst_no+"'"+
	  									" and t.SubjectNo='"+dataNo+"'"+
	  								    " and r.MONTH=substr(t.DATADATE,0,6)"+
	  							        " and t.DATADATE ='"+reptdate+"'";
	  				else
	  				    sqlStr = Str32 +
	  	                         "from T_TOTAL t,T_INST_INFO i,T_RATE r " +
	  				             "where t.Inst_No=i.Inst_No and t.CURRNO=r.CURRNO"+
	  					                " and t.UPINST_NO='"+inst_no+"'"+
	  									" and t.SubjectNo='"+dataNo+"'"+
	  							        " and r.MONTH=substr(t.DATADATE,0,6)"+
	  						            " and t.DATADATE ='"+reptdate+"'";
	  					
	  			}

	  		}
	  		else
	  		{
	  			if (curr_no.equals("001"))
	  			{
	  				if(dataFlag1.equals("2"))
	  					sqlStr = Str11 +
	  			                 "from T_TOTAL " +
	  							 "where CURRNO='001' " +
	  							       " and INST_NO='"+net_no+"'"+
	  								   " and SubjectNo='"+dataNo+"'"+
	  								   " and DATADATE ='"+reptdate+"'";
	  				else
	  					sqlStr = Str12 +
	                               "from T_TOTAL " +
	  			                 "where CURRNO='001' " +
	  						             " and INST_NO='"+net_no+"'"+
	  									 " and SubjectNo='"+dataNo+"'"+
	  			                         " and DATADATE ='"+reptdate+"'";
	  					
	  			}
	  			else if (curr_no.equals("000"))
	  			{
	  				if(dataFlag1.equals("2"))
	  				    sqlStr = Str21 +
	  			                 "from T_TOTAL t,T_RATE r " +
	  			                 "where t.CURRNO=r.CURRNO"+
	  								    " and t.CURRNO<>'001'" +
	  							        " and t.INST_NO='"+net_no+"'"+
	  									" and t.SubjectNo='"+dataNo+"'"+
	  									" and r.MONTH=substr(t.DATADATE,0,6)"+
	  								    " and t.DATADATE ='"+reptdate+"'";
	  				else
	  					sqlStr = Str22 +
	  				             "from T_TOTAL t,T_RATE r " +
	  				             "where t.CURRNO=r.CURRNO"+
	  								    " and t.CURRNO<>'001'" +
	  							        " and t.INST_NO='"+net_no+"'"+
	  									" and t.SubjectNo='"+dataNo+"'"+
	  									" and r.MONTH=substr(t.DATADATE,0,6)"+
	  								    " and t.DATADATE ='"+reptdate+"'";
	  					
	  			}
	  			else if (curr_no.equals("111"))
	  			{
	  				if(dataFlag1.equals("2"))
	  				    sqlStr = Str31 +
	  					         "from T_TOTAL t,T_RATE r " +
	  				             "where t.CURRNO=r.CURRNO"+
	  						            " and t.INST_NO='"+net_no+"'"+
	  									" and t.SubjectNo='"+dataNo+"'"+
	  								    " and r.MONTH=substr(t.DATADATE,0,6)"+
	  							        " and t.DATADATE ='"+reptdate+"'";
	  				else
	  				    sqlStr = Str32 +
	  	                         "from T_TOTAL t,T_RATE r " +
	  				             "where t.CURRNO=r.CURRNO"+
	  					                " and t.INST_NO='"+net_no+"'"+
	  									" and t.SubjectNo='"+dataNo+"'"+
	  							        " and r.MONTH=substr(t.DATADATE,0,6)"+
	  						            " and t.DATADATE ='"+reptdate+"'";
	  					
	  			}


	  		}
	  		
	  		logger.debug(sqlStr);	
	  		
	  		return sqlStr;
	  	
	      }
	      
	      
	      public static String DBSql_2(String reptdate,String inst_type,String inst_no,String net_no,String curr_no,String dataFlag,String dataFlag1,String dataNo,double rate)
	      {
	      	String sqlStr = "";
	      	String Str11="select nvl(round(sum(LendBal)/10000),0) as LendBal," +
			"nvl(round(sum(UpDayLendBalC)/10000),0) as UpDayLendBalC," +
			"nvl(round(sum(UpYearLendBalC)/10000),0) as UpYearLendBalC, " +
			"nvl(round(sum(UpMonthLendBalC)/10000),0) as UpMonthLendBalC," +
			"nvl(round(sum(UpTenDayLendBalC)/10000),0) as UpTenDayLendBalC," +
			"nvl(round(sum(UpYearSameLendBalC)/10000),0) as UpYearSameLendBalC ";
      	String Str12="select nvl(round(sum(LoanBal)/10000),0) as LendBal," +
            "nvl(round(sum(UpDayLoanBalC)/10000),0) as UpDayLendBalC," +
            "nvl(round(sum(UpYearLoanBalC)/10000),0) as UpYearLendBalC, " +
			 "nvl(round(sum(UpMonthLoanBalC)/10000),0) as UpMonthLendBalC," +
			 "nvl(round(sum(UpTenDayLoanBalC)/10000),0) as UpTenDayLendBalC," +
			 "nvl(round(sum(UpYearSameLoanBalC)/10000),0) as UpYearSameLendBalC ";
      	String Str21="select nvl(round(sum(LendBal*r.EXCHRATE)/10000),0) as LendBal," +
			"nvl(round(sum(UpDayLendBalC*r.EXCHRATE)/10000),0) as UpDayLendBalC," +
			"nvl(round(sum(UpYearLendBalC*r.EXCHRATE)/10000),0) as UpYearLendBalC, " +
			"nvl(round(sum(UpMonthLendBalC*r.EXCHRATE)/10000),0) as UpMonthLendBalC," +
			"nvl(round(sum(UpTenDayLendBalC*r.EXCHRATE)/10000),0) as UpTenDayLendBalC," +
			"nvl(round(sum(UpYearSameLendBalC*r.EXCHRATE)/10000),0) as UpYearSameLendBalC ";
      	String Str22="select nvl(round(sum(LoanBal*r.EXCHRATE)/10000),0) as LendBal," +
		    "nvl(round(sum(UpDayLoanBalC*r.EXCHRATE)/10000),0) as UpDayLendBalC," +
			"nvl(round(sum(UpYearLoanBalC*r.EXCHRATE)/10000),0) as UpYearLendBalC, " +
			"nvl(round(sum(UpMonthLoanBalC*r.EXCHRATE)/10000),0) as UpMonthLendBalC," +
			"nvl(round(sum(UpTenDayLoanBalC*r.EXCHRATE)/10000),0) as UpTenDayLendBalC," +
			"nvl(round(sum(UpYearSameLoanBalC*r.EXCHRATE)/10000),0) as UpYearSameLendBalC ";
      	String Str31="select nvl(round(sum(decode(t.CURRNO,'001',LendBal,LendBal*r.EXCHRATE*"+rate+"))/10000),0) as LendBal," +
			"nvl(round(sum(decode(t.CURRNO,'001',UpDayLendBalC,UpDayLendBalC*r.EXCHRATE*"+rate+"))/10000),0) as UpDayLendBalC," +
				"nvl(round(sum(decode(t.CURRNO,'001',UpYearLendBalC,UpYearLendBalC*r.EXCHRATE*"+rate+"))/10000),0) as UpYearLendBalC, " +
				"nvl(round(sum(decode(t.CURRNO,'001',UpMonthLendBalC,UpMonthLendBalC*r.EXCHRATE*"+rate+"))/10000),0) as UpMonthLendBalC, " +
				"nvl(round(sum(decode(t.CURRNO,'001',UpTenDayLendBalC,UpTenDayLendBalC*r.EXCHRATE*"+rate+"))/10000),0) as UpTenDayLendBalC, " +
				"nvl(round(sum(decode(t.CURRNO,'001',UpYearSameLendBalC,UpYearSameLendBalC*r.EXCHRATE*"+rate+"))/10000),0) as UpYearSameLendBalC ";
      	String Str32="select nvl(round(sum(decode(t.CURRNO,'001',LoanBal,LoanBal*r.EXCHRATE*"+rate+"))/10000),0) as LendBal," +
          "nvl(round(sum(decode(t.CURRNO,'001',UpDayLoanBalC,UpDayLoanBalC*r.EXCHRATE*"+rate+"))/10000),0) as UpDayLendBalC," +
         	"nvl(round(sum(decode(t.CURRNO,'001',UpYearLoanBalC,UpYearLoanBalC*r.EXCHRATE*"+rate+"))/10000),0) as UpYearLendBalC, " +
			"nvl(round(sum(decode(t.CURRNO,'001',UpMonthLoanBalC,UpMonthLoanBalC*r.EXCHRATE*"+rate+"))/10000),0) as UpMonthLendBalC, " +
			"nvl(round(sum(decode(t.CURRNO,'001',UpTenDayLoanBalC,UpTenDayLoanBalC*r.EXCHRATE*"+rate+"))/10000),0) as UpTenDayLendBalC, " +
			"nvl(round(sum(decode(t.CURRNO,'001',UpYearSameLoanBalC,UpYearSameLoanBalC*r.EXCHRATE*"+rate+"))/10000),0) as UpYearSameLendBalC ";
	  		if (inst_type.equals("1"))
	  		{
	  			if (curr_no.equals("001"))
	  			{
	  				if(dataFlag1.equals("2"))
	  					sqlStr = Str11 +
	  			                 "from T_ITEM_DETAIL " +
	  							 "where Inst_Flag='2' and CURRNO='001'" +
	  								   " and ITEMNO="+dataNo+
	  								   " and DATADATE ='"+reptdate+"' ";
	  				else
	  					sqlStr = Str12 +
	                               "from T_ITEM_DETAIL " +
	  			                 "where Inst_Flag='2' and CURRNO='001'" +
	  			                         " and ITEMNO="+dataNo+
	  			                         " and DATADATE ='"+reptdate+"'";
	  					
	  			}
	  			else if (curr_no.equals("000"))
	  			{
	  				if(dataFlag1.equals("2"))
	  				    sqlStr = Str21 +
	  			                 "from T_ITEM_DETAIL t,T_RATE r " +
	  			                 "where t.CURRNO=r.CURRNO"+
	  							    	" and t.CURRNO<>001 " +
	  							        " and Inst_Flag='2'" +
	  							    	" and r.MONTH=substr(t.DATADATE,0,6)"+
	  								    " and t.ITEMNO="+dataNo+
	  								    " and t.DATADATE ='"+reptdate+"'";
	  				else
	  					sqlStr = Str22 +
	  				             "from T_ITEM_DETAIL t,T_RATE r " +
	  				             "where t.CURRNO=r.CURRNO"+
	  									" and t.CURRNO<>001 " +
	  								    " and Inst_Flag='2'" +
	  									" and r.MONTH=substr(t.DATADATE,0,6)"+
	  									" and t.ITEMNO="+dataNo+
	  									" and t.DATADATE ='"+reptdate+"'";
	  					
	  			}
	  			else if (curr_no.equals("111"))
	  			{
	  				if(dataFlag1.equals("2"))
	  				    sqlStr = Str31 +
	  					                 "from T_ITEM_DETAIL t,T_RATE r " +
	  					         "where t.CURRNO=r.CURRNO " +
	  									" and Inst_Flag='2'" +
	  									" and r.MONTH=substr(DATADATE,0,6)"+
	  									" and t.ITEMNO="+dataNo+
	  									" and DATADATE ='"+reptdate+"'";
	  				else
	  				    sqlStr = Str32 +
	  	                         "from T_ITEM_DETAIL t,T_RATE r " +
	  					         "where t.CURRNO=r.CURRNO " +
	  								    " and Inst_Flag='2'" +
	  								    " and r.MONTH=substr(DATADATE,0,6)"+
	  							    	" and t.ITEMNO="+dataNo+
	  								    " and DATADATE ='"+reptdate+"'";
	  					
	  			}
	  		}
	  		else if(inst_type.equals("2"))
	  		{
	  			if (curr_no.equals("001"))
	  			{
	  				if(dataFlag1.equals("2"))
	  					sqlStr = Str11 +
	  			                 "from T_ITEM_DETAIL " +
	  							 "where Inst_Flag='2' and CURRNO='001' " +
	  							       " and INST_NO='"+inst_no+"'"+
	  								   " and ITEMNO="+dataNo+
	  								   " and DATADATE ='"+reptdate+"'";
	  				else
	  					sqlStr = Str12 +
	                               "from T_ITEM_DETAIL " +
	  			                 "where Inst_Flag='2' and CURRNO='001' " +
	  						             " and INST_NO='"+inst_no+"'"+
	  			                         " and ITEMNO="+dataNo+
	  			                         " and DATADATE ='"+reptdate+"'";
	  					
	  			}
	  			else if (curr_no.equals("000"))
	  			{
	  				if(dataFlag1.equals("2"))
	  				    sqlStr = Str21 +
	  			                 "from T_ITEM_DETAIL t,T_RATE r " +
	  			                 "where t.CURRNO=r.CURRNO"+
	  								    " and t.CURRNO<>'001'" +
	  							        " and Inst_Flag='2'" +
	  							        " and t.INST_NO='"+inst_no+"'"+
	  								    " and t.ITEMNO="+dataNo+
	  									" and r.MONTH=substr(t.DATADATE,0,6)"+
	  								    " and t.DATADATE ='"+reptdate+"'";
	  				else
	  					sqlStr = Str22 +
	  				             "from T_ITEM_DETAIL t,T_RATE r " +
	  				             "where t.CURRNO=r.CURRNO"+
	  								    " and t.CURRNO<>'001'" +
	  							        " and Inst_Flag='2'" +
	  							        " and t.INST_NO='"+inst_no+"'"+
	  								    " and t.ITEMNO="+dataNo+
	  									" and r.MONTH=substr(t.DATADATE,0,6)"+
	  								    " and t.DATADATE ='"+reptdate+"'";
	  					
	  			}
	  			else if (curr_no.equals("111"))
	  			{
	  				if(dataFlag1.equals("2"))
	  				    sqlStr = Str31 +
	  					         "from T_ITEM_DETAIL t,T_RATE r " +
	  				             "where t.CURRNO=r.CURRNO"+
	  						            " and Inst_Flag='2'" +
	  						            " and t.INST_NO='"+inst_no+"'"+
	  							        " and t.ITEMNO="+dataNo+
	  								    " and r.MONTH=substr(t.DATADATE,0,6)"+
	  							        " and t.DATADATE ='"+reptdate+"'";
	  				else
	  				    sqlStr = Str32 +
	  	                         "from T_ITEM_DETAIL t,T_RATE r " +
	  				             "where t.CURRNO=r.CURRNO"+
	  					                " and Inst_Flag='2'" +
	  					                " and t.INST_NO='"+inst_no+"'"+
	  						            " and t.ITEMNO="+dataNo+
	  							        " and r.MONTH=substr(t.DATADATE,0,6)"+
	  						            " and t.DATADATE ='"+reptdate+"'";
	  					
	  			}

	  		}
	  		else if(inst_type.equals("5"))
	  		{
	  			if (curr_no.equals("001"))
	  			{
	  				if(dataFlag1.equals("2"))
	  					sqlStr = Str11 +
	  			                 "from T_ITEM_DETAIL t,T_INST_INFO i " +
	  							 "where t.Inst_No=i.Inst_No and Inst_Flag='3' and CURRNO='001' " +
	  							       " and i.UPINST_NO='"+inst_no+"'"+
	  								   " and ITEMNO="+dataNo+
	  								   " and DATADATE ='"+reptdate+"'";
	  				else
	  					sqlStr = Str12 +
	                               "from T_ITEM_DETAIL t,T_INST_INFO i " +
	  			                 "where t.Inst_No=i.Inst_No and Inst_Flag='3' and CURRNO='001' " +
	  						             " and i.UpINST_NO='"+inst_no+"'"+
	  			                         " and ITEMNO="+dataNo+
	  			                         " and DATADATE ='"+reptdate+"'";
	  					
	  			}
	  			else if (curr_no.equals("000"))
	  			{
	  				if(dataFlag1.equals("2"))
	  				    sqlStr = Str21 +
	  			                 "from T_ITEM_DETAIL t,T_INST_INFO i,T_RATE r " +
	  			                 "where t.Inst_No=i.Inst_No and t.CURRNO=r.CURRNO"+
	  								    " and t.CURRNO<>'001'" +
	  							        " and Inst_Flag='3'" +
	  							        " and i.UPINST_NO='"+inst_no+"'"+
	  								    " and t.ITEMNO="+dataNo+
	  									" and r.MONTH=substr(t.DATADATE,0,6)"+
	  								    " and t.DATADATE ='"+reptdate+"'";
	  				else
	  					sqlStr = Str22 +
	  				             "from T_ITEM_DETAIL t,T_INST_INFO i,T_RATE r " +
	  				             "where t.Inst_No=i.Inst_No and t.CURRNO=r.CURRNO"+
	  								    " and t.CURRNO<>'001'" +
	  							        " and Inst_Flag='3'" +
	  							        " and i.UPINST_NO='"+inst_no+"'"+
	  								    " and t.ITEMNO="+dataNo+
	  									" and r.MONTH=substr(t.DATADATE,0,6)"+
	  								    " and t.DATADATE ='"+reptdate+"'";
	  					
	  			}
	  			else if (curr_no.equals("111"))
	  			{
	  				if(dataFlag1.equals("2"))
	  				    sqlStr = Str31 +
	  					         "from T_ITEM_DETAIL t,T_INST_INFO i,T_RATE r " +
	  				             "where t.Inst_No=i.Inst_No and t.CURRNO=r.CURRNO"+
	  						            " and Inst_Flag='3'" +
	  						            " and i.UPINST_NO='"+inst_no+"'"+
	  							        " and t.ITEMNO="+dataNo+
	  								    " and r.MONTH=substr(t.DATADATE,0,6)"+
	  							        " and t.DATADATE ='"+reptdate+"'";
	  				else
	  				    sqlStr = Str32 +
	  	                         "from T_ITEM_DETAIL t,T_INST_INFO i,T_RATE r " +
	  				             "where t.Inst_No=i.Inst_No and t.CURRNO=r.CURRNO"+
	  					                " and Inst_Flag='3'" +
	  					                " and i.UPINST_NO='"+inst_no+"'"+
	  						            " and t.ITEMNO="+dataNo+
	  							        " and r.MONTH=substr(t.DATADATE,0,6)"+
	  						            " and t.DATADATE ='"+reptdate+"'";
	  					
	  			}

	  		}
	  		else
	  		{
	  			if (curr_no.equals("001"))
	  			{
	  				if(dataFlag1.equals("2"))
	  					sqlStr = Str11 +
	  			                 "from T_ITEM_DETAIL " +
	  							 "where CURRNO='001' and Inst_Flag='3'" +
	  							       " and INST_NO='"+net_no+"'"+
	  								   " and ITEMNO="+dataNo+
	  								   " and DATADATE ='"+reptdate+"'";
	  				else
	  					sqlStr = Str12 +
	                               "from T_ITEM_DETAIL " +
	  			                 "where CURRNO='001'  and Inst_Flag='3'" +
	  						             " and INST_NO='"+net_no+"'"+
	  			                         " and ITEMNO="+dataNo+
	  			                         " and DATADATE ='"+reptdate+"'";
	  					
	  			}
	  			else if (curr_no.equals("000"))
	  			{
	  				if(dataFlag1.equals("2"))
	  				    sqlStr = Str21 +
	  			                 "from T_ITEM_DETAIL t,T_RATE r " +
	  			                 "where t.CURRNO=r.CURRNO"+
	  								    " and t.CURRNO<>'001' and Inst_Flag='3'" +
	  							        " and t.INST_NO='"+net_no+"'"+
	  								    " and t.ITEMNO="+dataNo+
	  									" and r.MONTH=substr(t.DATADATE,0,6)"+
	  								    " and t.DATADATE ='"+reptdate+"'";
	  				else
	  					sqlStr = Str22 +
	  				             "from T_ITEM_DETAIL t,T_RATE r " +
	  				             "where t.CURRNO=r.CURRNO"+
	  								    " and t.CURRNO<>'001' and Inst_Flag='3'" +
	  							        " and t.INST_NO='"+net_no+"'"+
	  								    " and t.ITEMNO="+dataNo+
	  									" and r.MONTH=substr(t.DATADATE,0,6)"+
	  								    " and t.DATADATE ='"+reptdate+"'";
	  					
	  			}
	  			else if (curr_no.equals("111"))
	  			{
	  				if(dataFlag1.equals("2"))
	  				    sqlStr = Str31 +
	  					         "from T_ITEM_DETAIL t,T_RATE r " +
	  				             "where t.CURRNO=r.CURRNO and Inst_Flag='3'"+
	  						            " and t.INST_NO='"+net_no+"'"+
	  							        " and t.ITEMNO="+dataNo+
	  								    " and r.MONTH=substr(t.DATADATE,0,6)"+
	  							        " and t.DATADATE ='"+reptdate+"'";
	  				else
	  				    sqlStr = Str32 +
	  	                         "from T_ITEM_DETAIL t,T_RATE r " +
	  				             "where t.CURRNO=r.CURRNO and Inst_Flag='3'"+
	  					                " and t.INST_NO='"+net_no+"'"+
	  						            " and t.ITEMNO="+dataNo+
	  							        " and r.MONTH=substr(t.DATADATE,0,6)"+
	  						            " and t.DATADATE ='"+reptdate+"'";
	  					
	  			}


	  		}

	  		
	  		logger.debug(sqlStr);	
	  		
	  		return sqlStr;
	  	
	      }
	      
	      @SuppressWarnings("rawtypes")
	  	public static ArrayList getResult (String inst_type,String inst_no,String net_no,String item_no,String reptdate,double rate,String ym,String InfoStr,ConnectionFactory dbclass)
	      {     
	          ArrayList tmpv1 = null;     
	          
	      	char subjflag=' ';
	      	char kmflag=' ';
	      	char jdflag='J';
	      	int addflag=0;
	      	int endflag=0;
	      	int start=0;
	      	
	      	String subjno="";
	       	String RMBStr="";
	      	String USAStr="";
	      	String HJStr="";
	       	String RMBWhenStr="";
	      	String USAWhenStr="";
	      	String HJWhenStr="";
	      	String TableStr="";
	      	char tab='A';
	      	String fieldStr="";
	       	String dlm="";
	       	String tmpStr="";
	       	String WhereStr="";

	          for(int i=0;i<InfoStr.length();i++) 
	          {
	          	subjflag=InfoStr.charAt(i);
	         	
	          	if(subjflag!='M' && subjflag!='m') continue;
	          	jdflag='0';
	          	addflag=0;
	          	endflag=0;
	          	subjno="";
	          	kmflag=InfoStr.charAt(i-1);
	         	    int j=0;
	           	for(j=i+1;j<InfoStr.length() && endflag==0;j++)
	          	{
	          	   switch(InfoStr.charAt(j))
	                 {
	                     case 'A':
	                     case 'a':
	                     	         addflag=1; 
	                     	         break;
	                     case 'J':
	                     case 'j':
	                     	         jdflag='J';
	            	                 break;
	                     case 'D':
	                     case 'd':
	                   	         jdflag='D';
	  	                         break;
	                     case 'V':
	                     case 'v':
	                  	         jdflag='V';
	                               break;
	                     case 'W':
	                     case 'w':	
	            	                 jdflag='W';
	                               break;
	                     case '0':	
	                     case '1':	
	                     case '2':	
	                     case '3':	
	                     case '4':	
	                     case '5':	
	                     case '6':	
	                     case '7':	
	                     case '8':	
	                     case '9':
	                     	         subjno=subjno+InfoStr.charAt(j);
	                               break;
	                     default :
	                     	         endflag=j;
	                               break;
	                 }
	          	}
	           	if(endflag==0) endflag=j;
	            	tmpStr=InfoStr.substring(start,i-1);
	           	start=endflag;
	          	RMBStr=RMBStr+tmpStr+tab+".RMBBal";
	             	USAStr=USAStr+tmpStr+tab+".USABal";
	          	HJStr=HJStr+tmpStr+tab+".HJBal";
	              if((inst_type.equals("2") && inst_no.equals("") || 
	              	inst_type.equals("3") &&  net_no.equals("")	) &&  tab!='A')
	              {
	              	if( tab=='B')
	           	         WhereStr=WhereStr+"where "+(char)(tab-1)+".Inst_Name="+tab+".Inst_Name";
	              	 else
	          	         WhereStr=WhereStr+" and "+(char)(tab-1)+".Inst_Name="+tab+".Inst_Name";
	              }
	          	switch(jdflag)
	  			{
	          	   case 'J':
	          	   	         if(addflag==1)	fieldStr="UpYearLoanBalC";
	          	   	              else      fieldStr="LoanBal";
	          	   	         break;
	          	   case 'D':
	  	   	                     if(addflag==1)	fieldStr="UpYearLendBalC";
	  	   	                          else      fieldStr="LendBal";
	  	   	                     break;
	          	   case 'V':
	  	   	                     if(addflag==1)	fieldStr="(UpYearLoanBalC-UpYearLendBalC)";
	  	   	                          else     fieldStr="(LoanBal-LendBal)";
	  	            	         break;
	             	   case 'W':
	  	                     if(addflag==1)	fieldStr="(UpYearLendBalC-UpYearLoanBalC)";
	  	                          else     fieldStr="(LendBal-LoanBal)";
	          	         break;
	             	   default:
	                     if(addflag==1)	fieldStr="(UpYearLendBalC+UpYearLoanBalC)";
	                          else     fieldStr="(LendBal+LoanBal)";
	     	                 break;
	  			}
	          	if(kmflag=='k' || kmflag=='K')
	          	{
	                 if (inst_type.equals("1"))
	                 {
	                   	TableStr =TableStr+dlm+
	  			    	  "(select d.LInst_Name as Inst_Name"+
	  					     ",sum(decode(b.CurrNo,'001',b.Bal,0)) as RMBBal" +
	                           ",sum(decode(b.CurrNo,'001', 0,b.Bal*c.ExchRate)) as USABal" +
	                  	     ",sum(decode(b.CurrNo,'001',b.Bal,b.Bal*c.ExchRate*"+rate+")) as HJBal"+
	                        " from (select CurrNo,"+fieldStr+" as Bal from T_TOT where SubjectNo='"+subjno+"' and DataDate='" + reptdate+"' union all select '001',0 from dual) b,"+
	  				          "(select r.CurrNo,r.ExchRate from T_CURRENCY y,T_RATE r where y.CurrNo=r.CurrNo and r.Month='"+ym+"') c,"+
	  						  "(select LInst_Name from T_INST_INFO where Inst_No='0000') d "+
	                        " where  c.CurrNo=b.CurrNo  group by d.LInst_Name) "+tab;                      
	          	   }
	                 if (inst_type.equals("2"))
	                 {
	               	   TableStr =TableStr+dlm+
	  		    	     "(select b.LInst_Name as Inst_Name"+
	  				        ",sum(decode(b.CurrNo,'001',b.Bal,0)) as RMBBal" +
	                          ",sum(decode(b.CurrNo,'001', 0,b.Bal*c.ExchRate)) as USABal" +
	            	            ",sum(decode(b.CurrNo,'001',b.Bal,b.Bal*c.ExchRate*"+rate+")) as HJBal";
	               	   TableStr=TableStr+" from (select LInst_No,LInst_Name,CurrNo,"+fieldStr+" as Bal from T_TOT x,T_INST_INFO d where x.UpInst_No = d.Inst_No and x.SubjectNo='"+subjno+"' and x.DataDate='" + reptdate+"'";
	                     if(!inst_no.equals("")) TableStr = TableStr + " and x.UpInst_No = '" + inst_no+"'";
	                     TableStr =TableStr+" union all select LInst_No,LInst_Name,'001',0 from T_INST_INFO";
	                     if(!inst_no.equals("")) TableStr = TableStr + " where Inst_No = '" + inst_no+"'";
	                      else TableStr = TableStr + " where Inst_No = UpInst_No and Inst_No<>'0000'";
	                     TableStr =TableStr+") b,(select r.CurrNo,r.ExchRate from T_CURRENCY y,T_RATE r where y.CurrNo=r.CurrNo and r.Month='"+ym+"') c"+
	                       " where b.CurrNo = c.CurrNo";

	                     TableStr = TableStr + " group by b.LInst_No,b.LInst_Name) "+tab;
	                  }
	                 if (inst_type.equals("5"))
	                 {
	               	   TableStr =TableStr+dlm+
	  		    	     "(select b.LInst_Name as Inst_Name"+
	  				        ",sum(decode(b.CurrNo,'001',b.Bal,0)) as RMBBal" +
	                          ",sum(decode(b.CurrNo,'001', 0,b.Bal*c.ExchRate)) as USABal" +
	            	            ",sum(decode(b.CurrNo,'001',b.Bal,b.Bal*c.ExchRate*"+rate+")) as HJBal";
	               	   TableStr=TableStr+" from (select LInst_No,LInst_Name,CurrNo,"+fieldStr+" as Bal from T_TOTAL x,( select a.LInst_No,a.LInst_Name,b.Inst_No from T_INST_INFO a,T_INST_INFO b where a.Inst_No=b.UpInst_No and a.Inst_No<>'0000') d where x.Inst_No = d.Inst_No and x.SubjectNo='"+subjno+"' and x.DataDate='" + reptdate+"'";
	                     if(!inst_no.equals("")) TableStr = TableStr + " and d.UpInst_No = '" + inst_no+"'";
	                     TableStr =TableStr+" union all select LInst_No,LInst_Name,'001',0 from T_INST_INFO";
	                     if(!inst_no.equals("")) TableStr = TableStr + " where Inst_No = '" + inst_no+"'";
	                      else TableStr = TableStr + " where Inst_No = UpInst_No and Inst_No<>'0000'";
	                     TableStr =TableStr+") b,(select r.CurrNo,r.ExchRate from T_CURRENCY y,T_RATE r where y.CurrNo=r.CurrNo and r.Month='"+ym+"') c"+
	                       " where b.CurrNo = c.CurrNo";

	                     TableStr = TableStr + " group by b.LInst_No,b.LInst_Name) "+tab;
	                  }
	                  if (inst_type.equals("3"))
	                  {
	                	   TableStr =TableStr+dlm+
	  		    	     "(select b.LInst_Name as Inst_Name"+
	  				        ",sum(decode(b.CurrNo, '001',b.Bal,0)) as RMBBal" +
	                        ",sum(decode(b.CurrNo, '001', 0,b.Bal*c.ExchRate)) as USABal" +
	          	            ",sum(decode(b.CurrNo, '001',b.Bal,b.Bal*c.ExchRate*"+rate+")) as HJBal";
	             	       TableStr =TableStr+" from (select LInst_Name,CurrNo,"+fieldStr+" as Bal from T_TOTAL x,T_INST_INFO d where x.Inst_No = d.Inst_No and x.SubjectNo='"+subjno+"' and x.DataDate='" + reptdate+"'";
	                     if(!inst_no.equals("") && net_no.equals("")) TableStr = TableStr + " and x.UpInst_No = '" + inst_no+"'";  
	                     if(!net_no.equals("")) TableStr = TableStr + " and x.Inst_No = '" + net_no+"'";
	                     TableStr =TableStr+" union all select LInst_Name,'001',0 from T_INST_INFO";
	                     if(!inst_no.equals("") && net_no.equals("")) TableStr = TableStr + " where UpInst_No = '" + inst_no+"'";  
	                       else if(!net_no.equals("")) TableStr = TableStr + " where Inst_No = '" + net_no+"'";
	                               else TableStr = TableStr + " where Inst_No<>'0000'";
	                     TableStr =TableStr+") b,(select r.CurrNo,r.ExchRate from T_CURRENCY y,T_RATE r where y.CurrNo=r.CurrNo and r.Month='"+ym+"') c"+
	                     " where b.CurrNo = c.CurrNo";

	                     TableStr = TableStr + " group by b.LInst_Name) "+tab;
	                  }
	          	 }
	          	 else
	          	 {
	                  if (inst_type.equals("1"))
	                  {
	                    	TableStr =TableStr+dlm+
	  				    	  "(select d.LInst_Name as Inst_Name"+
	  						     ",sum(decode(b.CurrNo, '001',b.Bal,0)) as RMBBal" +
	                            ",sum(decode(b.CurrNo, '001', 0,b.Bal*c.ExchRate)) as USABal" +
	                   	     ",sum(decode(b.CurrNo, '001',b.Bal,b.Bal*c.ExchRate*"+rate+")) as HJBal"+
	                         " from (select CurrNo,"+fieldStr+" as Bal from T_ITEM_DETAIL where ItemNo="+subjno+" and DataDate='" + reptdate+"'  and Inst_Flag='2' union all select '001',0 from dual) b,"+
	  					          "(select r.CurrNo,r.ExchRate from T_CURRENCY y,T_RATE r where y.CurrNo=r.CurrNo and r.Month='"+ym+"') c,"+
	  							  "(select LInst_Name from T_INST_INFO where Inst_No='0000') d "+
	                         " where  c.CurrNo=b.CurrNo  group by d.LInst_Name) "+tab;                      
	           	    }
	                  if (inst_type.equals("2"))
	                  {
	                	   TableStr =TableStr+dlm+
	  			    	     "(select b.LInst_Name as Inst_Name"+
	  					        ",sum(decode(b.CurrNo, '001',b.Bal,0)) as RMBBal" +
	                           ",sum(decode(b.CurrNo, '001', 0,b.Bal*c.ExchRate)) as USABal" +
	             	            ",sum(decode(b.CurrNo, '001',b.Bal,b.Bal*c.ExchRate*"+rate+")) as HJBal";
	                	   TableStr =TableStr+" from (select LInst_No,LInst_Name,CurrNo,"+fieldStr+" as Bal from T_ITEM_DETAIL x,T_INST_INFO d where x.Inst_No = d.Inst_No and x.Inst_Flag='2' and x.ItemNo="+subjno+" and x.DataDate='" + reptdate+"'";
	                      if(!inst_no.equals("")) TableStr = TableStr + " and d.Inst_No = '" + inst_no+"'";
	                      TableStr =TableStr+" union all select LInst_No,LInst_Name,'001',0 from T_INST_INFO";
	                      if(!inst_no.equals("")) TableStr = TableStr + " where Inst_No = '" + inst_no+"'";
	                       else TableStr = TableStr + " where Inst_No = UpInst_No and Inst_No<>'0000'";
	                      TableStr =TableStr+") b,(select r.CurrNo,r.ExchRate from T_CURRENCY y,T_RATE r where y.CurrNo=r.CurrNo and r.Month='"+ym+"') c"+
	                        " where b.CurrNo = c.CurrNo";

	                      TableStr = TableStr + " group by b.LInst_Name) "+tab;
	                   }
	                  if (inst_type.equals("5"))
	                  {
	                	   TableStr =TableStr+dlm+
	  			    	     "(select b.LInst_Name as Inst_Name"+
	  					        ",sum(decode(b.CurrNo, '001',b.Bal,0)) as RMBBal" +
	                           ",sum(decode(b.CurrNo, '001', 0,b.Bal*c.ExchRate)) as USABal" +
	             	            ",sum(decode(b.CurrNo, '001',b.Bal,b.Bal*c.ExchRate*"+rate+")) as HJBal";
	                	   TableStr =TableStr+" from (select LInst_No,LInst_Name,CurrNo,"+fieldStr+" as Bal from T_ITEM_DETAIL x,( select a.LInst_No,a.LInst_Name,b.Inst_No from T_INST_INFO a,T_INST_INFO b where a.Inst_No=b.UpInst_No and a.Inst_No<>'0000') d where x.Inst_No = d.Inst_No and x.Inst_Flag='3' and x.ItemNo="+subjno+" and x.DataDate='" + reptdate+"'";
	                      if(!inst_no.equals("")) TableStr = TableStr + " and d.Inst_No = '" + inst_no+"'";
	                      TableStr =TableStr+" union all select LInst_No,LInst_Name,'001',0 from T_INST_INFO";
	                      if(!inst_no.equals("")) TableStr = TableStr + " where Inst_No = '" + inst_no+"'";
	                       else TableStr = TableStr + " where Inst_No = UpInst_No and Inst_No<>'0000'";
	                      TableStr =TableStr+") b,(select r.CurrNo,r.ExchRate from T_CURRENCY y,T_RATE r where y.CurrNo=r.CurrNo and r.Month='"+ym+"') c"+
	                        " where b.CurrNo = c.CurrNo";

	                      TableStr = TableStr + " group by b.LInst_Name) "+tab;
	                   }
	                  if (inst_type.equals("3"))
	                  {
	                	   TableStr =TableStr+dlm+
	  		    	     "(select b.LInst_Name as Inst_Name"+
	  				        ",sum(decode(b.CurrNo, '001',b.Bal,0)) as RMBBal" +
	                        ",sum(decode(b.CurrNo, '001', 0,b.Bal*c.ExchRate)) as USABal" +
	          	            ",sum(decode(b.CurrNo, '001',b.Bal,b.Bal*c.ExchRate*"+rate+")) as HJBal";
	             	       TableStr =TableStr+" from (select LInst_Name,CurrNo,"+fieldStr+" as Bal from T_ITEM_DETAIL x,T_INST_INFO d where x.Inst_No = d.Inst_No and x.Inst_Flag='3' and x.ItemNo="+subjno+" and x.DataDate='" + reptdate+"'";
	                     if(!inst_no.equals("") && net_no.equals("")) TableStr = TableStr + " and i.UpInst_No = '" + inst_no+"'";  
	                     if(!net_no.equals("")) TableStr = TableStr + " and i.Inst_No = '" + net_no+"'";
	                     TableStr =TableStr+" union all select LInst_Name,'001',0 from T_INST_INFO";
	                     if(!inst_no.equals("") && net_no.equals("")) TableStr = TableStr + " where UpInst_No = '" + inst_no+"'";  
	                       else if(!net_no.equals("")) TableStr = TableStr + " where Inst_No = '" + net_no+"'";
	                               else TableStr = TableStr + " where Inst_No<>'0000'";
	                     TableStr =TableStr+") b,(select r.CurrNo,r.ExchRate from T_CURRENCY y,T_RATE r where y.CurrNo=r.CurrNo and r.Month='"+ym+"') c"+
	                     " where b.CurrNo = c.CurrNo";

	                     TableStr = TableStr + " group by b.LInst_Name) "+tab;
	                  }
	          	 }
	              tab+=1;
	              dlm=",";
	              i=start;
	            }    
	      	tmpStr=InfoStr.substring(start);
	      	RMBStr=RMBStr+tmpStr;
	      	logger.info("RMBStr = "+RMBStr);//---------------------------
	      	USAStr=USAStr+tmpStr;
	      	logger.info("USAStr = "+USAStr);//---------------------------
	      	HJStr=HJStr+tmpStr;
	      	logger.info("HJStr = "+HJStr);//---------------------------
	             String [] ColList = RMBStr.split("/");
	          for(int i=1;i < ColList.length;i++)
	          {
	          	if(RMBWhenStr.equals(""))
	                  RMBWhenStr=GetWenStr(ColList[i].toString());
	          	 else
	                  RMBWhenStr=RMBWhenStr+"*"+GetWenStr(ColList[i].toString());
	          }	 
	            
	          USAWhenStr=RMBWhenStr;
	          USAWhenStr=USAWhenStr.replaceAll("RMB","USA");
	          HJWhenStr=RMBWhenStr;
	          HJWhenStr=HJWhenStr.replaceAll("RMB","HJ");
	         	if(RMBWhenStr.equals(""))
	              tmpStr="select A.Inst_Name as Inst_Name,"+RMBStr+" as RMBBal,"+" "+USAStr+" as USABal,"+" "+HJStr+" as HJBal "+
	  		                " from "+TableStr+" "+WhereStr; 
	         	  else
	              tmpStr="select A.Inst_Name as Inst_Name,decode("+RMBWhenStr+",0.0,0,"+RMBStr+") as RMBBal,decode("+USAWhenStr+",0.0,0,"+USAStr+") as USABal,decode("+HJWhenStr+",0.0,0,"+HJStr+") as HJBal "+
	              " from "+TableStr+" "+WhereStr; 
	          
	          logger.info("[queryStr] = [" + tmpStr + "]");
	       
	          //��Ҫ���ص��� 
	           
	          tmpv1 = dbclass.doQuery(tmpStr,-1,-1);
	                         
	          logger.debug("[��ѯ��� ] = [" + tmpv1.size() + "]");
	          //logger.debug("[��� ] = [" + tmpv + "]");
	          //logger.debug("��ѯ����");
	          
	          return tmpv1;
	          
	      }
	      /**	����������һ�������
	  	 * 
	  	 * @param day
	  	 * @return �������һ�������
	  	 */    
	   static public String getPreviousMonthEnd(String day) {
	  		Calendar tempCalendar = Calendar.getInstance();
	  		tempCalendar.set(Integer.parseInt(day.substring(0, 4)), Integer.parseInt(day.substring(4, 6)) - 1, Integer.parseInt(day.substring(6, 8))); // �����·�Ҫ��1   

	  		tempCalendar.add(Calendar.MONTH, -1);//��һ����    
	  		tempCalendar.set(Calendar.DATE, 1);//����������Ϊ���µ�һ��     
	  		tempCalendar.roll(Calendar.DATE, -1);//���ڻع�һ�죬Ҳ���Ǳ������һ��     

	  		java.text.SimpleDateFormat formater = new java.text.SimpleDateFormat("yyyyMMdd");

	  		return formater.format(tempCalendar.getTime());
	  	}   
	  	
	  	/**	��ñ������һ�������
	  	 * 
	  	 * @author tys
	  	 * @param day
	  	 * @return �������һ�������
	  	 */    
	   static public String getThisMonthEnd(String day) {
	  		Calendar tempCalendar = Calendar.getInstance();
	  		tempCalendar.set(Integer.parseInt(day.substring(0, 4)), Integer.parseInt(day.substring(4, 6)) - 1, Integer.parseInt(day.substring(6, 8))); // �����·�Ҫ��1   

	  		//tempCalendar.add(Calendar.MONTH, 1);//��һ����   
	  		tempCalendar.set(Calendar.DATE, 1);//����������Ϊ���µ�һ��     
	  		tempCalendar.roll(Calendar.DATE, -1);//���ڻع�һ�죬Ҳ���Ǳ������һ��     
	       
	  		java.text.SimpleDateFormat formater = new java.text.SimpleDateFormat("yyyyMMdd");

	  		return formater.format(tempCalendar.getTime());
	  	}    

	    
	  /**
	   * Method:��ȡ����ĩ����()
	   * @param:str
	   * @return:����12��31��
	   * */ 
	   
	   static public String getLastYear(String str){
	   	 
	   	
	   	String resultStr="";
	   	String year=str.substring(0,4);
	   	String month="12";
	   	String day="31";
	   	year=Integer.toString(Integer.parseInt(year)-1);
	   	 
	   	resultStr=year+month+day;
	   	
	   	
	   	return resultStr;
	   } 
	   
	   
	   /**
	    * ��ȡ�������ڵ�����
	    * 
	    * @param day
	    * @return
	    * 
	    */    
	   static public String getPreviousYear(String day) {
	  		Calendar tempCalendar = Calendar.getInstance();
	  		tempCalendar.set(Integer.parseInt(day.substring(0, 4)), Integer.parseInt(day.substring(4, 6)) - 1, Integer.parseInt(day.substring(6, 8))); // �����·�Ҫ��1   
	       
	  		tempCalendar.add(Calendar.YEAR, -1);//��һ��
	          
	  		java.text.SimpleDateFormat formater = new java.text.SimpleDateFormat("yyyyMMdd");

	  		return formater.format(tempCalendar.getTime());
	  	}
	  	
	  	
	      
}