package edu.nyu.crypto.csci3033.miners;

import edu.nyu.crypto.csci3033.blockchain.NetworkStatistics;
import edu.nyu.crypto.csci3033.blockchain.Block;

public class FeeSnipingMiner extends BaseMiner implements Miner {
    private Block currentHead;
    private Block otherHead;
    private float totalHashRate;
    private boolean fork;
    private boolean debug = false;
    private double fork2Reward;
    private int stop = 0;
    private int advance = 0;

    public FeeSnipingMiner(String id, int hashRate, int connectivity) {
        super(id, hashRate, connectivity);
    }

    @Override
    public Block currentlyMiningAt() {
        return currentHead;
    }

    @Override
    public Block currentHead() {
        return currentHead;
    }

    @Override
    public void blockMined(Block block, boolean isMinerMe) {
        if(isMinerMe) {
            if (block.getHeight() > currentHead.getHeight()) {
                this.currentHead = block;
                advance = currentHead.getHeight() - otherHead.getHeight();
                if (debug) {
                    System.out.print("Block mined: ");
                    System.out.println(block);
                    System.out.print("Block reward: ");
                    System.out.println(block.getBlockValue());
                    System.out.print("Current advance: ");
                    System.out.println(advance);
                    System.out.println();
                }
                stop++;
            }
        }
        else{
           if (block.getHeight() > otherHead.getHeight()) {
               stop++;
               otherHead = block;
               advance = currentHead.getHeight() - otherHead.getHeight();
               if (debug) {
                   System.out.print("Block mined: ");
                   System.out.println(block);
                   System.out.print("Block reward: ");
                   System.out.println(block.getBlockValue());
                   System.out.print("Current advance: ");
                   System.out.println(advance);
               }
                if (!fork) {
                    fork2Reward = Math.pow(getHashRate() / totalHashRate, 2) * block.getBlockValue();
                    if (debug) {
                        System.out.print("Fork 2 reward: ");
                        System.out.println(fork2Reward);
                    }

                    if (fork2Reward > 1.5) {
                        // Should we fork longer?
                        // 1 is ignoring potential rewards from not forking, so we need to choose something higher
                        // To simplify we give up if we are more than one block behind.
                        // Optimal behaviour would be to adapt that to the reward.
                        fork = true;
                        if (debug) {
                            System.out.println("Activated forking!");
                            System.out.println();
                        }

                    }

                    else {
                        fork = false;
                        this.currentHead = block;
                        if (debug) {
                            System.out.println("Too low to fork");
                            System.out.println();
                        }
                    }
                }

                else {
                   if (advance < -1) {
                       if (debug) {
                           System.out.println("Lost race, resetting...");
                           System.out.println();
                       }
                       fork = false;
                       currentHead = otherHead;

                   }
                   else {
                       if (debug) {
                           System.out.println();
                       }
                   }

                }

            }
        }

        if (stop > 60 && debug) {
            System.exit(0);
        }
    }


    @Override
    public void initialize(Block genesis, NetworkStatistics networkStatistics) {
        fork2Reward = 0;
        currentHead = genesis;
        totalHashRate = 1000;
        otherHead = genesis;
        fork = false;
        advance = 0;
    }

    @Override
    public void networkUpdate(NetworkStatistics statistics) {
        totalHashRate = statistics.getTotalHashRate();
    }
}
