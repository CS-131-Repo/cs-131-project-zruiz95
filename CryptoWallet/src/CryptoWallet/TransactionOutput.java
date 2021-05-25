package CryptoWallet;

import java.security.PublicKey;

public class TransactionOutput {
    public String id;
    public PublicKey reciepient; // This is the new owner of the coins
    public float value; // Amount of coins owned
    public String parentTransactionId; // ID of the transaction

    // Constructor
    public TransactionOutput(PublicKey reciepient, float value, String parentTransactionId) {
        this.reciepient = reciepient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = StringUtil.applySha256(StringUtil.getStringFromKey(reciepient)+Float.toString(value)+parentTransactionId);
    }

    // Check if coin belongs to you
    public boolean isMine(PublicKey publicKey) {
        return (publicKey == reciepient);
    }

}