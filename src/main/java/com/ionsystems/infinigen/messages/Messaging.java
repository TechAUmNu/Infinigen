package main.java.com.ionsystems.infinigen.messages;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedTransferQueue;

public class Messaging {
	/*
	 * The message system is an intermediary between the different modules of the game. It allows any module to send and receive messages from any other section.
	 * 
	 * So basically it is a hash map of queues.
	 */
	
	
	// The main data storage for messages is a ConcurrentHashMap which provides very high performance for high concurrency applications
	private static ConcurrentHashMap<String, LinkedTransferQueue<Object>> messageStorage = new ConcurrentHashMap<String, LinkedTransferQueue<Object>>();
	
	public static void addMessageQueue(Tag tag, LinkedTransferQueue<Object> queue){
		messageStorage.put(tag.toString(), queue);
	}
	
	public static LinkedTransferQueue<Object> getMessageQueue(String tag){
		return messageStorage.get(tag);
	}
	
	public static Object pollLatestMessage(String tag){
		return messageStorage.get(tag).poll();		
	}
	
	public static Object peakLatestMessage(String tag){
		return messageStorage.get(tag).peek();		
	}
	
	public static void addMessage(String tag, Object message){
		messageStorage.get(tag).add(message);	
	}
	
	public static boolean anyMessages(String tag){
		return messageStorage.get(tag).isEmpty();		
	}
	
	public static Object takeLatestMessage(String tag) throws InterruptedException{
		return messageStorage.get(tag).take();	
	}
	
	public static Collection<Object> drainMessages(String tag, Collection<Object> c){
		messageStorage.get(tag).drainTo(c);
		return c;
	}
	
	
}
