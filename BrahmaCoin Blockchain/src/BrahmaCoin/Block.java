package BrahmaCoin;

import java.util.Date;

public class Block {

    public String hash;
    public String previousHash;
    private String data; // Data will be stored in a message
    private long timeStamp; // Number in milliseconds
    private int nonce;

    // Constructor
    public Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();

        this.hash = calculateHash();
    }

    // Calculate new hash based on block content
    public String calculateHash() {
        String calculatedhash = StringUtil.applySha256(previousHash + Long.toString(timeStamp)
        + Integer.toString(nonce) + data);

        return calculatedhash;
    }

    // Increases nonce value until target hash is achieved
    public void mineBlock(int difficulty) {
        String target = StringUtil.getDificultyString(difficulty);
        while(!hash.substring( 0, difficulty).equals(target)){
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block Mined Successfully: " + hash);
    }
}
