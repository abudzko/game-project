package com.game.client.zones.surface.perlinnoise;

import java.util.Random;


// Z-UP: Z is a height
public class PerlinNoiseSurface3D {
    // Таблица перестановок для шума Перлина
    private static final int[] perm = new int[512];

    static {
        int[] p = new int[256];
        for (int i = 0; i < 256; i++) p[i] = i;
        // Перемешивание
        Random rand = new Random(0);
        for (int i = 255; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int temp = p[i];
            p[i] = p[j];
            p[j] = temp;
        }

        for (int i = 0; i < 512; i++) {
            perm[i] = p[i & 255];
        }
    }

    private int xSize;
    private int ySize;

    private float[][][] heightMap;
    // Частота/масштаб - контролирует размер "волн"
    private float frequency = 16.0f;
    // Амплитуда - контролирует высоту перепадов
    private float amplitude = 2.0f;
    // Persistence - как уменьшается амплитуда с каждой октавой
    // Меньше = более плавный рельеф, больше = более резкий
    private float persistence = .5f;
    // Количество октав - больше = более детализированный рельеф
    private int octaves = 8;
    // Lacunarity - как увеличивается частота с каждой октавой
    private float lacunarity = 4.0f;

    public PerlinNoiseSurface3D(int xSize, int ySize) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.heightMap = new float[xSize][ySize][3];
    }

    // Генерация поверхности с учетом граничных условий
    public void generateSurface() {
        // Сначала создаем базовый шум Перлина
        float[][] baseNoise = generatePerlinNoise();

        // Создаем 3D координаты
        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
                heightMap[x][y][0] = x; // X координата
                heightMap[x][y][1] = y; // Y координата
                heightMap[x][y][2] = baseNoise[x][y]; // Z координата (высота)
            }
        }
    }

    // Генерация шума Перлина
    private float[][] generatePerlinNoise() {
        float[][] noise = new float[xSize][ySize];

        // Генерируем несколько октав для более естественного вида
        for (int octave = 0; octave < octaves; octave++) {
            float currentFrequency = frequency * (float) Math.pow(lacunarity, octave);
            float currentAmplitude = amplitude * (float) Math.pow(persistence, octave);

            for (int x = 0; x < xSize; x++) {
                for (int y = 0; y < ySize; y++) {
                    float sampleX = x / (float) xSize * currentFrequency;
                    float sampleY = y / (float) ySize * currentFrequency;

                    float perlinValue = improvedPerlinNoise(sampleX, sampleY);
                    noise[x][y] += perlinValue * currentAmplitude;
                }
            }
        }

        // Нормализация значений
        normalizeNoise(noise);

        return noise;
    }

    // Улучшенный шум Перлина
    private float improvedPerlinNoise(float x, float y) {
        int xi = (int) Math.floor(x) & 255;
        int yi = (int) Math.floor(y) & 255;

        float xf = x - (float) Math.floor(x);
        float yf = y - (float) Math.floor(y);

        float u = fade(xf);
        float v = fade(yf);

        int aa = perm[perm[xi] + yi];
        int ab = perm[perm[xi] + yi + 1];
        int ba = perm[perm[xi + 1] + yi];
        int bb = perm[perm[xi + 1] + yi + 1];

        float x1 = lerp(grad(aa, xf, yf), grad(ba, xf - 1, yf), u);
        float x2 = lerp(grad(ab, xf, yf - 1), grad(bb, xf - 1, yf - 1), u);

        return lerp(x1, x2, v);
    }

    // Вспомогательные функции для шума Перлина
    private float fade(float t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private float lerp(float a, float b, float t) {
        return a + t * (b - a);
    }

    private float grad(int hash, float x, float y) {
        switch (hash & 3) {
            case 0:
                return x + y;
            case 1:
                return -x + y;
            case 2:
                return x - y;
            case 3:
                return -x - y;
            default:
                return 0;
        }
    }

    // Нормализация шума в диапазон [-1, 1]
    private void normalizeNoise(float[][] noise) {
        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;

        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
                if (noise[x][y] < min) min = noise[x][y];
                if (noise[x][y] > max) max = noise[x][y];
            }
        }

        float range = max - min;
        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
                noise[x][y] = 2 * ((noise[x][y] - min) / range) - 1;
            }
        }
    }

    public float[][][] getHeightMap() {
        return heightMap;
    }

    public int getXSize() {
        return xSize;
    }

    public int getYSize() {
        return ySize;
    }

}
