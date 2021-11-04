package demo1;

import java.awt.image.BufferedImage;
import java.util.Objects;

//小方格类
public class Cell {
    //行
    private int row;
    //列
    private int col;
    //小方格图片
    private BufferedImage image;

    public Cell() {
    }

    public Cell(int row, int col, BufferedImage image) {
        this.row = row;
        this.col = col;
        this.image = image;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return row == cell.row && col == cell.col && Objects.equals(image, cell.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col, image);
    }

    @Override
    public String toString() {
        return "demo1.Cell{" +
                "row=" + row +
                ", col=" + col +
                ", image=" + image +
                '}';
    }
    //左移一格
    public void left(){
        col--;
    }
    //右移一格
    public void right(){
        col++;
    }
    //下落一格
    public void drop(){
        row++;
    }
}
