# mvn 打包 
mvn clean package -Dmaven.test.skip=true -Dfile.encoding=UTF-8 -Pprod
# window mvn 打包 
mvn clean package '-Dmaven.test.skip=true' '-Dfile.encoding=UTF-8' -Pprod