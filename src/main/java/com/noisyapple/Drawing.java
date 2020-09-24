package com.noisyapple;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Drawing extends JFrame {

  // private final int[] FIGURE = { 0x9C9C9C9C, 0xC9C9C9C9, 0x63636363,
  // 0x36363636, 0x63636363, 0xC9C9C9C9, 0x9C9C9C9C,
  // 0xC9C9C9C9, 0x63636363, 0x36363636, 0x63636363, 0xC9C9C9C9, 0x9C9C9C9C,
  // 0xC9C9C9C9, 0x63636363 };

  private final int[] FIGURE = { 0x1FF80FE0, 0x1FFC1FF0, 0x01DC1C38, 0x18DE3F18, 0x18CE3B18, 0x19C77338, 0x1F8773F0,
      0x1F03E3E0, 0x0001C000, 0x00008000, 0x001FF800, 0x03FFFFC0, 0x1FFFFFF8, 0x7FFC3FFE, 0xFFE007FF, 0x7FFC3FFE,
      0x1FFFFFF8, 0x03FFFFC0, 0x001FF800 };

  private int UPDATE_RATE = 60;
  private final int MASK = 0x80000000;
  private final int BIT_LENGTH = 32;
  private final int PIXEL_SIZE = 5;

  private int width, height;
  private Color color;

  private int xSpeed = 5;
  private int ySpeed = 5;

  private int xPos = 0;
  private int yPos = 0;
  private int collitions = 0;

  public Drawing(int width, int height) {
    this.width = width;
    this.height = height;
    this.generateColor();

    this.xPos = (width / 2) - (BIT_LENGTH * PIXEL_SIZE / 2);
    this.yPos = (height / 2) - (FIGURE.length * PIXEL_SIZE / 2);

    this.setTitle("Bit Drawing");
    this.setVisible(true);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setResizable(false);

    this.add(new CustomCanvas());

    this.pack();
    this.setLocationRelativeTo(null);

    startLoop();
  }

  public void generateColor() {
    this.color = Color.getHSBColor((float) Math.random(), 0.8f, 0.8f);
  }

  public void startLoop() {
    Thread drawLoop = new Thread() {
      public void run() {
        while (true) {
          repaint();

          // xPos += xSpeed;
          // yPos += ySpeed;

          // if (xPos + BIT_LENGTH * PIXEL_SIZE > width || xPos < 0) {
          // xSpeed *= -1;
          // generateColor();
          // }

          // if (yPos + FIGURE.length * PIXEL_SIZE > height || yPos < 0) {
          // ySpeed *= -1;
          // generateColor();
          // }

          if (collitions < 10) {
            if (xPos + BIT_LENGTH * PIXEL_SIZE > width || xPos < 0 || yPos + FIGURE.length * PIXEL_SIZE > height
                || yPos < 0) {

              System.out.println("Collitions: " + ++collitions);

              generateColor();

              xPos = ((int) Math.floor(Math.random() * width)) - BIT_LENGTH * PIXEL_SIZE;
              yPos = ((int) Math.floor(Math.random() * height)) - FIGURE.length * PIXEL_SIZE;
            }

            xPos += (((int) Math.floor(Math.random() * 2)) == 1) ? xSpeed : -xSpeed;
            yPos += (((int) Math.floor(Math.random() * 2)) == 1) ? ySpeed : -ySpeed;

          } else {
            System.exit(0);
          }

          try {
            Thread.sleep(1000 / UPDATE_RATE);

          } catch (InterruptedException ex) {
          }
        }
      }
    };

    drawLoop.start();
  }

  class CustomCanvas extends JPanel {

    public void paintComponent(Graphics g) {

      g.setColor(Color.decode("#000000"));

      g.fillRect(0, 0, width, height);

      g.setColor(color);

      // System.out.println("X: " + xPos + ", Y: " + yPos + ", X_SPEED: " + xSpeed +
      // ", Y_SPEED: " + ySpeed);

      for (int figure = 0; figure < FIGURE.length; figure++) {
        for (int bit = 0; bit < BIT_LENGTH; bit++) {
          int temp = FIGURE[figure] & (MASK >>> bit);

          if (temp != 0) {
            g.fillRect(bit * PIXEL_SIZE + xPos, figure * PIXEL_SIZE + yPos, PIXEL_SIZE, PIXEL_SIZE);

          }
        }
      }
    }

    public Dimension getPreferredSize() {
      return (new Dimension(width, height));
    }

  }

}