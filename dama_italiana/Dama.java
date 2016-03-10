package dama_italiana;

import java.awt.Point;
import java.util.*;

public class Dama {

    /*
    Definizione delle pedine e delle dame
     */
    public final static int BLANK = 0;

    public final static int BLACK = 1;
    public final static int WHITE = 2;

    public final static int D_BLACK = 3;
    public final static int D_WHITE = 4;

    /*
    Altro
     */
    public static int MAX_DEPTH = 6;

    private int matrix[][];

    public Dama() {
        /*
        Inizializzo la matrix con l'opportuna disposizione delle pedine
         */
        this.matrix = new int[8][8];
       for(int i = 0; i<8; i++)
           for(int j = 0; j<8; j++)
               if ((i % 2) == (j % 2)) {
                   if (i<3)
                       this.matrix[i][j] = BLACK;
                   else if (i>4)
                       this.matrix[i][j] = WHITE;
                   else
                       this.matrix[i][j] = BLANK;
               } else
                   this.matrix[i][j] = BLANK;
    }

    public int[][] GetMatrix(){
        return this.matrix;
    }

    public int[][] CopyMatrix() {
        int [][] newM = new int[8][8];
        for(int i = 0; i < 8; i++){
            newM[i] = Arrays.copyOf(matrix[i],8);
        }
        return newM;
    }

    public Vector<Move> RetriveMoves(int color){
        MovesEngine me = new MovesEngine(matrix, color);
        return me.PossibleBestMooves;
    }


    public boolean CheckMoveAndExecute(Vector<Move> m, Vector<Point> p){
        for(Move move : m)
            if (move.Sequence.equals(p)) {
                for(int i = 0; i < 8; i++)
                    this.matrix[i] = Arrays.copyOf(move.FinalMatrix[i],8);
                return true;
            }
        return false;
    }
    /*
    turn  = 0 -> Muove il black
    turn  = 1 -> Muove il white
     */
    public void ExecutePCMove(int turn) throws UnsupportedOperationException{
        DamaTree dt = new DamaTree(this.matrix, MAX_DEPTH, turn);
        this.matrix = dt.ExecuteBestMove();
        dt = null;
        System.gc();
    }

    /* Funzione di valutazione */
    public static int Valute(int[][] matrix){
        int score = 0;
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                if (matrix[i][j] == WHITE) {
                    score -= 100;
                    score -= 1 * (7-i) * (7-i);
                }
                else if (matrix[i][j] == D_WHITE){
                    score -= 200;

                    if (i == 0 || i == 7)		//check  WHITE
                        score += 10;

                    if (j == 0 || j == 7)
                        score += 10;
                }
                else if (matrix[i][j] == D_BLACK) {
                    score += 200;

                    if (i == 0 || i == 7)		//check BLACK
                        score -= 10;

                    if (j == 0 || j == 7)
                        score -= 10;
                }
                else if (matrix[i][j] == BLACK) {
                    score += 100;
                    score += 1 * i * i;
                }

        score += (int)(Math.random() * 10);
        return score;
    }
    public static int EvalPrima(int[][] position) {

        final int CHECKER = 100;                    // peso di una pedina
        final int POS = 1;                    // valore di una posizione
        final int KING = 200;                    // peso di una dama
        int score = 0;                        // azzero la variabile ove salvare il punteggio
        int[][] posKW = new int[12][2];
        int[][] posKB = new int[12][2];
        int nKW = 0, nKB = 0;

        // scorro la board per valutarla
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                if (position[i][j] == WHITE) {
                    score -= CHECKER;
                    score -= POS * i * i;
                    score -= supportoWHITE(position, i, j);
                } else if (position[i][j] == BLACK) {
                    score += CHECKER;
                    score += POS * (7 - i) * (7 - i);
                    score += supportoBLACK(position, i, j);
                } else if (position[i][j] == D_WHITE) {
                    posKW[nKW][0] = i;
                    posKW[nKW][1] = j;
                    nKW++;
                    score -= KING;
                    if (i == 0 || i == 7)
                        score += 10;
                    if (j == 0 || j == 7)
                        score += 10;
                } else if (position[i][j] == D_BLACK) {
                    posKB[nKB][0] = i;
                    posKB[nKB][1] = j;
                    nKB++;
                    score += KING;
                    if (i == 0 || i == 7)
                        score -= 10;
                    if (j == 0 || j == 7)
                        score -= 10;
                }

            }
        return score;
    }
    public static int supportoBLACK(int[][] position, int i, int j){
        int score =0;
        int tmp=0;

        if(!inRange(i,j) || (( inRange(i+1,j-1) && position[i+1][j-1] != BLACK ) && (inRange(i+1,j+1) && position[i+1][j+1] != BLACK)))
            if(i==8)
                return 0;
            else
                return -1;
        else{
            tmp=supportoBLACK(position, i+1, j-1);
            if(tmp!=-1)
                score+=tmp+2;
            tmp=supportoBLACK(position, i+1, j+1);
            if(tmp!=-1)
                score+=tmp+2;
        }
        return score;
    }

    public static int supportoWHITE(int[][] position, int i, int j){
        int score =0;
        int tmp=0;

        if(!inRange(i,j) || (( inRange(i-1,j-1) && position[i-1][j-1] != WHITE ) && (inRange(i-1,j+1) && position[i-1][j+1] != WHITE)))
            if(i==-1)
                return 0;
            else
                return -1;
        else{
            tmp=supportoWHITE(position, i-1, j-1);
            if(tmp!=-1)
                score+=tmp+2;
            tmp=supportoWHITE(position, i-1, j+1);
            if(tmp!=-1)
                score+=tmp+2;
        }
        return score;
    }

    public static boolean inRange(int i, int j)
    {
        return (i>-1 && i<8 && j>-1 && j<8);
    }
}

