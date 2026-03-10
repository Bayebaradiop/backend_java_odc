package com.medibook.common.config.seeder;

public interface Seeder {
    String getName();
    int getOrder();
    void run();
    boolean shouldRun();
    
    default int defaultOrder() {
        return 100;
    }
}
