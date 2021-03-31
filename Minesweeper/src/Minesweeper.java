import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.io.IOException;
import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

public class Minesweeper extends JFrame implements ActionListener, MouseListener
{
    JToggleButton[][] board;
    JPanel boardPanel;
    boolean firstClick = true;
    int numMines, rowAmt, colAmt;
    ImageIcon mine, flag, coolEmoji, deadEmoji, smileyEmoji, shockedEmoji;
    ImageIcon[] numbers;
    GraphicsEnvironment ge;
    Font mineFont, timerFont;
    boolean gameOn = true;
    JMenuBar jMenuBar;
    JMenu difficultyLevelMenu;
    JMenuItem beginnerMenuItem, intermediateMenuItem, expertMenuItem;
    JButton reset;
    Timer timer;
    int timePassed;
    JTextField timeField;

    public Minesweeper()
    {
        jMenuBar = new JMenuBar();
        jMenuBar.setLayout(new GridLayout(1, 2));
        difficultyLevelMenu = new JMenu("Difficulty Menu");
        beginnerMenuItem = new JMenuItem("Beginner");
        intermediateMenuItem = new JMenuItem("Intermediate");
        expertMenuItem = new JMenuItem("Exert");
        beginnerMenuItem.addActionListener(this);
        intermediateMenuItem.addActionListener(this);
        expertMenuItem.addActionListener(this);
        difficultyLevelMenu.add(beginnerMenuItem);
        difficultyLevelMenu.add(intermediateMenuItem);
        difficultyLevelMenu.add(expertMenuItem);
        timeField = new JTextField();
        rowAmt = 9;
        colAmt = 9;
        try
        {
            ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            timerFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/digital-7.ttf"));
            ge.registerFont(timerFont);
        }catch(Exception e){}
        timeField.setForeground(Color.RED);
        timeField.setBackground(Color.BLACK);
        timeField.setText(""+timePassed);

        jMenuBar.add(difficultyLevelMenu);

        numMines = 10;
        try
        {
            ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            mineFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/mine-sweeper.ttf"));
            ge.registerFont(mineFont);
        }catch(Exception e){}

        numbers = new ImageIcon[8];
        for(int x = 1; x<=8; x++)
        {
            numbers[x-1] = new ImageIcon("src/" + x + ".png");
            numbers[x-1] = new ImageIcon(numbers[x-1].getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
        }

        mine = new ImageIcon("src/mine.png");
        mine = new ImageIcon(mine.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));

        flag = new ImageIcon("src/flag.png");
        flag = new ImageIcon(flag.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));

