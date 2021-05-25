package CryptoWallet;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BrahmaCoin {

    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();

    public static int difficulty = 3;
    public static float minimumTransaction = 0.1f;
    public static Wallet walletA;
    public static Wallet walletB;
    public static Transaction genesisTransaction;

    public static void main(String[] args) {

        // Adds our blocks to the blockchain ArrayList:
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        // Create wallets:
        walletA = new Wallet(); //WalletA is Alex
        walletB = new Wallet(); // WalletB is Yuen
        Wallet coinbase = new Wallet();

        // Create genesis transaction, which sends 100 BrahmaCoin to Alex:
        genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
        genesisTransaction.generateSignature(coinbase.privateKey);	 // Manually sign the genesis transaction
        genesisTransaction.transactionId = "0"; // Manually set the transaction id
        genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.reciepient, genesisTransaction.value, genesisTransaction.transactionId)); // Manually add the Transactions Output
        UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); // It's important to store our first transaction in the UTXOs list.

        System.out.println("\n*************************************");
        System.out.println("Creating and Mining Genesis block... ");
        System.out.println("*************************************");
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);

        // Testing
        Block block1 = new Block(genesis.hash);

        System.out.println("*************************************");
        System.out.println("Alex's Current Balance: " + walletA.getBalance());
        System.out.println("Alex is Attempting to send funds to Yuen...");
        System.out.println("*************************************");
        block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
        addBlock(block1);

        System.out.println("*************************************");
        System.out.println("Alex's Current Balance: " + walletA.getBalance());
        System.out.println("Yuen's Current Balance: " + walletB.getBalance());
        System.out.println("*************************************\n");

        Block block2 = new Block(block1.hash);
        System.out.println("*************************************");
        System.out.println("Alex is attempting to send more funds than is available!!");
        System.out.println("*************************************\n");
        block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
        addBlock(block2);
        System.out.println("*************************************");
        System.out.println("Alex's Current Balance: " + walletA.getBalance());
        System.out.println("Yuen's Current Balance: " + walletB.getBalance());
        System.out.println("*************************************\n");

        Block block3 = new Block(block2.hash);
        System.out.println("*************************************");
        System.out.println("Yuen is attempting to send more funds than is available!!");
        System.out.println("*************************************");
        block3.addTransaction(walletB.sendFunds( walletA.publicKey, 20));

        System.out.println("*************************************");
        System.out.println("Alex's Current Balance: " + walletA.getBalance());
        System.out.println("Yuen's Current Balance: " + walletB.getBalance());
        System.out.println("*************************************");

        isChainValid();

    }

    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        HashMap<String,TransactionOutput> tempUTXOs = new HashMap<String,TransactionOutput>();
        tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

        // Loop through our Blockchain to check hashes
        for(int i=1; i < blockchain.size(); i++) {

            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i-1);

            //Compare registered hash and calculated hash:
            if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
                System.out.println("#Current Hashes are not equal!!");
                return false;
            }

            // Compare previous hash and registered previous hash
            if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
                System.out.println("#Previous Hashes are not equal!!");
                return false;
            }

            // Check if hash is solved
            if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
                System.out.println("#This block has not been mined!!");
                return false;
            }

            // Loop thru blockchains transactions:
            TransactionOutput tempOutput;
            for(int t=0; t <currentBlock.transactions.size(); t++) {
                Transaction currentTransaction = currentBlock.transactions.get(t);

                if(!currentTransaction.verifySignature()) {
                    System.out.println("#Signature on Transaction(" + t + ") is Invalid");
                    return false;
                }
                if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
                    System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
                    return false;
                }

                for(TransactionInput input: currentTransaction.inputs) {
                    tempOutput = tempUTXOs.get(input.transactionOutputId);

                    if(tempOutput == null) {
                        System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
                        return false;
                    }

                    if(input.UTXO.value != tempOutput.value) {
                        System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
                        return false;
                    }

                    tempUTXOs.remove(input.transactionOutputId);
                }

                for(TransactionOutput output: currentTransaction.outputs) {
                    tempUTXOs.put(output.id, output);
                }

                if( currentTransaction.outputs.get(0).reciepient != currentTransaction.reciepient) {
                    System.out.println("#Transaction(" + t + ") output recipient is not who it should be");
                    return false;
                }
                if( currentTransaction.outputs.get(1).reciepient != currentTransaction.sender) {
                    System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
                    return false;
                }
            }
        }
        System.out.println("\n*************************************");
        System.out.println("Blockchain is valid. Exiting Simulation...");
        System.out.println("*************************************");
        return true;
    }

    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }

}