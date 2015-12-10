----------------------------------------------------------
1. Create jhm from Maven archetype:
----------------------------------------------------------

mvn archetype:generate -DinteractiveMode=false -DarchetypeGroupId=org.openjdk.jmh -DarchetypeArtifactId=jmh-java-benchmark-archetype -DgroupId=org.sample -DartifactId=jhm-tests -Dversion=1.0

----------------------------------------------------------
2. Compile
----------------------------------------------------------
cd jhm-tests
mvn clean install

----------------------------------------------------------
3. Run benchmarks
----------------------------------------------------------
java -jar target/benchmarks.jar

----------------------------------------------------------
3. Samples
----------------------------------------------------------
http://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/





