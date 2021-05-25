package CryptoWallet;

import java.util.ArrayList;
import java.util.Date;

public class Block{

    public String hash;
    public String previousHash;
    public String merkleRoot;
    public ArrayList<Transaction> transactions = new ArrayList<Transaction>();
    public long timeStamp; // Number of milliseconds since 1/1/1970
    public int nonce;

    //Block Constructor:
    public Block(String previousHash ) {
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();

        this.hash = calculateHash();
    }

    // Calculate new hash based on block content
    public String calculateHash() {
        String calculatedHash = StringUtil.applySha256(
                previousHash +
                        Long.toString(timeStamp) +
                        Integer.toString(nonce) +
                        merkleRoot
        );
        return calculatedHash;
    }

    // Increases nonce value until hash target is reached:
    public void mineBlock(int difficulty) {
        merkleRoot = StringUtil.getMerkleRoot(transactions);
        String target = StringUtil.getDificultyString(difficulty); // Create a string with difficulty * "0"
        while(!hash.substring( 0, difficulty).equals(target)) {
            nonce ++;
            hash = calculateHash();
        }
        System.out.println("*************************************");
        System.out.println("BrahmaCoin Block Mined Successfully!" + hash);
        System.out.println("*************************************\n");
    }

    // Add transactions to this block
    public boolean addTransaction(Transaction transaction) {

        // Process transaction and checks if valid. If block is the genesis block then ignore.
        if(transaction == null) return false;
        if((!"0".equals(previousHash))) {
            if((transaction.processTransaction() != true)) {
                System.out.println("*************************************");
                System.out.println("Transaction failed to process. Transaction Discarded...");
                System.out.println("*************************************\n");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("\n*************************************");
        System.out.println("Transaction Successfully added to Block...");
        System.out.println("*************************************\n");
        return true;
    }
}
