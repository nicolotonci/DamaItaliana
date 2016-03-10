package GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Dimension;

public class Form extends JFrame {
    private int SQ_DIM = 80;
    public JLabel statusBar = new JLabel();
    private Board b = new Board(statusBar);

    public Form(String txt){
        super(txt);
        setLocation(300, 15);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(this.SQ_DIM * 8, this.SQ_DIM * 8 + 61);
        setResizable(false);


        /* creo il menu */

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Game");
        String[] depths = {"2", "4", "6", "8"};
        String[] funcs = {"Easy", "Hard"};
        JComboBox<String> depthMenu = new JComboBox<>(depths);
        depthMenu.setMaximumSize(new Dimension(80,20));
        depthMenu.setSelectedIndex(2);
        JComboBox<String> funcMenu = new JComboBox<>(funcs);
        funcMenu.setMaximumSize(new Dimension(100,20));
        menuBar.add(menu);
        menuBar.add(depthMenu);
        menuBar.add(funcMenu);
        depthMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                b.ChangeDepth(depthMenu.getSelectedIndex());
            }
        });

        funcMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                b.ChangeFunction(funcMenu.getSelectedIndex());
            }
        });

        JMenuItem HvsC_m = new JMenuItem("Play Human vs CPU");
        HvsC_m.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                b.Reset_Game();
                b.HUMANvsCPU = true;
            }
        });

        JMenuItem CvsC_m = new JMenuItem("Play CPU vs CPU");
        CvsC_m.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                new Thread(() -> {
                    b.Reset_Game();
                    b.CPUvsCPU = true;
                    b.Play_CvsC();
                }).start();

            }
        });

        JMenuItem Restart_m = new JMenuItem("Restart Game");
        Restart_m.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                b.Reset_Game();
            }
        });

        menu.add(HvsC_m);
        menu.add(CvsC_m);
        menu.add(Restart_m);

        this.setJMenuBar(menuBar);

        this.getContentPane().add(b);

        /* aggiungo la status Bar */
        statusBar.setPreferredSize(new Dimension(this.SQ_DIM * 8, 16));
        this.add(statusBar, java.awt.BorderLayout.SOUTH);
    }
}
