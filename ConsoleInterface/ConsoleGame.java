package ConsoleInterface;

import dama_italiana.Dama;
import dama_italiana.Move;

import java.awt.Point;
import java.util.Vector;

public class ConsoleGame {
    private Dama dama;
    private int Mode;

    public ConsoleGame(){
        this.dama = new Dama();
        System.out.println("Welecome in Dama Italiana game!");
        System.out.println("Please choose one game mode:");
        System.out.println(" 1: Human vs PC");
        System.out.println(" 2: PC vs PC");
        while(!CheckMode()){
           System.out.println("Bad input. Try again!");
        }
        Run();
    }

    public void Run() {
        Print();
        if (Mode == 1)
            while(true){
                try {
                    HumanMove();
                    dama.ExecutePCMove(0);
                    Print();
                } catch (UnsupportedOperationException e) {
                    System.out.println("\n\n\n" + e.toString().split(":")[1]);
                    return;
                }
            }
        else if (Mode == 2)
            while(true){
                try {
                    dama.ExecutePCMove(1);
                    dama.ExecutePCMove(0);
                    Print();
                } catch (UnsupportedOperationException e) {
                    System.out.println("\n\n\n" + e.toString().split(":")[1]);
                    return;
                }
            }
    }

    private boolean CheckMode(){
        String i = System.console().readLine();
        switch (Character.getNumericValue(i.charAt(0))) {
            case 1: Mode = 1; return true;
            case 2: Mode = 2; return true;
            default: return false;
        }
    }

    private void Print() {
        int current_matrix[][] = dama.GetMatrix();
        System.out.println("  ---------------------------------");
        for(int i=0; i<8; i++) {
            System.out.print("\033[1m"+ (8-i) +"\033[0m");
            for (int j=0; j<7; j++) {
                System.out.print(" | " + StyleIt(current_matrix[i][j]));
            }
            System.out.print(" | " + StyleIt(current_matrix[i][7]) + " |\n");
            System.out.println("  ---------------------------------");

        }
        System.out.println("\033[1m    A   B   C   D   E   F   G   H \033[0m");
    }

    private String StyleIt(int i){
        switch (i){
            case 0: return " ";
            case 1: return "\u001B[34ma\u001B[0m";
            case 2: return "\u001B[37ma\u001B[0m";
            case 3: return "\u001B[34m@\u001B[0m";
            case 4: return "\u001B[37m@\u001B[0m";
            default: return "";
        }
    }

    public void HumanMove() throws UnsupportedOperationException {
        Vector<Move> LegalMoves = dama.RetriveMoves(0);

        if (LegalMoves.size() == 0)
            throw new UnsupportedOperationException("No moves available! BLACK Wins!");
        //PrintMoves(LegalMoves);
        System.out.println("It's your turn, type your move.");
        Vector<Point> i_parsed = ParseInput();

        while(!dama.CheckMoveAndExecute(LegalMoves, i_parsed)) {
            System.out.println("Illegal moves");
            dama_italiana.DamaTree.PrintMoves(LegalMoves);
            i_parsed = ParseInput();
        }

        Print();
        System.gc(); // Chiamo il Garbage Collector esplicitamente
    }


    private Vector<Point> ParseInput() {
        String in = null;
        while(true) {
            try {
                in = System.console().readLine();


                Vector<Point> v = new Vector();
                String[] splitted = in.split(" ");

                for (String s : splitted) {

                    int x = 8 - Character.getNumericValue(s.charAt(1));

                    CheckRow(x);

                    v.add(new Point(x, L2C(s.charAt(0))));
                }

                return v;

            }
            catch (IllegalArgumentException e) {
                System.out.println(e.toString().split(":")[1]);
            }
            catch (StringIndexOutOfBoundsException e){
                System.out.println("Bad input: check the format!!!");
            }
        }
    }

    static private int L2C(char input) throws IllegalArgumentException {
        switch (Character.toUpperCase(input)){
            case 'A': return 0;
            case 'B': return 1;
            case 'C': return 2;
            case 'D': return 3;
            case 'E': return 4;
            case 'F': return 5;
            case 'G': return 6;
            case 'H': return 7;
            default: throw new IllegalArgumentException("ERROR: Possible wrong column character");
        }
    }

    static private void CheckRow(int x) throws IllegalArgumentException {
        if (x < 0 || x > 7)
            throw new IllegalArgumentException("ERROR: Possible wrong row number");
    }



}
