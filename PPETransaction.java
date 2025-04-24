import java.util.Date;

public class PPETransaction {
    private int transactionId;
    private String itemCode;
    private int quantity;
    private Date transactionDate;
    private String transactionType; // "RECEIVE" or "DISTRIBUTE"
    private String sourceDestination;
    private String notes;

    public PPETransaction(int transactionId, String itemCode, int quantity, 
                         Date transactionDate, String transactionType, 
                         String sourceDestination, String notes) {
        this.transactionId = transactionId;
        this.itemCode = itemCode;
        this.quantity = quantity;
        this.transactionDate = transactionDate;
        this.transactionType = transactionType;
        this.sourceDestination = sourceDestination;
        this.notes = notes;
    }

    // Getters and Setters
    public int getTransactionId() { return transactionId; }
    public void setTransactionId(int transactionId) { this.transactionId = transactionId; }

    public String getItemCode() { return itemCode; }
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public Date getTransactionDate() { return transactionDate; }
    public void setTransactionDate(Date transactionDate) { this.transactionDate = transactionDate; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public String getSourceDestination() { return sourceDestination; }
    public void setSourceDestination(String sourceDestination) { this.sourceDestination = sourceDestination; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
} 