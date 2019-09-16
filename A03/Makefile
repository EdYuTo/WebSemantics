LIB="jena/lib/*;."
JAVAC=javac -cp $(LIB)
JAVA=java -cp $(LIB)

target?=

all:
	$(if $(target), @$(JAVAC) query.java)
	$(if $(target), @$(JAVA) query simpsons.ttl $(target), @echo "Usage: make target=exercise_X.rq")

clean:
	@find -name 'query.class' | xargs rm -rf {}
	@find -name 'output.ttl' | xargs rm -rf {}