package nesquikdeveloper.escondeesconde.game.enums;

public enum PlayerRole {
    HIDER("Escondedor", "§6"), // Escondedor
    SEEKER("Procurador", "§9"); // Procurador
    
    private final String displayName;
    private final String color;
    
    PlayerRole(String displayName, String color) {
        this.displayName = displayName;
        this.color = color;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getColor() {
        return color;
    }
    
    @Override
    public String toString() {
        return color + displayName;
    }
}