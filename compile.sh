rm -rf src/db/MysqlToXls.class src/db/ConnectionData.class
rm -rf bin
mkdir -p bin/db
javac -cp .:gson-2.2.4.jar:poi-3.12-20150511.jar:mysql-connector-java-5.1.35-bin.jar src/db/DbConnection.java
mv src/db/DbConnection.class src/db/ConnectionData.class bin/db
javac -cp .:bin:gson-2.2.4.jar:poi-3.12-20150511.jar:mysql-connector-java-5.1.35-bin.jar src/db/MysqlToXls.java
mv src/db/MysqlToXls.class bin/db
