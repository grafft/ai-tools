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
        nextFigure.setyCoord(1);
    }

    public synchronized void tick() {
        if (currentFigure != null && !checkBottom()) {
            currentFigure.setyCoord(currentFigure.getyCoord() + 1);
        } else {
            currentFigure = nextFigure;
            currentFigure.setxCoord((X - currentFigure.getxSize()) / 2);
            currentFigure.setyCoord(0);

            nextFigure = startFigures.get(RandomUtils.nextInt(startFigures.size())).clone();
            nextFigure.setColorIndex(RandomUtils.nextInt(COLOR_COUNT));
            nextFigure.setxCoord((PREVIEW_SIZE - nextFigure.getxSize()) / 2);
            nextFigure.setyCoord(1);
        }

    }

    public synchronized boolean moveFigure(int deltaX, int deltaY) {
        if (currentFigure == null ||
                checkBottom() ||
                intersectBounds(deltaX, deltaY) ||
                intersectStaticBlocks(deltaX, deltaY)) {
            return false;
        } else {
            currentFigure.setxCoord(currentFigure.getxCoord() + deltaX);
            currentFigure.setyCoord(currentFigure.getyCoord() + deltaY);
            return true;
        }
    }

    private boolean checkBottom() {
        if (currentFigure.getyCoord() + currentFigure.getySize() == Y) {
            addFigureToStatic();
            return true;
        }
        for (Block block : staticBlocks.getBlockSet()) {
            if (currentFigure.blockExist(block.getxCoord() - currentFigure.getxCoord(), block.getyCoord() - currentFigure.getyCoord() - 1)) {
                addFigureToStatic();
                return true;
            }
        }
        return false;
    }

    private void addFigureToStatic() {
        for (Block figureBlock : currentFigure.getBlocks()) {
            figureBlock.setxCoord(figureBlock.getxCoord() + currentFigure.getxCoord());
            figureBlock.setyCoord(figureBlock.getyCoord() + currentFigure.getyCoord());
            staticBlocks.addBlock(figureBlock);
        }
        currentFigure = null;
        int collapsed = staticBlocks.collapseLines(X);
        if (collapsed > 0) {
            score += 100 * collapsed;
        }
    }

    private boolean intersectStaticBlocks(int deltaX, int deltaY) {
        for (Block block : staticBlocks.getBlockSet()) {
            if (currentFigure.blockExist(block.getxCoord() - currentFigure.getxCoord() - deltaX,
                    block.getyCoord() - currentFigure.getyCoord() - deltaY)) {
                return true;
            }
        }
        return false;
    }

    private boolean intersectBounds(int deltaX, int deltaY) {
        return currentFigure.getxCoord() + deltaX < 0 || currentFigure.getxCoord() + currentFigure.getxSize() + deltaX > X ||
                currentFigure.getyCoord() + currentFigure.getySize() + deltaY > Y;
    }

    public synchronized void rotateFigure() {
        if (currentFigure != null) {
            currentFigure.rotate();
        }
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
