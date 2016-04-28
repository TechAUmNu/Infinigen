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
	
	public static void addMessageQueue(Tag tag){
		messageStorage.put(tag.toString(), new LinkedTransferQueue<Object>());		
	}
		
	public static Object pollLatestMessage(Tag tag){
		if(!messageStorage.containsKey(tag.toString()))
			addMessageQueue(tag);
		return messageStorage.get(tag.toString()).poll();	
		
	}
	
	public static Object peakLatestMessage(Tag tag){
		if(!messageStorage.containsKey(tag.toString()))
			addMessageQueue(tag);
		return messageStorage.get(tag.toString()).peek();		
	}
	
	public static void addMessage(Tag tag, Object message){
		if(!messageStorage.containsKey(tag.toString()))
			addMessageQueue(tag);
		messageStorage.get(tag.toString()).add(message);	
	}
	
	/**
	 * Returns true if there are any messages in this queue
	 * @param tag The queue to check
	 * @return true if there are any messages in this queue
	 */
	public static boolean anyMessages(Tag tag){
		if(!messageStorage.containsKey(tag.toString()))
			addMessageQueue(tag);
		return !messageStorage.get(tag.toString()).isEmpty();		
	}
	
	/**
	 * Retrieves and removes the head of this queue, waiting if necessary until an element becomes available.
	 * @param tag The queue to take from
	 * @return The head of the queue
	 * @throws InterruptedException
	 */
	public static Object takeLatestMessage(Tag tag){
		if(!messageStorage.containsKey(tag.toString()))
			addMessageQueue(tag);
		try {
			return messageStorage.get(tag.toString()).take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;	
	}
	
	public static Collection<Object> drainMessages(Tag tag, Collection<Object> c){
		if(!messageStorage.containsKey(tag.toString()))
			addMessageQueue(tag);
		messageStorage.get(tag.toString()).drainTo(c);
		return c;
	}
	
	
}
