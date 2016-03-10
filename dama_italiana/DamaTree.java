package dama_italiana;

import java.awt.*;
import java.util.Vector;
import java.util.Arrays;

public class DamaTree {
    /* massima profondità di costruzione dell'albero */
    public static int FUNC = 0;

    public int score;
    public int[][] matrix;

    private Vector<DamaTree> sons = new Vector();
    private int turn;

    public DamaTree(int[][] matrix, int depth, int turn){
        this.matrix = matrix;
        this.turn = turn;
        if(depth == 0){
            switch (FUNC){
                case 0: score = Dama.Valute(matrix);break;
                case 1: score = Dama.EvalPrima(matrix);break;
            }
            return;
        }
        int color;

        if (turn == 0)
            color = depth % 2 == 0 ? 1 : 0; // 0 se è il turno del bianco; 1 se è turno del computer
        else
            color = depth % 2 == 0 ? 0 : 1;

        MovesEngine me = new MovesEngine(matrix, color);

        /* caso in cui il nodo sia una foglia */
        /*if (me.PossibleBestMooves.size() == 0) {
            score = Dama.Valute(matrix);
            return;
        }
    */

        /* creazione albero di ricerca */
        for(Move m : me.PossibleBestMooves){
            sons.add(new DamaTree(m.FinalMatrix, depth-1, turn));
        }
        /* applicazione algoritmo MIN-MAX */
        if (color == 1){
            this.score = Integer.MIN_VALUE;
            // cerco il MAX e lo metto nel mio score
            for (DamaTree s : sons)
                if (s.score > this.score)
                    this.score = s.score;

        }
        else {
            this.score = Integer.MAX_VALUE;
            // cerco il MIN e lo metto nello score
            for (DamaTree s : sons)
                if (s.score < this.score)
                    this.score = s.score;

        }
    }

    public int[][] ExecuteBestMove() throws UnsupportedOperationException{
        int matrix[][] = new int[8][8];

        if (sons.size() == 0)
            throw new UnsupportedOperationException("No moves available for PC! "+ (turn == 0 ? "WHITE" : "BLACK") +" Wins!");

        for (DamaTree s : sons)
            if (s.score == this.score){
                for(int i = 0; i < 8; i++)
                    matrix[i] = Arrays.copyOf(s.matrix[i],8);

                return matrix;
            }
        return matrix;
    }

    /* DEBUG METOHD --- ignore*/
    public static void PrintMoves(Vector<Move> m){
        System.out.println("Serie di mosse: "+ m.size());
        for(Move serie : m){
            for(Point c : serie.Sequence){
                System.out.print(c.toString());
            }
            System.out.print("\n");
        }

    }

}
