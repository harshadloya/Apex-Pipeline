package edu.bing.beans;

public class Instruction {

	private String instr_type;
	private String src1;
	private String src2;
	private String src3;
	private String dest;
	private int literal;
	
	public String getInstr_type() {
		return instr_type;
	}
	public void setInstr_type(String instr_type) {
		this.instr_type = instr_type;
	}
	public String getSrc1() {
		return src1;
	}
	public void setSrc1(String src1) {
		this.src1 = src1;
	}
	public String getSrc2() {
		return src2;
	}
	public void setSrc2(String src2) {
		this.src2 = src2;
	}
	
	public String getSrc3() {
		return src3;
	}
	public void setSrc3(String src3) {
		this.src3 = src3;
	}
	public String getDest() {
		return dest;
	}
	public void setDest(String dest) {
		this.dest = dest;
	}
	public int getLiteral() {
		return literal;
	}
	public void setLiteral(int literal) {
		this.literal = literal;
	}
	@Override
	public String toString() {
		return "Instruction [instr_type=" + instr_type + ", src1=" + src1 + ", src2=" + src2 + ", src3=" + src3
				+ ", dest=" + dest + ", literal=" + literal + "]";
	}
	
	
}
