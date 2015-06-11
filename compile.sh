javac -cp .:gson-2.2.4.jar:poi-3.12-20150511.jar:mysql-connector-java-5.1.35-bin.jar MysqlToXls.java
rm -rf mysql/MysqlToXls.class
mv MysqlToXls.class mysql
