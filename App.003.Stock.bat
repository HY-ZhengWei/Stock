

cd bin

rd /s/q .\com\hy\stock\junit

jar cvfm hy.Stock MANIFEST.MF LICENSE com

copy hy.Stock ..\lib\hy.Stock
del /q hy.Stock
cd ..
