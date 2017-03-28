

cd bin

rd /s/q .\com\hy\stock\junit

jar cvfm hy.Stock.jar MANIFEST.MF LICENSE com

copy hy.Stock.jar ..\lib
del /q hy.Stock.jar
cd ..
