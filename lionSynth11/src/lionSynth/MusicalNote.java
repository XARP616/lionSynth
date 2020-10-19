package lionSynth;

public enum MusicalNote {
	C ("C", 523.25, 554.37),
	D ("D", 587.33, 622.25),
	E ("E", 659.25, 698.46), // E5# = F5
	F ("F", 698.46, 739.99),
	G ("G", 783.99, 830.61),
	A ("A", 880.00, 932.33),
	B ("B", 987.77, 1046.5), // B5# = C6
	
	DO (C, "Do"),
	RE (D, "Re"),
	MI (E, "Mi"),
	FA (F, "Fa"),
	SOL(G, "Sol"),
	LA (A, "La"),
	SI (B, "Si");
	
	private final String name;
	private final String nameSharp;
	private final double hz;
	private final double hzSharp;
	
	MusicalNote (String name, double hz, double hzSharp) {
		this.name = name;
		this.nameSharp = name + "#";
		this.hz = hz;
		this.hzSharp = hzSharp;
	}
	MusicalNote (MusicalNote m, String name){
		this.name = name;
		this.nameSharp = name + "#";
		this.hz = m.hz;
		this.hzSharp = m.hzSharp;
	}
	
	double getHz() {
		return this.hz;
	}
	
	double getHzSharp() {
		return this.hzSharp;
	}
	
	String getName() {
		return this.name;
	}
	
	String getNameSharp() {
		return this.nameSharp;
	}
}
