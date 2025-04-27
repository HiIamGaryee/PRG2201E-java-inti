public class PPEItem {
    private String itemCode;
    private String name;
    private String description;
    private int quantity;
    private int minStockLevel;
    private String unit;
    private String category;

    public PPEItem(String itemCode, String name, String description, int quantity, 
                  int minStockLevel, String unit, String category) {
        this.itemCode = itemCode;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.minStockLevel = minStockLevel;
        this.unit = unit;
        this.category = category;
    }

    // Getters and Setters
    public String getItemCode() { return itemCode; }
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getMinStockLevel() { return minStockLevel; }
    public void setMinStockLevel(int minStockLevel) { this.minStockLevel = minStockLevel; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isLowStock() {
        return quantity <= minStockLevel;
    }
} 