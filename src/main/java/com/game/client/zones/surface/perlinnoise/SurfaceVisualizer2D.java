package com.game.client.zones.surface.perlinnoise;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class SurfaceVisualizer2D extends JPanel {
    private PerlinNoiseSurface3D surface;
    private BufferedImage heightMapImage;

    public SurfaceVisualizer2D(PerlinNoiseSurface3D surface) {
        this.surface = surface;
        this.heightMapImage = createHeightMapImage();
        setPreferredSize(new Dimension(500, 500));
    }

    public static void showSurface(PerlinNoiseSurface3D surface) {
        JFrame frame = new JFrame("3D Surface Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SurfaceVisualizer2D visualizer = new SurfaceVisualizer2D(surface);
        frame.add(visualizer);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private BufferedImage createHeightMapImage() {
        int xSize = surface.getXSize();
        int ySize = surface.getYSize();
        BufferedImage image = new BufferedImage(xSize, ySize, BufferedImage.TYPE_INT_RGB);
        float[][][] heightMap = surface.getHeightMap();

        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
                float height = heightMap[x][y][2];
                Color color = getColorForHeight(height);
                image.setRGB(x, y, color.getRGB());
            }
        }

        return image;
    }

    private Color getColorForHeight(float height) {
        // Градиент от синего (низкие точки) к зеленому (средние) к белому (высокие)
        if (height < -0.5) {
            return new Color(0, 0, 100); // Темно-синий
        } else if (height < 0) {
            return new Color(0, 0, 200); // Синий
        } else if (height < 0.3) {
            return new Color(0, 150, 0); // Зеленый
        } else if (height < 0.6) {
            return new Color(100, 100, 0); // Коричневый
        } else {
            return new Color(255, 255, 255); // Белый
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Масштабируем изображение под размер панели
        int panelSize = Math.min(getWidth(), getHeight());
        g2d.drawImage(heightMapImage, 0, 0, panelSize, panelSize, null);

        // Добавляем сетку
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, panelSize - 1, panelSize - 1);
    }
}
