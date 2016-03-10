package GUI;

import dama_italiana.Dama;
import dama_italiana.DamaTree;
import dama_italiana.Move;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Vector;

public class Board extends JPanel {
    private static int SQ_DIM = 80;
    private static int IN_R = 20;
    private dama_italiana.Dama Dama;
    private int[][] m = new int[8][8];

    public boolean HUMANvsCPU = false;
    public boolean CPUvsCPU = false;

    /* Logic */
    private Vector<Move> LegalMoves = null;
    private Vector<Point> current_m_coo = new Vector<Point>();



    /* Graphics */
    private Color d_brown = new Color(106, 53, 18);
    private Color l_brown = new Color(255, 225, 184);
    private ArrayList<Rectangle> coordinates_list = new ArrayList<>();
    private Point Start;
    private Point End;
    private JLabel statusBar;



    public Board(JLabel sB){
        super(true);
        setBackground(l_brown);
        setDoubleBuffered(true);

        statusBar = sB;
        /* inizializzo la lista delle coordinate per le correlazioni */
        for (int row = 0; row < 8; row++)
            for (int col = 0; col < 8; col++)
                if ((col % 2) == (row % 2))
                {
                    coordinates_list.add(new Rectangle(col * 80, row * 80, 80, 80));
                }

        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (HUMANvsCPU){
                    for(Rectangle r : coordinates_list)
                        if(r.contains(e.getPoint())){
                           Start = new Point(r.y / SQ_DIM, r.x / SQ_DIM);
                            break;
                        }
                }
            }
        });

        this.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (HUMANvsCPU && (m[Start.x][Start.y] == Dama.WHITE || m[Start.x][Start.y] == Dama.D_WHITE))
                    for (Rectangle r : coordinates_list)
                        if (r.contains(e.getPoint())){
                            End = new Point(r.y / SQ_DIM, r.x / SQ_DIM);
                            moveWhite();
                            break;
                        }
            }
        });
    }

    public void ChangeDepth(int depth){
        switch (depth){
            case 0: Dama.MAX_DEPTH = 2; break;
            case 1: Dama.MAX_DEPTH = 4; break;
            case 2: Dama.MAX_DEPTH = 6; break;
            case 3: Dama.MAX_DEPTH = 8; break;
        }
    }

    public void ChangeFunction(int func){
        DamaTree.FUNC = func;
    }


    private void moveWhite(){
        if (Start.equals(End))
            return;

        if (current_m_coo.isEmpty()){
            LegalMoves = Dama.RetriveMoves(0);
            if (LegalMoves.size() == 0){
                JOptionPane.showMessageDialog(null,"No moves available! BLACK Wins!");
                Reset_Game();
                return;
            }
            current_m_coo.add(Start);
            current_m_coo.add(End);
        }
        else {
            current_m_coo.add(End);
        }

        if (Dama.CheckMoveAndExecute(LegalMoves,current_m_coo)){
            m = Dama.CopyMatrix();
            System.gc();
            current_m_coo.clear();
            this.repaint();
            try {
                Dama.ExecutePCMove(0);
            } catch (UnsupportedOperationException e) {
                JOptionPane.showMessageDialog(null,e.toString().split(":")[1]);
                Reset_Game();
                return;
            }
            m = Dama.CopyMatrix();
            this.repaint();
            System.gc();
            CheckWins();
            return;
        }

        for(Move mo : LegalMoves)
            if (Check(mo.Sequence, current_m_coo)){
                if (m[End.x][End.y] == Dama.BLANK)
                {
                    m[End.x][End.y] = m[Start.x][Start.y];	//switch pedina
                    m[Start.x][Start.y] = Dama.BLANK;

                    if (Math.abs(Start.x - End.x) != 1){
                        m[Start.x + ((End.x - Start.x) / 2)][Start.y + ((End.y - Start.y)/2)] = Dama.BLANK;
                    }

                }

                if (End.x == 0)
                    m[End.x][End.y] = Dama.D_WHITE;
                this.repaint();
                return;
            }

        if (current_m_coo.size() == 2){
            current_m_coo.clear();
        }

        // a questo punto la mossa era illegale
        JOptionPane.showMessageDialog(null, "Move not allowed!");
        return;

    }

    private boolean Check (Vector<Point> LegalM, Vector<Point> CurrM){
        for (int i = 0; i < CurrM.size(); i++)
            if (!LegalM.elementAt(i).equals(CurrM.elementAt(i)))
                return false;
        return true;
    }

    public void Play_CvsC(){
        long Tempo1, Tempo2;
        statusBar.setText("CPU vs CPU mode");
        for (int i = 0; i < 200; i++){
            try {
                Dama.ExecutePCMove(1);
                m = Dama.GetMatrix();
                this.repaint();

                //Tempo1 = System.currentTimeMillis();
                Dama.ExecutePCMove(0);
                //Tempo2 = System.currentTimeMillis();
                //System.out.println((Tempo2-Tempo1)+"ms");
                m = Dama.GetMatrix();
                this.repaint();
            } catch (UnsupportedOperationException e) {
                JOptionPane.showMessageDialog(null, e.toString().split(":")[1]);
                Reset_Game();
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "DRAWN GAME!");
        Reset_Game();
        return;
    }

    public void Reset_Game(){
        this.Dama = new Dama();
        this.m = Dama.CopyMatrix();
        CPUvsCPU = HUMANvsCPU = false;
        current_m_coo.clear();
        LegalMoves = null;
        this.paint(this.getGraphics());
        statusBar.setText("Please select a mode in the menu");
    }

    private void CheckWins() {
            int numBLACK = 0, numWHITE = 0;
            for (int i = 0; i < 8; i++)
                for (int j = 0; j < 8; j++)
                {
                    if (m[i][j] == Dama.WHITE || m[i][j] == Dama.D_WHITE)
                        numWHITE++;
                    else
                    if (m[i][j] == Dama.BLACK || m[i][j] == Dama.D_BLACK)
                        numBLACK++;
                }

        if (numBLACK == 0){
            JOptionPane.showMessageDialog(null, "No moves available for PC! WHITE Wins!");
            Reset_Game();
        }

        if (numWHITE == 0){
            JOptionPane.showMessageDialog(null, "No moves available for PC! BLACK Wins!");
            Reset_Game();
        }
    }

    public void paintComponent(Graphics g){
        int x, y;
        setDoubleBuffered(true);
        Graphics2D g2 = (Graphics2D) g;
        super.paintComponent(g2);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

        /* disegno la scacchiera */
        g2.setColor(d_brown);
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                if((i % 2) == (j % 2))
                    g2.fillRect(j * SQ_DIM, i * SQ_DIM, SQ_DIM, SQ_DIM);

        /* disegno le pedine */
        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
                switch (m[i][j]){
                    case 0:             break;
                    case 1:             g2.setColor(Color.BLACK);
                                        g2.fillOval(j * SQ_DIM + 5, i * SQ_DIM + 5, SQ_DIM - 10, SQ_DIM - 10);
                                        break;
                    case 2:             g2.setColor(new Color(224, 226, 213));
                                        g2.fillOval(j * SQ_DIM + 5, i * SQ_DIM + 5, SQ_DIM - 10, SQ_DIM - 10);
                                        break;
                    case 3:             x = j * SQ_DIM;
                                        y = i * SQ_DIM;
                                        g2.setColor(Color.BLACK);
                                        g2.fillOval(x + 5, y + 5, SQ_DIM - 10, SQ_DIM - 10);
                                        g2.setColor(Color.RED);
                                        g2.fillOval(x + IN_R, y + IN_R, 2 * IN_R, 2 * IN_R);
                                        break;
                    case 4:             x = j * SQ_DIM;
                                        y = i * SQ_DIM;
                                        g2.setColor(new Color(224, 226, 213));
                                        g2.fillOval(x + 5, y + 5, SQ_DIM - 10, SQ_DIM - 10);
                                        g2.setColor(Color.DARK_GRAY);
                                        g2.fillOval(x + IN_R, y + IN_R, 2 * IN_R, 2 * IN_R);
                                        break;
                }
    }

}
