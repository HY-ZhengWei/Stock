cd %STOCK_HOME%

SET CLASSPATH=%CLASSPATH%;..\hy.common.base\hy.common.base.jar;..\hy.common.db\hy.common.db.jar;..\hy.common.file\hy.common.file.jar;.\lib\xjava.jar;..\hy.common.tpool\hy.common.tpool.jar;..\hy.common.mail\hy.common.mail.jar;.\lib\hy.Stock.jar;.\lib\c3p0-0.9.2-pre5.jar;.\lib\json-smart-1.1.1.jar;.\lib\mchange-commons-java-0.2.3.jar;.\lib\mysql-connector-java-5.1.30-bin.jar;.\lib\servlet-api.jar;.\lib\javax.mail.jar;.\lib\fel.jar;.\lib\junit-4.11.jar


java -Dfile.encoding=UTF-8 com.hy.stock.Stock
