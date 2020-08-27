cd src/utils/applinking_test &&
rm -f ballerina.conf && 
cp ballerina.template.conf ballerina.conf && 
echo "[b7a.observability.choreo.application]" >> ballerina.conf && 
echo $1 >> ballerina.conf && 
java -jar target/bin/test.jar
