package dama_italiana;

import java.util.Arrays;
import java.util.Vector;
import java.awt.Point;

public class MovesEngine {
    /* if BLACK: 0, if WHITE: 1; */
    private int color;


    /* Max Mooves */
    private int MaxEaten = 0;
    private String BestSeq = new String();
    private int MaxType; // 0= pedina ; 1=Dama
    public Vector<Move> PossibleBestMooves = new Vector();

    /* End Max Mooves */


    public MovesEngine(int [][] matrix, int color){
        this.color = color;


        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
                if (matrix[i][j] != Dama.BLANK && matrix[i][j] % 2 == this.color) {
                    CalculateMoves(matrix, new Point(i,j), new Vector<Point>(), new String());
                }

    }

    private boolean CheckCoordinates(int x, int y){
        return ((x > -1 && x < 8) && ((y > -1) && (y < 8)));
    }

    private int CompareValue(String s){
       int occ1 = 0, occ2 = 0;
       for(int i = 0; i< s.length(); i++){
           if (s.charAt(i) == 'd')
               occ1++;
           if (this.BestSeq.charAt(i) == 'd')
               occ2++;
       }
       return Integer.signum(occ1 - occ2);
   }

    private int MovesCompare(int type,  int eaten, String seq){
        if (eaten == 0 && MaxEaten == 0)
            return 0;
        if (eaten == this.MaxEaten){
            if (type == this.MaxType){
                switch (CompareValue(seq)) {
                    case 0:
                        return seq.compareToIgnoreCase(this.BestSeq);
                    case 1: return 1;
                    case -1: return -1;
                }
            }
            else if(type > this.MaxType)
                return 1;
            else
                return -1;
        } else if (eaten > this.MaxEaten)
            return 1;

        return -1;
    }

    private void RegisterMoves(int[][] finalMatrix, Vector<Point> MCoord, boolean isDama, String seq) {
        /* Registro le mosse solo se sono più convenienti oppure se sono esattamente equipotenziali */

        switch (MovesCompare(isDama ? 1 : 0, seq.length(), seq)) {
            case 1:  this.MaxEaten = seq.length();
                this.MaxType = isDama ? 1 : 0;
                this.BestSeq = seq;
                PossibleBestMooves.clear();
                PossibleBestMooves.add(new Move(MCoord, finalMatrix));
                break;
            case 0: PossibleBestMooves.add(new Move(MCoord, finalMatrix));
                break;
        }

    }

    private static int[][] CloneMatrixAndUpdate( int[][] m, Point source,  Point dest, Point eaten){
        int [][] newM = new int[8][8];
        for(int i = 0; i < 8; i++){
            newM[i] = Arrays.copyOf(m[i],8);
        }
        newM[dest.x][dest.y] = newM[source.x][source.y];
        if (eaten != null)
            newM[eaten.x][eaten.y] = Dama.BLANK;
        newM[source.x][source.y] = Dama.BLANK;
        return newM;
    }

