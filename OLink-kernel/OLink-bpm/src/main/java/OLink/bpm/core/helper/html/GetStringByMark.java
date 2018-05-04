package OLink.bpm.core.helper.html;
/**
 * 
 * @author aiming 2010-12-15
 * 该类处理了字符串的截取，以源字符串为操作对象，通过指定开始和结束字符串截取其中间字符串
 *
 */
public class GetStringByMark {

	public static String getStr(String str, String beginTib,String endTib){
		
		char[] fromStr = str.toCharArray();
		char[] fromBeginTib = beginTib.toCharArray();
		char[] fromEndTib = endTib.toCharArray();
		
		//是否开始截取所需信息
		boolean condition = false;
		//是否需要循环寻找开始标记
		boolean ifRun = true;
		//目标字符串
		StringBuffer targeStr = new StringBuffer();
		
		//1、在开始和结束标记的长度之和大于所需截取的字符串时才允许执行截取操作  2、在开始标记不为空的情况下
		if(fromStr.length >= (fromBeginTib.length + fromEndTib.length) && beginTib.length() != 0){
			
			//遍历字符串所有字符，一一和所需截取的开始标记首字符比较，如果存在与首字符匹配的字符，则进一步比较
			for(int i = 0; i < fromStr.length; i++){
				
				if(ifRun && fromStr[i] == fromBeginTib[0]){
					//此循环只需执行一次正确的匹配即可，故而添加ifRun判断，节省不必要的循环
					for(int j = 1; j < beginTib.length(); j++){
						if((i+j+1) > fromStr.length)
						{
							//预防索引溢出
							break;
						}
						else if(fromStr[i+j] != fromBeginTib[j]){
							//System.out.println(fromStr[i+j] + "  " + "***  " + fromBeginTib[j] + j);
							break;
						}
						else{
							//System.out.println(j);
							/**
							 * 通过比对，程序执行到此，如果条件成立，说明找到开始标记了此时已经是循环的结束了。故需要告诉程序
							 * 可以开始截取字符串了，并在此之后无需重新寻找开始标记的循环
							 **/
							
							if(j == (beginTib.length()-1)){
								condition = true;
								ifRun = false;
							}
						}
					}
				}
				
				//判断是否开始截取字符串
				if(condition){
					//如果当前的字符串长度加上开始标记的字符串长度相等，则表示开始标记位于字符串的末端，不需要继续往后截取内容
					if(i+beginTib.length() == fromStr.length)
					{
						break;
					}
					targeStr.append(fromStr[i+beginTib.length()]); 
				}
			}
			
			
			
			//程序执行到此处，说明目标字符串出现了！情况有2
			/**
			 * 1、目标字符串为空
			 * 2、目标字符串不为空
			 */
			if(targeStr.length() != 0)
			{
				//重置ifRun,和condition
				ifRun = true;
				condition = false;
				fromStr = targeStr.toString().toCharArray();
				
				//遍历字符串所有字符，一一和所需截取的结束标记首字符比较，如果存在与首字符匹配的字符，则进一步比较
				for(int i = 0; i < fromStr.length; i++){
					
					if(ifRun && fromStr[i] == fromEndTib[0]){
						//此循环只需执行一次正确的匹配即可，故而添加ifRun判断，节省不必要的循环
						for(int j = 1; j < endTib.length(); j++){
							if((i+j+1) > fromStr.length)
							{
								//预防索引溢出
								break;
							}
							else if(fromStr[i+j] != fromEndTib[j]){
								//System.out.println(fromStr[i+j] + "  " + "***  " + fromBeginTib[j] + j);
								break;
							}
							else{
								//System.out.println(j);
								/**
								 * 通过比对，程序执行到此，如果条件成立，说明找到结束标记了此时已经是循环的结束了。故需要告诉程序
								 * 可以开始截取字符串了，并在此之后无需重新寻找结束标记的循环
								 **/
								
								if(j == (beginTib.length()-1)){
									condition = true;
									ifRun = false;
								}
							}
						}
					}
					
					//判断是否开始截取字符串
					if(condition){
						//此处只需执行一次
						//System.out.println(i);
						String temp =targeStr.substring(0, i);
						targeStr.setLength(0);
						targeStr.append(temp);
						break;
					}
				}
			}
			else{
				return targeStr.toString();
			}
			
		}
		return targeStr.toString();
	}
	
	
	
//	public static void main(String args[]){
//		String str = "sssss";
//		if(getStr(str,"###","###").length() != 0)
//		{
//			System.out.println("根据条件截取到的字符串为：" +getStr(str,"###","###"));
//		}else
//		{
//			System.out.println("null");
//		}
//		
//	}
	
}
