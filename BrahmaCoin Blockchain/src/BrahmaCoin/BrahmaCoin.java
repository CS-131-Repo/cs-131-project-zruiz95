package BrahmaCoin;

import java.util.ArrayList;

public class BrahmaCoin {

    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static int difficulty = 5;

    public static void main(String[] args) {

        // Add block to the blockchain ArrayList:
        System.out.println("\n***********************************");
        System.out.println("\nProof-of-Work Mining Start...");



        System.out.println("\n***********************************");
        System.out.println("Mining Block 1. Please Wait...");
        addBlock(new Block("Block 1 Mined Successfully: ", "0"));
        System.out.println("***********************************");

        System.out.println("\n***********************************");
        System.out.println("Mining Block 2. Please Wait...");
        addBlock(new Block("Block 2 Mined Successfully: ", blockchain.get(blockchain.size()-1).hash));
        System.out.println("***********************************");

        System.out.println("\n***********************************");
        System.out.println("Mining Block 3. Please Wait...");
        addBlock(new Block("Block 3 Mined Successfully: ", blockchain.get(blockchain.size()-1).hash));
        System.out.println("***********************************\n");

        System.out.println("Blockchain Valid is: " + isChainValid());

        String blockchainJson = StringUtil.getJson(blockchain);
        System.out.println("\nThe Blockchain: ");
        System.out.println(blockchainJson);

        System.out.println("\nMining Complete. Now Exiting...");

    }

    public static Boolean isChainValid(){
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        // Loop through blockchain checking hashes:
        for(int i = 1; i < blockchain.size(); i++){
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i-1);
            
            // Compare registered hash and calculate hash
            if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
                System.out.println("Current Hashes are not equal!");
                return false;
            }
            // Compare previous hash and registered previous hash
            if(!previousBlock.hash.equals(currentBlock.previousHash) ){
                System.out.println("Previous Hashes are not equal");
                return false;
            }
            // Check if hash is solved
            if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
                System.out.println("This block hasn't been mined");
                return false;
            }

        }
        return true;
    }
        public static void addBlock(Block newBlock) {
            newBlock.mineBlock(difficulty);
            blockchain.add(newBlock);
        }
    }

