package demo1;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

//俄罗斯方块主类
public class Tetris extends JPanel {
    //声明正在下落的方块
    private Tetromino currentOne = Tetromino.RandomOne();
    //声明将要下落的方块
    private Tetromino nextOne = Tetromino.RandomOne();
    //声明游戏主区域
    private Cell[][] wall = new Cell[18][9];
    //声明单元格的值为48像素
    private static final int CELL_SIZE =48;
    //声明游戏分数池
    int[] scores_pool = {0,1,2,5,10};
    //声明当前获得游戏分数
    private int totalScore = 0;
    //当前已消除行数
    private int totalLine = 0;
    //声明三个常量：游戏中、暂停、游戏结束
    public static final int PLAYING = 0;
    public static final int PAUSE = 1;
    public static final int GAME_OVER = 2;
    //声明变量存放当前游戏状态值
    private int gameState;
    //声明一个数组，用来显示当前游戏状态
    String[] show_state = {"游戏中","游戏暂停","游戏结束"};

    //载入方块图片
    public static BufferedImage I;
    public static BufferedImage J;
    public static BufferedImage L;
    public static BufferedImage O;
    public static BufferedImage S;
    public static BufferedImage T;
    public static BufferedImage Z;
    public static BufferedImage backImage;

    static {

        try {
            I = ImageIO.read(new File("E:\\mysql\\Game\\game\\src\\images\\I.png"));
            J = ImageIO.read(new File("E:\\mysql\\Game\\game\\src\\images\\J.png"));
            L = ImageIO.read(new File("E:\\mysql\\Game\\game\\src\\images\\L.png"));
            O = ImageIO.read(new File("E:\\mysql\\Game\\game\\src\\images\\O.png"));
            S = ImageIO.read(new File("E:\\mysql\\Game\\game\\src\\images\\S.png"));
            T = ImageIO.read(new File("E:\\mysql\\Game\\game\\src\\images\\T.png"));
            Z = ImageIO.read(new File("E:\\mysql\\Game\\game\\src\\images\\Z.png"));
            backImage = ImageIO.read(new File("E:\\mysql\\Game\\game\\src\\images\\backImage.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(backImage,0,0,null);
        //平移坐标轴
        g.translate(22,15);
        //绘制游戏主区域
        paintWall(g);
        //绘制正在下落的四方格
        paintCurrentOne(g);
        //绘制将要下落的四方格
        paintNextOne(g);
        //绘制游戏得分
        paintScore(g);
        //绘制游戏状态
        paintState(g);
    }
    public void start(){
        gameState = PLAYING;
        KeyListener listener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
               int code = e.getKeyCode();
               switch (code){
                   case KeyEvent.VK_DOWN:
                       softDropAction();
                       break;
                   case KeyEvent.VK_LEFT:
                       moveLeftAction();
                       break;
                   case KeyEvent.VK_RIGHT:
                       moveRightAction();
                       break;
                   case KeyEvent.VK_UP:
                       rotateRightAction();
                       break;
                   case KeyEvent.VK_SPACE:
                       handDrop();
                       break;
                   case KeyEvent.VK_P:
                       //判断当前游戏状态
                       if(gameState==PLAYING){
                           gameState=PAUSE;
                       }
                       break;
                   case KeyEvent.VK_C:
                       if(gameState==PAUSE){
                           gameState=PLAYING;
                       }
                       break;
                   case KeyEvent.VK_S:
                       gameState=PLAYING;
                       wall = new Cell[18][9];
                       currentOne = Tetromino.RandomOne();
                       nextOne = Tetromino.RandomOne();
                       totalLine = 0;
                       totalScore = 0;
                       break;
               }
            }
        };
        //将俄罗斯方块窗口设置为焦点
        this.addKeyListener(listener);
        this.requestFocus();

        while(true){
            //判断当前游戏状态在游戏中时，每隔0.5秒下落一次
            if(gameState==PLAYING){
                try {
                    Thread.sleep(500);
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //判断能否下落
                if(canDrop()){
                 currentOne.softDrop();
                }else{
                //将四方格嵌入墙中
                landToWall();
                //判断能否消行
                destroyLine();
                //判断游戏是否结束
                if(isGameOver()){
                    gameState = GAME_OVER;
                }else{
                    //当前游戏没有结束，继续生成新的四方格
                    currentOne = nextOne;
                    nextOne = Tetromino.RandomOne();
                }
                }
            }
            repaint();
        }
    }
    //创建顺时针旋转方法
    public void rotateRightAction(){
        currentOne.rotateRight();
        if(outOfBounds()||coincide()){
            currentOne.rotateLeft();
        }
    }
    //瞬间下落
    public void handDrop(){
        while(true){
            if(canDrop()){
                currentOne.softDrop();
            }else{
                break;
            }
        }
        //将四方格嵌入墙中
        landToWall();
        //判断能否消行
        destroyLine();
        //判断游戏是否结束
        if(isGameOver()){
            gameState = GAME_OVER;
        }else{
            //当前游戏没有结束，继续生成新的四方格
            currentOne = nextOne;
            nextOne = Tetromino.RandomOne();
        }

    }
    //按键一次四方格下落一次
    public void softDropAction(){
        //判断能否下落
        if(canDrop()){
            currentOne.softDrop();
        }else{
            //将四方格嵌入墙中
            landToWall();
            //判断能否消行
            destroyLine();
            //判断游戏是否结束
            if(isGameOver()){
                gameState = GAME_OVER;
            }else{
                //当前游戏没有结束，继续生成新的四方格
                currentOne = nextOne;
                nextOne = Tetromino.RandomOne();
            }
        }
    }
    //将四方格嵌入墙中
    private void landToWall() {
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells) {
            int row = cell.getRow();
            int col = cell.getCol();
            wall[row][col] = cell;
        }
    }

    //判断四方格能否下落
    public boolean canDrop(){
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells) {
            int row = cell.getRow();
            int col = cell.getCol();
            //判断是否到达底部
            if(row == wall.length-1){
                return false;
            }else if(wall[row+1][col] != null){

                return false;
            }

        }
        return true;
    }
    //创建消行方法
    public void destroyLine(){
        int line = 0;
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells) {
            int row = cell.getRow();
            if(isFullLine(row)){
                line++;
                for(int i = row;i>0;i--){
                    System.arraycopy(wall[i-1],0,wall[i],0,wall[0].length);
                }
                wall[0]  = new Cell[9];
            }
        }
        //在分数池中获取分数，累加到总分数中
        totalScore += scores_pool[line];
        //获取消除行数累加到总行数中
        totalLine += line;
    }
    //判断当前行是否已满
    public boolean isFullLine(int row){
        Cell[] cells = wall[row];
        for (Cell cell : cells) {
            if(cell == null){
                return false;
            }
        }return true;
    }
    //判断游戏是否结束
    public boolean isGameOver(){
        Cell[] cells = nextOne.cells;
        for (Cell cell : cells) {
            int row = cell.getRow();
            int col = cell.getCol();
            if(wall[row][col] != null){
                return true;
            }
        }return false;
    }
    //绘制游戏当前状态
    private void paintState(Graphics g) {
        if(gameState==PLAYING){
            g.drawString("按P暂停游戏",530,690);
        }else if(gameState==PAUSE){
            g.drawString("按C继续游戏",530,690);
        }else {
            g.drawString("按S重新开始",530,690);
            g.setColor(Color.red);
            g.setFont(new Font(Font.DIALOG,Font.BOLD,60));
            g.drawString("GAMEOVER!",90,400);
        }

    }

    private void paintScore(Graphics g) {
        g.setFont(new Font(Font.DIALOG,Font.BOLD,25));
        g.setColor(Color.black);
        g.drawString("当前游戏得分："+totalScore,520,340);
        g.drawString("当前消除行数："+totalLine,520,510);
    }

    private void paintNextOne(Graphics g) {
        Cell[] cells = nextOne.cells;
        for (Cell cell : cells) {
            int x = cell.getCol()*CELL_SIZE + 390;
            int y = cell.getRow()*CELL_SIZE + 85;
            g.drawImage(cell.getImage(),x,y,null);
        }
    }

    private void paintCurrentOne(Graphics g) {
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells) {
            int x = cell.getCol()*CELL_SIZE;
            int y = cell.getRow()*CELL_SIZE;
            g.drawImage(cell.getImage(),x,y,null);

        }
    }

    private void paintWall(Graphics g) {
        for(int i=0;i< wall.length;i++){
            for(int j=0;j<wall[i].length;j++){
                int x = j*CELL_SIZE;
                int y = i*CELL_SIZE;
                Cell cell = wall[i][j];
                //判断当前单元格是否有小方块，如果没有则绘制矩形，否则将小方块嵌入墙中
                if(cell==null){
                    g.drawRect(x,y,CELL_SIZE,CELL_SIZE);
                }else{
                    g.drawImage(cell.getImage(),x,y,null);
                }

            }
        }
    }
    //判断游戏是否出界
    public boolean outOfBounds(){
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells) {
            int col = cell.getCol();
            int row = cell.getRow();
            if(row < 0 || row > wall.length-1 || col < 0 || col > wall[0] .length-1){
                return true;
            }
        }return false;
    }
    //判断方块是否重合
    public boolean coincide(){
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells) {
            int col = cell.getCol();
            int row = cell.getRow();
            if(wall[row][col]!=null){
                return true;
            }
        }return false;
    }
    //按键一次，四方格左移一次
    public void moveLeftAction(){
        currentOne.moveLeft();
        if(outOfBounds()||coincide()){
            currentOne.moveRight();
        }
    }
    //按键一次，四方格右移一次
    public void moveRightAction(){
        currentOne.moveRight();
        if(outOfBounds()||coincide()){
            currentOne.moveLeft();
        }
    }

    public static void main(String[] args) {
        //创建一个窗口对象
        JFrame frame = new JFrame("俄罗斯方块");
        //创建游戏界面，也就是面板
        Tetris panel = new Tetris();
        //将面板嵌入到窗口中
        frame.add(panel);
        //设置可见
        frame.setVisible(true);
        //设置窗口尺寸
        frame.setSize(810,940);
        //设置窗口居中
        frame.setLocationRelativeTo(null);
        //设置窗口关闭的时候，程序终止
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //游戏主要逻辑封装在方法中
        panel.start();
    }
}

