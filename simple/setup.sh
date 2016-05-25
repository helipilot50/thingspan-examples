objy startlockserver
objy exportSchema -outFile simpleSchema.xml -over -bootfile data/simple.boot
objy deletefd -bootfile data/simple.boot
objy createfd -fdname simple -fdDirPath data
