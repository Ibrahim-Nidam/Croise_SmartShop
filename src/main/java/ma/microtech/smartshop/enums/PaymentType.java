package ma.microtech.smartshop.enums;

public enum PaymentType {
    ESPECES("Cash"),
    CHEQUE("Check"),
    VIREMENT("Bank Transfer");

    private final String label;
    PaymentType(String label){ this.label = label;}
    public String getLabel(){ return label; }
}