    private void CalculateMoves(int[][] matrix, Point c, Vector<Point> PrevC, String score){
        boolean imDama = matrix[c.x][c.y] > 2;
        boolean canMove = PrevC.size() == 0;
        int dir = matrix[c.x][c.y] % 2 == 0 ? -1 : 1;
        int myType = matrix[c.x][c.y];
        PrevC.add(c);

        boolean stop = true;

        /* vado a vedere la casella verso destra nella direzione opportuna */
        if (CheckCoordinates(c.x + dir, c.y+1)){

            /* posso spostarmi sulla destra */
            if (matrix[c.x + dir][c.y + 1] == Dama.BLANK && canMove){
                Vector<Point> currC = new Vector<Point>(PrevC);
                Point newC = new Point(c.x + dir, c.y + 1);
                currC.add(newC);
                RegisterMoves(CloneMatrixAndUpdate(matrix, c, newC, null), currC, imDama, score);
                stop = false;
            }
            /* se è occupata controllo se posso mangiare */
            else if (matrix[c.x + dir][c.y + 1] % 2 != myType % 2 && CheckCoordinates(c.x+ 2*dir, c.y + 2) && matrix[c.x + 2*dir][c.y + 2] == Dama.BLANK && matrix[c.x + dir][c.y + 1] != Dama.BLANK){
                /* se quella è una dama e io sono una dama */
                if (matrix[c.x + dir][c.y + 1] > 2 && myType > 2){
                    // MANGIO LA DAMA CON UNA DAMA
                    Point newPos = new Point(c.x + 2*dir, c.y + 2);
                    CalculateMoves(CloneMatrixAndUpdate(matrix, c, newPos, new Point(c.x + dir, c.y +1)), newPos, new Vector<Point>(PrevC), score.concat("d"));
                    stop = false;
                }
                /* altrimenti se è una pedina */
                else if (matrix[c.x + dir][c.y + 1] < 3){
                    // MANGIO UNA PEDINA
                    Point newPos = new Point(c.x + 2*dir, c.y + 2);
                    CalculateMoves(CloneMatrixAndUpdate(matrix, c, newPos, new Point(c.x + dir, c.y +1)), newPos, new Vector<Point>(PrevC), score.concat("p"));
                    stop = false;
                }
            }
        }

        /* vado a vedere la casella verso sinistra nella direzione opportuna */
        if (CheckCoordinates(c.x + dir, c.y - 1)){

            /* posso spostarmi sulla sinistra */
            if (matrix[c.x + dir][c.y - 1] == Dama.BLANK && canMove){
                Vector<Point> currC = new Vector<Point>(PrevC);
                Point newC = new Point(c.x + dir, c.y - 1);
                currC.add(newC);
                RegisterMoves(CloneMatrixAndUpdate(matrix, c, newC, null), currC, imDama, score);
                stop = false;
            }
            /* se è occupata controllo se posso mangiare */
            else if (matrix[c.x + dir][c.y - 1] % 2 != myType % 2 && CheckCoordinates(c.x+ 2*dir, c.y - 2) && matrix[c.x + 2*dir][c.y - 2] == Dama.BLANK && matrix[c.x + dir][c.y - 1] != Dama.BLANK){
                /* se quella è una dama e io sono una dama */
                if (matrix[c.x + dir][c.y - 1] > 2 && myType > 2){
                    // MANGIO LA DAMA CON UNA DAMA
                    Point newPos = new Point(c.x + 2*dir, c.y - 2);
                    CalculateMoves(CloneMatrixAndUpdate(matrix, c, newPos, new Point(c.x + dir, c.y -1)), newPos, new Vector<Point>(PrevC), score.concat("d"));
                    stop = false;
                }
                /* altrimenti se è una pedina */
                else if (matrix[c.x + dir][c.y - 1] < 3){
                    // MANGIO UNA PEDINA
                    Point newPos = new Point(c.x + 2*dir, c.y - 2);
                    CalculateMoves(CloneMatrixAndUpdate(matrix, c, newPos, new Point(c.x + dir, c.y -1)), newPos, new Vector<Point>(PrevC), score.concat("p"));
                    stop = false;
                }
            }
        }

        /* se sono una dama posso guardare anche le mosse da poter fare nella direzione opposta alla mia */
        if (imDama) {
            /* vado a vedere la casella verso destra nella direzione opportuna */
            if (CheckCoordinates(c.x - dir, c.y+1)){

            /* posso spostarmi sulla destra */
                if (matrix[c.x - dir][c.y + 1] == Dama.BLANK && canMove){
                    Vector<Point> currC = new Vector<Point>(PrevC);
                    Point newC = new Point(c.x - dir, c.y + 1);
                    currC.add(newC);
                    RegisterMoves(CloneMatrixAndUpdate(matrix, c, newC, null), currC, imDama, score);
                    stop = false;
                }
            /* se è occupata controllo se posso mangiare */
                else if (matrix[c.x - dir][c.y + 1] % 2 != myType % 2 && CheckCoordinates(c.x- 2*dir, c.y + 2) && matrix[c.x - 2*dir][c.y + 2] == Dama.BLANK && matrix[c.x - dir][c.y + 1] != Dama.BLANK){
                    // MANGIO E IN BASE A COSA HO MANGIATO AGGIORNO LA STRINGA E LE EATEN
                    Point newPos = new Point(c.x - 2*dir, c.y + 2);
                    CalculateMoves(CloneMatrixAndUpdate(matrix, c, newPos, new Point(c.x - dir, c.y +1)), newPos, new Vector<Point>(PrevC), score.concat(matrix[c.x - dir][c.y + 1] > 2 ? "d" : "p"));
                    stop = false;
                }
            }

            /* vado a vedere la casella verso sinistra nella direzione opportuna */
            if (CheckCoordinates(c.x - dir, c.y - 1)){

            /* posso spostarmi sulla sinistra */
                if (matrix[c.x - dir][c.y - 1] == Dama.BLANK && canMove){
                    Vector<Point> currC = new Vector<Point>(PrevC);
                    Point newC = new Point(c.x - dir, c.y - 1);
                    currC.add(newC);
                    RegisterMoves(CloneMatrixAndUpdate(matrix, c, newC, null), currC, imDama, score);
                    stop = false;
                }
            /* se è occupata controllo se posso mangiare */
                else if (matrix[c.x - dir][c.y - 1] % 2 != myType % 2 && CheckCoordinates(c.x- 2*dir, c.y - 2) && matrix[c.x - 2*dir][c.y - 2] == Dama.BLANK && matrix[c.x - dir][c.y - 1] != Dama.BLANK){
                    // MANGIO E IN BASE A COSA HO MANGIATO AGGIORNO LA STRINGA E LE EATEN
                    Point newPos = new Point(c.x - 2*dir, c.y - 2);
                    CalculateMoves(CloneMatrixAndUpdate(matrix, c, newPos, new Point(c.x - dir, c.y -1)), newPos, new Vector<Point>(PrevC), score.concat(matrix[c.x - dir][c.y - 1] > 2 ? "d" : "p"));
                    stop = false;
                }
            }
        }

        /* se sono semplicemente rimasta ferma, ma con almeno una mossa (quindi una mangiata) registro la mia posizione */
        if (stop && PrevC.size() > 1){
            RegisterMoves(matrix, PrevC, imDama, score);
        }

    }
}
