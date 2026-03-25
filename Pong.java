import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

//Object Classes
class PongBall extends Rectangle {
    Color color;
    int vx, vy;
    public PongBall(Color c, int x, int y, int size, int vx, int vy) {
        super(x,y,size,size);
        this.vx = vx;
        this.vy = vy;
        color = c;
    }
}
class PongPaddle extends Rectangle {
    int vy;
    Color color;
    public PongPaddle(Color c, int x, int y, int w, int h, int vy) {
        super(x,y,w,h);
        color = c;
        this.vy = vy;
    }
}
class Score {
    Font font;
    Color color;
    int x, y, score = 0;
    public Score(Font f, Color c, int x, int y) {
        font = f;
        color = c;
        this.x = x;
        this.y = y;
    }
}

//Game classes
class PongGamePanel extends JPanel {
    boolean start;
    
    Timer timer;
    
    int AI_AREA_WIDTH, AI_AREA_X;
    
    int PLAYER_AREA_WIDTH, PLAYER_AREA_X;
    
    int SEPARATOR_BAR_WIDTH, SEPARATOR_BAR_X;
    
    int criticalX;
    
    Dimension dimension;
    Color[] palette;  //8 colors
    
    PongPaddle playerPongPaddle;
    boolean up, down;
    Score playerScore;
    
    PongPaddle aiPongPaddle;
    Score aiScore;
    
