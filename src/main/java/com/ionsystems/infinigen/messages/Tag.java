package main.java.com.ionsystems.infinigen.messages;

public enum Tag {
	
	NetworkBandwidthSend("networkBandwidthSend"),
	NetworkBandwidthRecieve("networkBandwidthRecieve"),
	NetworkLatencySend("networkLatencySend"),
	NetworkLatencyRecieve("networkLatencyRecieve"),
	NetworkChunkUpdate("ChunkUpdate")
   
    ;

    private final String text;

    
    private Tag(final String text) {
        this.text = text;
    }

  
    
    @Override
    public String toString() {
        return text;
    }
    
    
}
