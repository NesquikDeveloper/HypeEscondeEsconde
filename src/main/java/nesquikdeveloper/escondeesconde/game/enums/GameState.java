package nesquikdeveloper.escondeesconde.game.enums;

public enum GameState {
    WAITING, // Esperando jogadores
    PLAYING, // Jogo em andamento
    ENDED;   // Jogo finalizado
    
    public boolean canJoin() {
        return this == WAITING;
    }
}