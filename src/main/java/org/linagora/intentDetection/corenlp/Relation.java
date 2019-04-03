package org.linagora.intentDetection.corenlp;

public class Relation {
	
	private Token governor = null;
	private Token dependent = null;
	private String name = null;
	private Direction direction = null;
	
	public Relation(){
		
	}
	
	public Relation(Token governor, Token dependent, String name, Direction direction) {
		super();
		this.governor = governor;
		this.dependent = dependent;
		this.name = name;
		this.direction = direction;
	}


	public Token getGovernor() {
		return governor;
	}

	public void setGovernor(Token governor) {
		this.governor = governor;
	}

	public Token getDependent() {
		return dependent;
	}

	public void setDependent(Token dependent) {
		this.dependent = dependent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	@Override
	public String toString() {
		return "Relation [governor=" + governor.getText() + ", dependent=" + dependent.getText() + ", name=" + name + ", direction=" + direction
				+ "]";
	}
	
	@Override
	public boolean equals(Object object2) {
		if(this.governor == ((Relation)object2).governor
				&& this.dependent == ((Relation)object2).dependent
				&& this.name.equals(((Relation)object2).name)
				&& this.direction == ((Relation)object2).direction) {
			return true;
		}
		
		return false;
	}
	
	
}