    PongBall PongBall;
    void setPlayerPongPaddle() {
        int PongPaddleX = (int)((30.0/800) * dimension.width);
        int PongPaddleY = (int)((200.0/500) * dimension.height);
        int PongPaddleWidth = (int)((20.0/800) * dimension.width);
        int PongPaddleHeight = (int)((100.0/500) * dimension.height);
        int PongPaddleSpeed = (int)((7.0/500) * dimension.height);
        up = false;
        down = false;
        playerPongPaddle = new PongPaddle(palette[3],PongPaddleX,PongPaddleY,PongPaddleWidth,PongPaddleHeight,PongPaddleSpeed);
    }
    void setAiPongPaddle() {
        int PongPaddleX = (int)((750.0/800) * dimension.width);
        int PongPaddleY = (int)((200.0/500) * dimension.height);
        int PongPaddleWidth = (int)((20.0/800) * dimension.width);
        int PongPaddleHeight = (int)((100.0/500) * dimension.height);
        int PongPaddleSpeed = (int)((6.0/500) * dimension.height);
        aiPongPaddle = new PongPaddle(palette[4],PongPaddleX,PongPaddleY,PongPaddleWidth,PongPaddleHeight,PongPaddleSpeed);
    }
    void setPongBall() {
        int PongBallSize = ((int)((20.0/500) * dimension.height) + (int)((20.0/800) * dimension.width))/2;
        int PongBallX = playerPongPaddle.x + playerPongPaddle.width;
        int PongBallY = playerPongPaddle.y + (playerPongPaddle.height - PongBallSize)/2;
        int PongBallXSpeed = Math.max(1, (int)((4.0/800) * dimension.width));
        int PongBallYSpeed = ((Math.random() > 0.5) ? 1 : -1) * Math.max(1, (int)((4.0/500) * dimension.height));
        PongBall = new PongBall(palette[5], PongBallX, PongBallY, PongBallSize, PongBallXSpeed, PongBallYSpeed);
    }
    void setScores() {
        int fontSize = (int)((300.0 / 500) * dimension.height);
        Font scoreFont = new Font("Comic Sans MS",Font.BOLD,fontSize);
        Color scoreColor = palette[6];
        
        playerScore = new Score(scoreFont,scoreColor,0,0);
    
        aiScore = new Score(scoreFont,scoreColor,0,0);
    }
    void update() {
        PongBall.x += PongBall.vx;
        PongBall.y += PongBall.vy;
        int PongBallCenter = PongBall.y + PongBall.height/2;
        
        //Player PongPaddle Movement
        if(up && playerPongPaddle.y > 0) 
            playerPongPaddle.y -= playerPongPaddle.vy;
        if(down && playerPongPaddle.y + playerPongPaddle.height < getHeight())
            playerPongPaddle.y += playerPongPaddle.vy;
            
        //Ai PongPaddle Movement
        if(PongBall.vx > 0 && PongBall.x > criticalX) {
            int aiPongPaddleCenter = aiPongPaddle.y + aiPongPaddle.height/2;
            if(aiPongPaddleCenter > PongBallCenter)
                aiPongPaddle.y -= Math.abs(aiPongPaddle.vy);
            else
                aiPongPaddle.y += Math.abs(aiPongPaddle.vy);
    
            if(aiPongPaddle.y < 0)
                aiPongPaddle.y = 0;
            if(aiPongPaddle.y + aiPongPaddle.height > dimension.height)
                aiPongPaddle.y = dimension.height - aiPongPaddle.height;
        }
            
        //PongBall Movement Control
        if(PongBall.y < 0 || PongBall.y + PongBall.height > getHeight())
            PongBall.vy = -PongBall.vy;
        if(PongBall.x < 0) {
            aiScore.score += 1;
            timer.stop();
            restartGame();
        }
        if(PongBall.x > dimension.width) {
            playerScore.score += 1;
            timer.stop();
            restartGame();
        }
            
        //PongPaddle Collision
        if(PongBall.intersects(playerPongPaddle) && PongBall.x > playerPongPaddle.x) {
            PongBall.x = playerPongPaddle.x + playerPongPaddle.width;
            int playerPongPaddleCenter = playerPongPaddle.y + playerPongPaddle.height / 2;
            if(PongBallCenter < playerPongPaddleCenter)
                PongBall.vy = -Math.abs(PongBall.vy);
            else
                PongBall.vy = Math.abs(PongBall.vy);
            PongBall.vx = -PongBall.vx;
        }
        if(PongBall.intersects(aiPongPaddle)) {
            PongBall.x = aiPongPaddle.x - PongBall.width;
            PongBall.vx = -PongBall.vx;
        }
            
        repaint();
    }
    PongGamePanel(Color[] colors, Dimension d) {
        palette = colors;
        dimension = d;
        setPreferredSize(d);
        
        AI_AREA_WIDTH = (int)((395.0/800) * d.width);
        PLAYER_AREA_WIDTH = (int)((395.0/800) * d.width);
        SEPARATOR_BAR_WIDTH = d.width - (AI_AREA_WIDTH + PLAYER_AREA_WIDTH);
        criticalX = 3 * dimension.width/4;;
        
        PLAYER_AREA_X = 0;
        SEPARATOR_BAR_X = PLAYER_AREA_WIDTH;
        AI_AREA_X = SEPARATOR_BAR_X + SEPARATOR_BAR_WIDTH;
        
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent key) {
                if(!start) {
                    start = true;
                    timer.start();
                }
                switch(key.getKeyCode()) {
                    case KeyEvent.VK_W : case KeyEvent.VK_UP : 
                        up = true;
                        break;
                    case KeyEvent.VK_S : case KeyEvent.VK_DOWN : 
                        down = true;
                        break;
                }
            }
            public void keyReleased(KeyEvent key) {
                switch(key.getKeyCode()) {
                    case KeyEvent.VK_W : case KeyEvent.VK_UP : 
                        up = false;
                        break;
                    case KeyEvent.VK_S : case KeyEvent.VK_DOWN : 
                        down = false;
                        break;
                }
            }
        });
        
        timer = new Timer(16, e -> update());
        startGame();
    }
    void startGame() {
        setPlayerPongPaddle();
        setAiPongPaddle();
        setPongBall();
        setScores();
        start = false;
            
        setFocusable(true);
    }
    void restartGame() {
        setPlayerPongPaddle();
        setAiPongPaddle();
        setPongBall();
        start = false;
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        //Drawing Player Area
        g.setColor(palette[0]);
        g.fillRect(PLAYER_AREA_X,0,PLAYER_AREA_WIDTH,getHeight());
        
        //Drawing Ai Area
        g.setColor(palette[1]);
        g.fillRect(AI_AREA_X,0,AI_AREA_WIDTH,getHeight());
        
        //Drawing Separator Bar
        g.setColor(palette[2]);
        g.fillRect(SEPARATOR_BAR_X,0,SEPARATOR_BAR_WIDTH,getHeight());
        
        //Drawing Player PongPaddle
        g.setColor(playerPongPaddle.color);
        g.fillRect(playerPongPaddle.x, playerPongPaddle.y, playerPongPaddle.width, playerPongPaddle.height);
        
        //Drawing Ai PongPaddle
        g.setColor(aiPongPaddle.color);
        g.fillRect(aiPongPaddle.x, aiPongPaddle.y, aiPongPaddle.width, aiPongPaddle.height);
        
        //Drawing PongBall 
        g.setColor(PongBall.color);
        g.fillOval(PongBall.x, PongBall.y, PongBall.width, PongBall.height);
        
        //Drawing Player Score
        g.setColor(playerScore.color);
        g.setFont(playerScore.font);
        
        FontMetrics fm = g.getFontMetrics(playerScore.font);
        
        int textWidthPlayer = fm.stringWidth("" + playerScore.score);
        playerScore.x = (PLAYER_AREA_WIDTH - textWidthPlayer) / 2;
        playerScore.y = getHeight() / 2 + (fm.getAscent() - fm.getHeight() / 2);
        
        g.drawString(Integer.toString(playerScore.score), playerScore.x, playerScore.y);
        
        //Drawing Ai Score
        g.setColor(aiScore.color);
        g.setFont(aiScore.font);
        
        fm = g.getFontMetrics(aiScore.font);
        
        int textWidthAI = fm.stringWidth("" + aiScore.score);
        aiScore.x = AI_AREA_X + (AI_AREA_WIDTH - textWidthAI) / 2;
        aiScore.y = getHeight() / 2 + (fm.getAscent() - fm.getHeight() / 2);
        
        g.drawString(Integer.toString(aiScore.score), aiScore.x, aiScore.y);
    }
}
class PongGameFrame extends JFrame {
    PongGameFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Pong Game");
        add(new PongGamePanel(new Color[]{Color.cyan, Color.red, Color.black, Color.white, Color.white, Color.white, new Color(100,100,100,40), new Color(100,100,100,40)},new Dimension(800,500)));
        pack();
        setLocationRelativeTo(null);
        setResizable(true);
        setVisible(true);
    }
}
public class Pong {
    public static void main(String args[]) {
        new PongGameFrame();
    }
}