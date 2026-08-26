package missao;

import java.util.Random;

public class Inimigo {
    private int x;
    private int y;

    public Inimigo(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void moverAleatoriamente(int minX,int maxX,int minY,int maxY) {
        Random random = new Random();
        int direcao = random.nextInt(4);

        switch(direcao) {
            case 0:
                if (this.y > minY) {
                    this.y--;
                }
                break;
            case 1:
                if (this.x < maxX) {
                    this.x++;
                }
                break;
            case 2:
                if (this.y < maxY) {
                    this.y++;
                }
                break;
            case 3:
                if (this.x > minX) {
                    this.x--;
                }
                break;
        }
    }
}
