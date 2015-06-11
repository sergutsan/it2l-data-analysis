rm -rf src/db/MysqlToXls.class src/db/ConnectionData.class
javac -cp .:gson-2.2.4.jar:poi-3.12-20150511.jar:mysql-connector-java-5.1.35-bin.jar src/db/MysqlToXls.java
rm -rf bin
mkdir -p bin/db
mv src/db/MysqlToXls.class src/db/ConnectionData.class bin/db
