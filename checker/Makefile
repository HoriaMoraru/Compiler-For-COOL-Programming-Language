ANTLR=../antlr-4.13.0-complete.jar
CLASSPATH=.:$(ANTLR)
SHELL = /bin/bash

build:
	java -jar $(ANTLR) cool/lexer/CoolLexer.g4
	java -jar $(ANTLR) -lib cool/lexer -listener -visitor cool/parser/CoolParser.g4

compiler:
	javac -cp $(CLASSPATH) cool/compiler/Compiler.java

run:
	./tester.sh

clean:
	@find . -name '*.class' -exec rm -rf {} \;
	@bash -c $$'cd cool; shopt -s extglob\nrm -rf !(tester)'
