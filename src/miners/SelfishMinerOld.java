package edu.nyu.crypto.csci3033.miners;

import edu.nyu.crypto.csci3033.blockchain.NetworkStatistics;
import edu.nyu.crypto.csci3033.blockchain.Block;

public class SelfishMinerOld extends BaseMiner implements Miner {
    private Block currentHead;
    private int advance;
    private int maxHeight;
    private boolean announce;
    private Block intermediary;
    private Block lie;
    private int stop;
    private boolean debug;

    public SelfishMinerOld(String id, int hashRate, int connectivity) {
        super(id, hashRate, connectivity);
        advance = 0;
        maxHeight = 0;
        announce = false;
        stop = 0;
        debug = true;
    }

    @Override
    public Block currentlyMiningAt() {
        return currentHead;
    }

    @Override
    public Block currentHead() {
        if (announce) {
            if (advance <= 1) {
                return currentHead;
            }
            else {
                intermediary = currentHead;
                for (int i = advance; i > 1; i--) {
                    intermediary = intermediary.getPreviousBlock();
                }
                return intermediary;
            }
        }
        else {
            return lie;
        }
    }

    @Override
    public void blockMined(Block block, boolean isMinerMe) {
        if (debug) {
            if (((block.getHeight() > currentHead.getHeight()) && isMinerMe) || ((block.getHeight() > maxHeight) && !isMinerMe)) {
                System.out.print("Round: ");
                System.out.println(stop);
                System.out.print("Mined block: ");
                System.out.println(block);
                System.out.print("Builds on top of: ");
                System.out.println(block.getPreviousBlock());
                System.out.print("Head before: ");
                System.out.println(currentHead);

                if (stop == 1000) {
                    System.exit(0);
                }
                stop++;
            }
        }

        if(isMinerMe && (block.getHeight() > currentHead.getHeight())) {
            announce = false;
            currentHead = block;
            advance = currentHead.getHeight() - maxHeight;
            if (debug) {
                System.out.print("Current advance: ");
                System.out.println(advance);
                System.out.println();
            }
        }

        else if (!isMinerMe && (block.getHeight() > maxHeight)){
            maxHeight = block.getHeight();
            if (debug) {
                System.out.println("Ennemy making progress, announcing");
            }
            announce = true;
            lie = block;
            if (block.getHeight() > currentHead.getHeight()) {
                if (debug) {
                    System.out.println("Ennemy in front, switching chain");
                }
                this.currentHead = block;
            }
            advance = this.currentHead.getHeight() - maxHeight;
            if (debug) {
                System.out.print("Current advance: ");
                System.out.println(advance);
                System.out.println();
            }
        }
    }


    @Override
    public void initialize(Block genesis, NetworkStatistics networkStatistics) {
        currentHead = genesis;
        lie = genesis;
        announce = false;
        maxHeight = 0;
        advance = 0;
    }

    @Override
    public void networkUpdate(NetworkStatistics statistics) {

    }
}
