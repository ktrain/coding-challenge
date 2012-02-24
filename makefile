default: Match.java
	@echo "Compiling Match ..."
	javac Match.java
	@echo ""
	@echo "If there are warnings, they're from org.json ..."
	@echo ""

sort: results.txt
	sort results.txt > sorted.txt

clean:
	@echo "Cleaning ..."
	cd org/json/ && $(RM) *.class
	$(RM) Match.class results.txt sorted.txt
	@echo ""
	@echo "Squeaky."
	@echo ""

new:
	@make clean
	@make
