default:
	javac Match.java

clean:
	rm -f Match.class

new:
	make clean
	make