        coolEmoji = new ImageIcon("src/win1.png");
        coolEmoji = new ImageIcon(coolEmoji.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));

        deadEmoji = new ImageIcon("src/lose1.png");
        deadEmoji = new ImageIcon(deadEmoji.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));

        smileyEmoji = new ImageIcon("src/smile1.png");
        smileyEmoji = new ImageIcon(smileyEmoji.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));

        shockedEmoji = new ImageIcon("src/wait1.png");
        shockedEmoji = new ImageIcon(shockedEmoji.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));

        UIManager.put("ToggleButton.select", Color.CYAN);

        reset = new JButton(smileyEmoji);
        reset.addActionListener(this);
        jMenuBar.add(reset);
        jMenuBar.add(timeField);

        createBoard(rowAmt, colAmt);
        this.add(jMenuBar, BorderLayout.NORTH);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public void createBoard(int row, int col)
    {
        if(boardPanel != null)
        {
            this.remove(boardPanel);
        }
        boardPanel = new JPanel();
        board = new JToggleButton[row][col];
        boardPanel.setLayout(new GridLayout(row, col));
        for(int r = 0; r<row; r++)
        {
            for(int c = 0; c<col; c++)
            {
                board[r][c] = new JToggleButton();
                board[r][c].putClientProperty("row", r);
                board[r][c].putClientProperty("col", c);
                board[r][c].putClientProperty("state", 0);
                board[r][c].setBorder(BorderFactory.createBevelBorder(0));
                board[r][c].setFocusPainted(false);
                board[r][c].setFont(mineFont.deriveFont(12f));
                board[r][c].addMouseListener(this);
                boardPanel.add(board[r][c]);
            }
        }
        this.add(boardPanel, BorderLayout.CENTER);
        this.setSize(col*40, row*40);//Column goes first because it is width x height
        this.revalidate();
    }

    public static void main(String[]args)
    {
        Minesweeper app = new Minesweeper();
    }

    public void setMinesAndCounts(int selectedRow, int selectedCol)
    {
        int count = numMines;
        int dimR = board.length;
        int dimC = board[0].length;

        while(count > 0)
        {
            int randR = (int)(Math.random()*dimR);
            int randC = (int)(Math.random()*dimC);
            int state = (int)((board[randR][randC].getClientProperty("state")));

            if(state == 0 && (Math.abs(randR-selectedRow) > 1 || Math.abs(randC-selectedCol) > 1))
            {
                board[randR][randC].putClientProperty("state", 9);
                count--;
            }
        }
        for(int r = 0; r<dimR; r++)
        {
            for (int c = 0; c < dimC; c++)
            {
                count = 0;
                int currentButtonState = (int)((board[r][c].getClientProperty("state")));
                if(currentButtonState != 9)
                {
                    for(int rSmall = r-1; rSmall<=r+1; rSmall++)
                    {
                        for(int cSmall = c-1; cSmall<=c+1; cSmall++)
                        {
                            try
                            {
                                int state = (int)((board[rSmall][cSmall].getClientProperty("state")));
                                if(state == 9)
                                {
                                    count++;
                                }
                            }catch(ArrayIndexOutOfBoundsException e)
                            {
                            }
                        }
                    }
                    board[r][c].putClientProperty("state", count);
                }
            }
        }
/*
        for(int r = 0; r<dimR; r++)
        {
            for(int c = 0; c<dimC; c++)
            {
                int state = (int)((board[r][c].getClientProperty("state")));
                board[r][c].setText(""+state);
            }
        }
 */
    }

    public void writeText(int row, int col, int state)
    {
        switch(state)
        {
            case 1: board[row][col].setForeground(Color.BLUE); break;
            case 2: board[row][col].setForeground(Color.GREEN); break;
            case 3: board[row][col].setForeground(Color.RED); break;
            case 4: board[row][col].setForeground(new Color(128, 0, 128)); break;
            case 5: board[row][col].setForeground(new Color(128, 0, 0)); break;
            case 6: board[row][col].setForeground(Color.CYAN); break;
            case 7: board[row][col].setForeground(Color.BLACK); break;
            case 8: board[row][col].setForeground(Color.ORANGE); board[row][col].setBackground(Color.RED); break;
            case 9: board[row][col].setIcon(mine); break;
        }
        if(state != 0)
        {
            board[row][col].setIcon(numbers[state-1]);
            board[row][col].setDisabledIcon(numbers[state-1]);
        }
    }

    public void expand(int row, int col)
    {
        if(!board[row][col].isSelected())
        {
            board[row][col].setSelected(true);
        }
        int state = (int)((board[row][col].getClientProperty("state")));
        if(state > 0)
        {
            writeText(row, col, state);
        }
        else
        {
            for(int rSmall = row-1; rSmall<=row+1; rSmall++)
            {
                for (int cSmall = col - 1; cSmall <= col + 1; cSmall++)
                {
                    if(!(rSmall==row && cSmall==col))
                    {
                        try
                        {
                            if(!board[rSmall][cSmall].isSelected())
                            {
                                expand(rSmall, cSmall);
                            }
                        }catch (ArrayIndexOutOfBoundsException e){}
                    }
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        int row = (int)(((JToggleButton)e.getComponent()).getClientProperty("row"));
        int col = (int)(((JToggleButton)e.getComponent()).getClientProperty("col"));
        System.out.println(row + ", " + col);

        if(gameOn)
        {
            reset.setIcon(shockedEmoji);
            if(e.getButton() == MouseEvent.BUTTON1 && board[row][col].isEnabled())
            {
                if (firstClick)
                {
                    timer = new Timer();
                    timer.schedule(new UpdateTimer(), 0, 1000);
                    setMinesAndCounts(row, col);
                    firstClick = false;
                }
                int currentButtonState = (int) ((board[row][col].getClientProperty("state")));
                if (currentButtonState == 9)
                {
                    board[row][col].setIcon(mine);
                    board[row][col].setContentAreaFilled(false);
                    board[row][col].setOpaque(true);
                    board[row][col].setBackground(Color.RED);
                    revealMines();
                    gameOn = false;
                    //JOptionPane.showMessageDialog(null, "You Lost!");
                    reset.setIcon(deadEmoji);
                    timer.cancel();
                }
                else{
                    expand(row, col);
                    checkWin();
                }
            }
            if(e.getButton() == MouseEvent.BUTTON3)
            {
                if(!board[row][col].isSelected())
                {
                    if(board[row][col].getIcon() == null)
                    {
                        board[row][col].setIcon(flag);
                        board[row][col].setDisabledIcon(flag);
                        board[row][col].setEnabled(false);
                    }
                    else
                    {
                        board[row][col].setIcon(null);
                        board[row][col].setDisabledIcon(null);
                        board[row][col].setEnabled(true);
                    }
                }
            }
        }
        else
        {
            if(board[row][col].isSelected())
            {
                board[row][col].setSelected(true);
            }
            else
            {
                board[row][col].setSelected(false);
            }
        }
    }
    public void revealMines()
    {
        for(int r = 0; r<rowAmt; r++)
        {
            for(int c = 0; c<colAmt; c++)
            {
                int state = (int) ((board[r][c].getClientProperty("state")));
                if(state == 9)
                {
                    board[r][c].setIcon(mine);
                    board[r][c].setDisabledIcon(mine);
                    board[r][c].setSelected(true);
                }
                board[r][c].setEnabled(false);
            }
        }
    }

    public void checkWin()
    {
        int dimR = board.length;
        int dimC = board[0].length;
        int totalSpaces = dimR*dimC;
        int count = 0;

        for(int r = 0; r<dimR; r++)
        {
            for(int c = 0; c<dimC; c++)
            {
                int state = (int)((board[r][c].getClientProperty("state")));
                if(state != 9 && board[r][c].isSelected())
                {
                    count++;
                }
            }
        }
        System.out.println(totalSpaces - count);
        if(numMines == totalSpaces-count)
        {
            reset.setIcon(coolEmoji);
            timer.cancel();
            //JOptionPane.showMessageDialog(null, "You Win!");
            gameOn = false;
            for(int r = 0; r<dimR; r++)
            {
                for(int c = 0; c<dimC; c++)
                {
                    board[r][c].setEnabled(false);
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == beginnerMenuItem)
        {
            numMines = 10;
            rowAmt = 9; colAmt = 9;
            createBoard(rowAmt, colAmt);
            reset.setIcon(smileyEmoji);
            timePassed = 0;
            timeField.setText(""+timePassed);
            gameOn = true;
            firstClick = true;
        }
        if(e.getSource() == intermediateMenuItem)
        {
            numMines = 40;
            rowAmt = 16; colAmt = 16;
            createBoard(rowAmt, colAmt);
            reset.setIcon(smileyEmoji);
            timePassed = 0;
            timeField.setText(""+timePassed);
            gameOn = true;
            firstClick = true;
        }
        if(e.getSource() == expertMenuItem)
        {
            numMines = 99;
            rowAmt = 16; colAmt = 40;
            createBoard(rowAmt, colAmt);
            reset.setIcon(smileyEmoji);
            timePassed = 0;
            timeField.setText(""+timePassed);
            gameOn = true;
            firstClick = true;
        }
        if(e.getSource() == reset)
        {
            reset.setIcon(smileyEmoji);
            timePassed = 0;
            timeField.setText(""+timePassed);
            createBoard(rowAmt, colAmt);
            gameOn = true;
            firstClick = true;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e){}

    @Override
    public void mousePressed(MouseEvent e){}

    @Override
    public void mouseEntered(MouseEvent e){}

    @Override
    public void mouseExited(MouseEvent e){}

    public class UpdateTimer extends TimerTask
    {
        @Override
        public void run()
        {
            if(gameOn)
            {
                reset.setIcon(smileyEmoji);
                timePassed++;
                timeField.setText(""+timePassed);
            }
        }
    }
}
