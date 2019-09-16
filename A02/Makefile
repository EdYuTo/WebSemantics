LIB="jena/lib/*;."
JAVAC=javac -cp $(LIB)
JAVA=java -cp $(LIB)

all:
	@$(JAVAC) Simpsons.java
	@$(JAVA) Simpsons simpsons.ttl output.ttl

clean:
	@find -name 'Simpsons.class' | xargs rm -rf {}
	@find -name 'output.ttl' | xargs rm -rf {}