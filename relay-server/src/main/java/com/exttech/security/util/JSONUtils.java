//package com.exttech.security.util;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import play.Logger;
//import play.mvc.Http.Header;
//
//public class JSONUtils {
//
//	private JSONUtils(){}
//	
//	public static JSONObject buildRequestHeaders(HttpServletRequest request) throws JSONException{
//        JSONObject jo = new JSONObject();
//
//		if(headers instanceof Map<?,?>){
//	        for(Header h:((Map<String,Header>)headers).values()){
//	        	jo.put(h.name, h.values);
//	        }
//		}else if(headers instanceof List<?>){
//			Map<String,List<String>> hMap = new HashMap();
//			for(Map.Entry h:((List<Map.Entry<String, String>>)headers)){
//				String key = ""+h.getKey();
//				String value = ""+h.getValue();
//
////				if("content-length".equalsIgnoreCase(""+key) && task !=null){
////					try{
////					task.setRequestContentLength(Long.parseLong(value));
////					}catch(Exception e){
////						if(Logger.isDebugEnabled()){
////							Logger.debug("[JSONUtils] Parse Content-Length failed!");
////							e.printStackTrace();
////						}
////					}
////				}
//	        	if(hMap.containsKey(key)){
//	        		hMap.get(key).add(value);
//	        	}else{
//	        		List<String> values = new ArrayList();
//	        		values.add(value);
//	        		hMap.put(key, values);
//	        	}
//	        }
//			
//			for(String key:hMap.keySet()){
//	        	jo.put(key, hMap.get(key));
//			}
//		}
//        return jo;
//	}
//	
//	
//	public static JSONObject buildResponseHeaders(Object headers) throws JSONException{
//        JSONObject jo = new JSONObject();
//
//		if(headers instanceof Map<?,?>){
//	        for(Header h:((Map<String,Header>)headers).values()){
//	        	jo.put(h.name, h.values);
//	        }
//		}else if(headers instanceof List<?>){
//			Map<String,List<String>> hMap = new HashMap();
//			for(Map.Entry h:((List<Map.Entry<String, String>>)headers)){
//				String key = ""+h.getKey();
//				String value = ""+h.getValue();
//
//				// if("content-length".equalsIgnoreCase(""+key) && task !=null){
//				// try{
//				// task.setResponseContentLength(Long.parseLong(value));
//				// }catch(Exception e){
//				// if(Logger.isDebugEnabled()){
//				// Logger.debug("[JSONUtils] Parse Content-Length failed!");
//				// e.printStackTrace();
//				// }
//				// }
//				// }
//	        	if(hMap.containsKey(key)){
//	        		hMap.get(key).add(value);
//	        	}else{
//	        		List<String> values = new ArrayList();
//	        		values.add(value);
//	        		hMap.put(key, values);
//	        	}
//	        }
//			
//			for(String key:hMap.keySet()){
//	        	jo.put(key, hMap.get(key));
//			}
//		}
//        return jo;
//	}
//}
