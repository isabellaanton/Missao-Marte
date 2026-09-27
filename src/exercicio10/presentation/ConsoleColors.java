package solidexercicio10.presentation;

/** Cores ANSI usadas exclusivamente pela apresentação no terminal. */
public final class ConsoleColors {
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";
    public static final String BOLD = "\u001B[1m";

    private ConsoleColors() { }

    public static String colorir(char simbolo) {
        String cor;
        switch (simbolo) {
            case '@': cor = GREEN; break;
            case 'L': cor = YELLOW; break;
            case 'P':
            case 'E':
            case 'T': cor = CYAN; break;
            case '#': cor = RED; break;
            case 'X': cor = PURPLE; break;
            default: cor = WHITE;
        }
        return cor + simbolo + RESET;
    }

    public static String colorir(String texto, String cor) {
        return cor + texto + RESET;
    }
}
