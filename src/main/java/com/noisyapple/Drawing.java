package com.noisyapple;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;

// Drawing class.
@SuppressWarnings("serial")
public class Drawing extends JFrame {

  // Graphic hex values.
  private final int[] GRAPHIC = { 0x1FF80FE0, 0x1FFC1FF0, 0x01DC1C38, 0x18DE3F18, 0x18CE3B18, 0x19C77338, 0x1F8773F0,
      0x1F03E3E0, 0x0001C000, 0x00008000, 0x001FF800, 0x03FFFFC0, 0x1FFFFFF8, 0x7FFC3FFE, 0xFFE007FF, 0x7FFC3FFE,
      0x1FFFFFF8, 0x03FFFFC0, 0x001FF800 };

  // Control constants.
  private final int UPDATE_RATE = 60;
  private final int MASK = 0x80000000;
  private final int BIT_LENGTH = 32;
  private final int PIXEL_SIZE = 5;

  // Panel bounds and current color.
  private int width, height;
  private Color color;

  // Position.
  private int xPos = 0;
  private int yPos = 0;

  // Speed.
  private int xSpeed = 5;
  private int ySpeed = 5;

  // Collision accumulator.
  private int collisions = 0;

  // Class constructor.
  public Drawing(int width, int height) {
    this.width = width;
    this.height = height;
    this.generateColor();

    // Graphic's position is set to the center of the panel.
    this.xPos = (width / 2) - (BIT_LENGTH * PIXEL_SIZE / 2);
    this.yPos = (height / 2) - (GRAPHIC.length * PIXEL_SIZE / 2);

    // Window configuration.
    this.setTitle("Bit Drawing");
    this.setVisible(true);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setResizable(false);

    // Panel is added to the window.
    this.add(new CustomCanvas());

    // Window position and size.
    this.pack();
    this.setLocationRelativeTo(null);

    // Loop.
    startLoop();
  }

  // Assigns a new current color based on a random hue from HSB color format.
  public void generateColor() {
    this.color = Color.getHSBColor((float) Math.random(), 0.8f, 0.8f);
  }

  // Loop for rerendering and updating.
  public void startLoop() {
    Thread drawLoop = new Thread() {
      public void run() {
        while (true) {
          repaint();

          // Keep going until 10 collisions happened.
          if (collisions < 10) {

            // Checks whether the graphic's position is within the panel bounds.
            if (xPos + BIT_LENGTH * PIXEL_SIZE > width || xPos < 0 || yPos + GRAPHIC.length * PIXEL_SIZE > height
                || yPos < 0) {

              // Collisions increments.
              System.out.println("Collisions: " + ++collisions);

              // A new color is set.
              generateColor();

              // Random position within the panel bounds.
              xPos = ((int) Math.floor(Math.random() * width)) - BIT_LENGTH * PIXEL_SIZE;
              yPos = ((int) Math.floor(Math.random() * height)) - GRAPHIC.length * PIXEL_SIZE;
            }

            // Random walk algorithm.
            xPos += (((int) Math.floor(Math.random() * 2)) == 1) ? xSpeed : -xSpeed;
            yPos += (((int) Math.floor(Math.random() * 2)) == 1) ? ySpeed : -ySpeed;

          } else {
            System.exit(0); // Otherwise stop the program's execution.
          }

          try {
            Thread.sleep(1000 / UPDATE_RATE); // Desired frame rate.

          } catch (InterruptedException ex) {
          }
        }
      }
    };

    // Thread execution.
    drawLoop.start();
  }

  // CustomCanvas inner class.
  class CustomCanvas extends JPanel {

    // Overwritten method paintComponent.
    public void paintComponent(Graphics g) {

      // Background gets drawn.
      g.setColor(Color.decode("#000000"));
      g.fillRect(0, 0, width, height);

      // Current color is set to the graphic context.
      g.setColor(color);

      // Collision indicator.
      g.drawString("COLLISIONS AMOUNT: " + collisions, 10, 10);

      // Image gets drawn pixel by pixel.
      for (int graphic = 0; graphic < GRAPHIC.length; graphic++) {
        for (int bit = 0; bit < BIT_LENGTH; bit++) {
          int temp = GRAPHIC[graphic] & (MASK >>> bit);

          if (temp != 0) {
            // Each pixel is scaled based on PIXEL_SIZE value.
            g.fillRect(bit * PIXEL_SIZE + xPos, graphic * PIXEL_SIZE + yPos, PIXEL_SIZE, PIXEL_SIZE);
          }
        }
      }
    }

    // Overwriten method getPreferredSize for window bounds to fit panel size.
    public Dimension getPreferredSize() {
      return (new Dimension(width, height));
    }

  }

}