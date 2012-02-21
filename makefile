default: Match.java
	@echo "Compiling Match ..."
	javac Match.java
	@echo "The warnings are from org.json ..."

clean:
	@echo "Cleaning ..."
	rm -f Match.class results.txt
	@echo ""
	@echo "Squeaky."
	@echo ""

new:
	@make clean
	@make
