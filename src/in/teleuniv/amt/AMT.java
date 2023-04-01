package in.teleuniv.amt;

import java.io.BufferedInputStream;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.FileSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringJoiner;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URI;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import com.sun.jna.platform.mac.SystemB.Statfs;


import oshi.SystemInfo;
import oshi.driver.windows.wmi.Win32ComputerSystem.ComputerSystemProperty;
import oshi.hardware.CentralProcessor;
import oshi.hardware.ComputerSystem;
import oshi.hardware.Display;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.PhysicalMemory;
import oshi.hardware.UsbDevice;
import oshi.hardware.platform.windows.WindowsHWDiskStore;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

import javax.usb.*;
import javax.usb.util.*;
import purejavahidapi.*;
import org.json.simple.JSONObject;   
//import static org.hamcrest.Matchers.notNullValue;

import java.io.*;



public class AMT{
	String status="on";
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws UnsupportedEncodingException, IOException, InterruptedException {
		JSONObject output=new JSONObject();    
 
		output.put("action", "sendmessage");
		output.put("Mouse", "on");
		SystemInfo si = new SystemInfo();
		HardwareAbstractionLayer hal = si.getHardware();
		OperatingSystem os= si.getOperatingSystem();	  
		
		//System.out.println(output);		
		AMT amt=new AMT();
		
		 try {
	            // open websocket
	            final WebsocketClientEndpoint clientEndPoint = new WebsocketClientEndpoint(new URI("wss://0hqif30xkl.execute-api.us-west-2.amazonaws.com/production"));

	            // add listener
	            clientEndPoint.addMessageHandler(new WebsocketClientEndpoint.MessageHandler() {
	                public void handleMessage(String message) {
	                	amt.status=message;
	                    System.out.println(amt.status);
	                    if(amt.status.equals("off")){
	                     	Runtime runtime = Runtime.getRuntime();
	                     	System.out.println("Shutting down the PC after 5 seconds.");
	            			try {
	            				if(getOSInfo(os).equals("Windows")) {
	        	        			
	        	        			output.put("Monitor",getMonitorInformation(hal));
	        	        			
	        	        		}		
	        	        		else
	        	        		{
	        	        			//code for linux monitor
	        	        			String cmd = "/usr/sbin/hwinfo  --monitor";
	        	        			Runtime run = Runtime.getRuntime();
	        	        			Process pr = run.exec(cmd);
	        	        			try {
										pr.waitFor();
									} catch (InterruptedException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
	        	        			BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
	        	        			String line = "";
	        	        			StringJoiner s = new StringJoiner("\n");
	        	        			while ((line=buf.readLine())!=null) {
	        	        				   //StringeJoiner object  
	        	        		        s.add(line);    //String 1  	        
	        	        		        
	        	        			}
	        	        			line=s.toString();
	        	        			output.put("Monitor",getMonitorLinuxInformation(line)); 
	        	        			
	        	        			//for linux mouse
	        	        			String cmd1 = "/usr/sbin/hwinfo  --mouse";			
	        	        			Process pr1 = run.exec(cmd1);
	        	        			try {
										pr1.waitFor();
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
	        	        			BufferedReader buf1 = new BufferedReader(new InputStreamReader(pr1.getInputStream()));
	        	        			String line1 = "";
	        	        			StringJoiner s1= new StringJoiner("\n");
	        	        			while ((line1=buf1.readLine())!=null) {
	        	        				   //StringeJoiner object  
	        	        		        s1.add(line1);    //String 1  	        
	        	        		        
	        	        			}
	        	        			line1=s1.toString();
	        	        			output.put("Mouse",getMouseLinuxInformation(line1));
	        	        			
	        	        			//for linux mouse
	        	        			String cmd2 = "/usr/sbin/hwinfo  --keyboard";			
	        	        			Process pr2 = run.exec(cmd2);
	        	        			try {
										pr1.waitFor();
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
	        	        			BufferedReader buf2 = new BufferedReader(new InputStreamReader(pr2.getInputStream()));
	        	        			String line2 = "";
	        	        			StringJoiner s2= new StringJoiner("\n");
	        	        			while ((line2=buf2.readLine())!=null) {
	        	        				   //StringeJoiner object  
	        	        		        s2.add(line2);    //String 1  	        
	        	        		        
	        	        			}
	        	        			line2=s2.toString();
	        	        			output.put("Keyboard",getKeyboardLinuxInformation(line2));
	        	        			
	        	        			
	        	        		}
	        	        	
	        	        		
	        	        		output.put("HDD",getHDDInformation(hal));
	        	        		output.put("RAM",getRAMInformation(hal));
	        	        		output.put("CPU",getCPUInformation(hal));
	        	        		output.put("System",getSysInformation(hal));
	        	        		output.put("MAC",hal.getNetworkIFs(false).get(0).getMacaddr());
	        	        		try {
	        	        			output.put("IPv4",hal.getNetworkIFs(false).get(0).getIPv4addr()[0]);
	        	        		}
	        	        		catch(Exception e)
	        	        			{}
	        	        		output.put("Keyboard", getKeyboardInformation(hal));
	        	        		
	        	        		output.put("OS", getOSInfo(os));
	        	        		output.put("SystemStatus", "off");
	        		            System.out.println(output.toString());
	        	            	clientEndPoint.sendMessage(output.toString());
	        	            	if(getOSInfo(os).equals("Ubuntu")) {
	        	            		runtime.exec("shutdown -P now");
	        	            	}
	        	            	else {
	        	            		runtime.exec("shutdown -s -t 5");
	        	            	}
	        	            	System.exit(0);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	                     }
	                }
	            });
	            while(true) {
		            // send message to websocket
	            	if(getOSInfo(os).equals("Windows")) {
	        			
	        			output.put("Monitor",getMonitorInformation(hal));
	        			
	        		}		
	        		else
	        		{
	        			//code for linux monitor
	        			String cmd = "/usr/sbin/hwinfo  --monitor";
	        			Runtime run = Runtime.getRuntime();
	        			Process pr = run.exec(cmd);
	        			pr.waitFor();
	        			BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
	        			String line = "";
	        			StringJoiner s = new StringJoiner("\n");
	        			while ((line=buf.readLine())!=null) {
	        				   //StringeJoiner object  
	        		        s.add(line);    //String 1  	        
	        		        
	        			}
	        			line=s.toString();
	        			output.put("Monitor",getMonitorLinuxInformation(line)); 
	        			
	        			//for linux mouse
	        			String cmd1 = "/usr/sbin/hwinfo  --mouse";			
	        			Process pr1 = run.exec(cmd1);
	        			pr1.waitFor();
	        			BufferedReader buf1 = new BufferedReader(new InputStreamReader(pr1.getInputStream()));
	        			String line1 = "";
	        			StringJoiner s1= new StringJoiner("\n");
	        			while ((line1=buf1.readLine())!=null) {
	        				   //StringeJoiner object  
	        		        s1.add(line1);    //String 1  	        
	        		        
	        			}
	        			line1=s1.toString();
	        			output.put("Mouse",getMouseLinuxInformation(line1));
	        			
	        			//for linux mouse
	        			String cmd2 = "/usr/sbin/hwinfo  --keyboard";			
	        			Process pr2 = run.exec(cmd2);
	        			pr1.waitFor();
	        			BufferedReader buf2 = new BufferedReader(new InputStreamReader(pr2.getInputStream()));
	        			String line2 = "";
	        			StringJoiner s2= new StringJoiner("\n");
	        			while ((line2=buf2.readLine())!=null) {
	        				   //StringeJoiner object  
	        		        s2.add(line2);    //String 1  	        
	        		        
	        			}
	        			line2=s2.toString();
	        			output.put("Keyboard",getKeyboardLinuxInformation(line2));
	        			
	        			
	        		}
	        	
	        		
	        		output.put("HDD",getHDDInformation(hal));
	        		output.put("RAM",getRAMInformation(hal));
	        		output.put("Keyboard", getKeyboardInformation(hal));
	        		output.put("CPU",getCPUInformation(hal));
	        		output.put("System",getSysInformation(hal));
	        		output.put("MAC",hal.getNetworkIFs(false).get(0).getMacaddr());
	        		try {
	        			output.put("IPv4",hal.getNetworkIFs(false).get(0).getIPv4addr()[0]);
	        		}
	        		catch(Exception e)
	        			{}
	        		//output.put("Keyboard", getKeyboardInformation(hal));
	        		output.put("OS", getOSInfo(os));
	        		output.put("SystemStatus", "on");
		            System.out.println(output.toString());
	            	clientEndPoint.sendMessage(output.toString());
	            	Thread.sleep(60000);
	            }
	            
	            
	            // wait 5 seconds for messages from websocket
	            //Thread.sleep(600000);
	            
	            

	        } catch (InterruptedException ex) {
	            System.err.println("InterruptedException exception: " + ex.getMessage());
	        } catch (URISyntaxException ex) {
	            System.err.println("URISyntaxException exception: " + ex.getMessage());
	        } catch(Exception e) {
	        	System.err.println("Exception: " + e.getMessage());
	        }
		 
		 
	}
	    
//		End of Push code
	
	public static void downloadFile(URL url, String fileName) throws IOException {
        try (InputStream in = url.openStream();
                BufferedInputStream bis = new BufferedInputStream(in);
                FileOutputStream fos = new FileOutputStream(fileName)) {
 
            byte[] data = new byte[1024];
            int count;
            while ((count = bis.read(data, 0, 1024)) != -1) {
                fos.write(data, 0, count);
            }
        }
    }

	public static String getOSInfo(OperatingSystem hal)
	{
		try
		{
//		 oshi.software.os.FileSystem compList = hal.get();
//		 for ( List comp : compList) {
//	           System.out.println(comp);
//	        }
//		 System.out.println(hal.getFamily());
//		 System.out.println(hal.getManufacturer());
//		 System.out.println(hal.getVersionInfo());
//		 System.out.println(hal.getFamily());
		}
		catch(Exception e)
		{
			
		}
		return  hal.getFamily();
	}
	@SuppressWarnings("unchecked")
	public static String getKeyboardLinuxInformation(String line1)
	{
		JSONObject mouseInfo= new JSONObject(); 
		String Model="";
		
		
			String[] dic = line1.toString().split("\\R");
			for(String z : dic)
			{
				String [] temp= z.split(":");
				for(int index=0; index <temp.length;index++) {
					
					if(temp[index].equals("  Model"))
						Model=temp[index+1].replaceAll("\"","");
				}
					
				
			} 
		
//		monitorInfo.put("size",dic[1]);
//		System.out.println(monitorInfo);
		return Model;
		
	}
	
	@SuppressWarnings("unchecked")
	public static String getMouseLinuxInformation(String line1)
	{
		JSONObject mouseInfo= new JSONObject(); 
		String Model="";
		
		
			String[] dic = line1.toString().split("\\R");
			for(String z : dic)
			{
				String [] temp= z.split(":");
				for(int index=0; index <temp.length;index++) {
					
					if(temp[index].equals("  Model"))
						Model= temp[index+1].replaceAll("\"","");
				}
					
				
			} 
		
//		monitorInfo.put("size",dic[1]);
//		System.out.println(monitorInfo);
		return Model;
		
	}

	@SuppressWarnings("unchecked")
	public static JSONObject getMonitorLinuxInformation(String line)
	{
		JSONObject monitorInfo= new JSONObject(); 
		
		
		
			String[] dic = line.toString().split("\\R");
			for(String z : dic)
			{
				String [] temp= z.split(":");
				for(int index=0; index <temp.length;index++) {
					
					if(temp[index].equals("  Serial ID"))
					monitorInfo.put("SerialNumber", temp[index+1].replaceAll("\"",""));
					
					else if(temp[index].equals("  Model"))
					monitorInfo.put("Name", temp[index+1].replaceAll("\"",""));	
					else if(temp[index].equals("  Size"))
						monitorInfo.put("size", temp[index+1].replaceAll("\"",""));	
				}
				
			} 
		
//		monitorInfo.put("size",dic[1]);
//		System.out.println(monitorInfo);
		return monitorInfo;
		
	}

	@SuppressWarnings("unchecked")
	public static JSONObject getMonitorInformation(HardwareAbstractionLayer hal)
	{
		JSONObject monitorInfo= new JSONObject(); 
		
		
		for(Display a: hal.getDisplays())
		{
			String[] dic = a.toString().split("\\R");
			for(String z : dic)
			{
				String [] temp= z.split(":");
				for(int index=0; index <temp.length;index++) {
					
					if(temp[index].equals("  Serial Number"))
					monitorInfo.put("SerialNumber", temp[index+1]);
					
					else if(temp[index].equals("  Monitor Name"))
					monitorInfo.put("Name", temp[index+1]);				
				}
				
			} monitorInfo.put("size",dic[1]);
		}
//		monitorInfo.put("size",dic[1]);
//		System.out.println(monitorInfo);
		return monitorInfo;
		
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject getHDDInformation(HardwareAbstractionLayer hal)
	{
		JSONObject hddInfo= new JSONObject(); 
		int index=0;
		String ind="HDD";
		long totalMem=0;
		for( HWDiskStore obj : hal.getDiskStores())
		{
			JSONObject temp= new JSONObject(); 
			temp.put("Model", obj.getModel());
			temp.put("SerialNumber", obj.getSerial());
			temp.put("size", obj.getSize()/(1000*1000*1000) + " GB");
			totalMem+=obj.getSize();
			hddInfo.put(String.format(ind+"%d",index), temp);
			index++;
		}
		hddInfo.put("totalMemory", totalMem/(1000*1000*1000)+" GB" );
//		System.out.println(hddInfo);
		
		return hddInfo;
		
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject getRAMInformation(HardwareAbstractionLayer hal)
	{
		JSONObject RAMInfo= new JSONObject(); 

		long totalMem=0;
		int index=0;
		String ind="BANK";
		for(  PhysicalMemory obj : hal.getMemory().getPhysicalMemory())
		{
			
		
			JSONObject temp= new JSONObject(); 
			temp.put("Capacity", obj.getCapacity()/(1024*1024*1024)+" GiB");
			temp.put("Memorytype", obj.getMemoryType());
			temp.put("Manufacturer", obj.getManufacturer());
			totalMem+=obj.getCapacity();
			
			RAMInfo.put(ind+index, temp);
			index++;

		}
		RAMInfo.put("totalMemory", totalMem/(1024*1024*1024)+" GiB" );
//		System.out.println(RAMInfo);
		
		return RAMInfo;
		
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject getCPUInformation(HardwareAbstractionLayer hal)
	{
		JSONObject CPUInfo= new JSONObject(); 
		CPUInfo.put("Identifier", hal.getProcessor().getProcessorIdentifier().toString());
		String[] a=hal.getProcessor().toString().split("\n");
		CPUInfo.put("Name", a[0]);
		CPUInfo.put("NumberOfLogicalProcessors",hal.getProcessor().getLogicalProcessorCount());

//		System.out.println(CPUInfo);
		
		return CPUInfo;	
	}
	@SuppressWarnings("unchecked")
	public static JSONObject getSysInformation(HardwareAbstractionLayer hal)
	{
		JSONObject SysInfo= new JSONObject(); 
		
//		System.out.println(hal.getComputerSystem());
		SysInfo.put("Manufacturer",hal.getComputerSystem().getManufacturer());
		SysInfo.put("SerialNumber",hal.getComputerSystem().getSerialNumber());
		SysInfo.put("model",hal.getComputerSystem().getModel());
//		System.out.println(SysInfo);
		
		return SysInfo;
		
	}
	
	public static String getKeyboardInformation(HardwareAbstractionLayer hal)
	{
//		JSONObject KBInfo= new JSONObject(); 
		String s="";
		try {
			List<HidDeviceInfo> devList = PureJavaHidApi.enumerateDevices();
//			System.out.println(devList.size());
			for (HidDeviceInfo info : devList) {
				
				s=info.getProductString();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
//		System.out.println(KBInfo);
		
		return s;
		
	}
	


}
