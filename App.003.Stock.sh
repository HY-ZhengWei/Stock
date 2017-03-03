#!/bin/sh

cd bin

rm -R ./com/hy/stock/junit

jar cvfm hy.Stock.jar MANIFEST.MF LICENSE com

cp hy.Stock.jar ../lib/hy.Stock.jar
rm hy.Stock.jar
cd ..
