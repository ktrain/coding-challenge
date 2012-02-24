default: Match.java
	@echo "Compiling Match ..."
	javac -Xlint:unchecked Match.java
	@echo ""
	@echo "If there are warnings, they're from org.json ..."
	@echo ""

sort: results.txt
	sort results.txt > sorted.txt

clean:
	@echo "Cleaning ..."
	rm -f Match.class results.txt
	@echo ""
	@echo "Squeaky."
	@echo ""

new:
	@make clean
	@make
