package nesquikdeveloper.escondeesconde.utils;

public class TimeFormatter {
    
    /**
     * Formata o tempo em segundos para o formato MM:SS
     * 
     * @param seconds Tempo em segundos
     * @return Tempo formatado
     */
    public static String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        
        return String.format("%02d:%02d", minutes, secs);
    }
}