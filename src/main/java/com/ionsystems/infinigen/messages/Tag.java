package main.java.com.ionsystems.infinigen.messages;

public enum Tag {
	
	NetworkBandwidthSend("networkBandwidthSend"),
	NetworkLatencySend("networkLatencySend"),
	NetworkChunkUpdate("ChunkUpdate"),
	NetworkChunkRequest("ChunkRequest")
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
