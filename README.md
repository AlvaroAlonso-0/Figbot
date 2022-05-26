# Figbot

Como compilar un agente (Agente1.java):
javac -classpath lib/jade.jar -d bin src/agents/*.java

Como ejecutar jade con ese agente:
java -cp "lib/jade.jar;bin" jade.Boot -gui -agents agente1:agents.Agent1