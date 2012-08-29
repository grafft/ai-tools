package ru.isa.ai.tests.tetris;

import org.apache.commons.lang.math.RandomUtils;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Author: Aleksandr Panov
 * Date: 27.08.12
 * Time: 9:21
 */
public class Situation {
    public static final int X = 14;
    public static final int Y = 20;
    public static final int PREVIEW_SIZE = 6;
    public static final int COLOR_COUNT = 7;
    public static final Color[] COLORS = new Color[]{Color.red, Color.blue, Color.gray, Color.green, Color.orange, Color.pink, Color.yellow};

    private Figure currentFigure;
    private Figure nextFigure;
    private List<Figure> startFigures = new ArrayList<>();
    private StaticBlocks staticBlocks;

    private int score = 0;

    public Situation(Map<Integer, Figure> startFigures) {
        this.startFigures.addAll(startFigures.values());
        staticBlocks = new StaticBlocks();
        nextFigure = startFigures.get(RandomUtils.nextInt(startFigures.size())).clone();
        nextFigure.setColorIndex(RandomUtils.nextInt(COLOR_COUNT));
        nextFigure.setxCoord((PREVIEW_SIZE - nextFigure.getxSize()) / 2);
        nextFigure.setyCoord((PREVIEW_SIZE - nextFigure.getySize()) / 2);
    }

    public synchronized void tick() {
        if (currentFigure != null && !checkBottom(currentFigure)) {
            currentFigure.setyCoord(currentFigure.getyCoord() + 1);
        } else {
            currentFigure = nextFigure;
            currentFigure.setxCoord((X - currentFigure.getxSize()) / 2);
            currentFigure.setyCoord(0);

            nextFigure = startFigures.get(RandomUtils.nextInt(startFigures.size())).clone();
            nextFigure.setColorIndex(RandomUtils.nextInt(COLOR_COUNT));
            nextFigure.setxCoord((PREVIEW_SIZE - nextFigure.getxSize()) / 2);
            nextFigure.setyCoord((PREVIEW_SIZE - nextFigure.getySize()) / 2);
        }

    }

    public synchronized boolean moveFigure(int deltaX, int deltaY) {
        if (currentFigure != null) {
            Figure test = currentFigure.clone();
            test.setxCoord(test.getxCoord() + deltaX);
            test.setyCoord(test.getyCoord() + deltaY);
            if ((deltaY != 0 && checkBottom(currentFigure)) ||
                    intersectBounds(test) ||
                    intersectStaticBlocks(test)) {
                return false;
            } else {
                currentFigure.setxCoord(currentFigure.getxCoord() + deltaX);
                currentFigure.setyCoord(currentFigure.getyCoord() + deltaY);
                return true;
            }
        } else {
            return false;
        }
    }

    public synchronized void rotateFigure() {
        if (currentFigure != null) {
            Figure test = currentFigure.clone();
            test.rotate();
            if (!intersectBounds(test) && !intersectStaticBlocks(test)) {
                currentFigure.rotate();
            }
        }
    }

    private boolean checkBottom(Figure figure) {
        if (figure.getyCoord() + figure.getySize() == Y) {
            addFigureToStatic(currentFigure);
            currentFigure = null;
            return true;
        }
        for (Block block : staticBlocks.getBlockSet()) {
            if (figure.blockExist(block.getxCoord() - figure.getxCoord(), block.getyCoord() - figure.getyCoord() - 1)) {
                addFigureToStatic(currentFigure);
                currentFigure = null;
                return true;
            }
        }
        return false;
    }

    private void addFigureToStatic(Figure figure) {
        for (Block figureBlock : figure.getBlocks()) {
            figureBlock.setxCoord(figureBlock.getxCoord() + figure.getxCoord());
            figureBlock.setyCoord(figureBlock.getyCoord() + figure.getyCoord());
            staticBlocks.addBlock(figureBlock);
        }
        int collapsed = staticBlocks.collapseLines(X);
        if (collapsed > 0) {
            score += 100 * collapsed;
        }
    }

    private boolean intersectStaticBlocks(Figure figure) {
        for (Block block : staticBlocks.getBlockSet()) {
            if (figure.blockExist(block.getxCoord() - figure.getxCoord(),
                    block.getyCoord() - figure.getyCoord())) {
                return true;
            }
        }
        return false;
    }

    private boolean intersectBounds(Figure figure) {
        return figure.getxCoord() < 0 || figure.getxCoord() + figure.getxSize() > X ||
                figure.getyCoord() + figure.getySize() > Y;
    }

    public synchronized Figure getCurrentFigure() {
        return currentFigure;
    }

    public synchronized Set<Block> getStaticBlocks() {
        return staticBlocks.getBlockSet();
    }

    public Figure getNextFigure() {
        return nextFigure;
    }

    public int getScore() {
        return score;
    }
}
