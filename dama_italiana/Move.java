package dama_italiana;
import java.awt.*;
import java.util.Vector;
public class Move{
    public Vector<Point> Sequence;
    public int[][] FinalMatrix;
    public Move(Vector<Point> seq, int[][] m){
        this.Sequence = seq;
        CheckForNewDame(m);
        this.FinalMatrix = m;
    }

    private static void CheckForNewDame(int[][] matrix) {
        for(int j = 0; j < 8; j++) {
            if (matrix[0][j] == Dama.WHITE) // check dame bianche
                matrix[0][j] = Dama.D_WHITE;

            if (matrix[7][j] == Dama.BLACK)
                matrix[7][j] = Dama.D_BLACK;
        }
    }
}
