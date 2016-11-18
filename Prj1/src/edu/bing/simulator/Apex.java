package edu.bing.simulator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import edu.bing.beans.Instruction;
import edu.bing.beans.Register;
import edu.bing.loader.InstructionLoader;


public class Apex {

	boolean initialized = false; 
	private static int MEM_LENGTH = 10000;
	int PC_value = 4000;
	int result_inEx1ALU2, result_inEx1ALU1, result_inEx2ALU2, result_inEx2ALU1, result_inWb;
	int result_inMem;
	int literal_zero = 0000; 
	int offset = 0;
	HashMap<Integer, Object> memory = new HashMap <Integer, Object> ();

	Scanner sc = new Scanner(System.in);
	static ArrayList<Instruction> instructionsToProcess = new ArrayList<Instruction>();
	boolean wb,mem, ex1a1, ex1a2, ex2a1, ex2a2, decode, fetch, branch, halt, set_exStage2, set_exStage1,jump_flag, branch_instr, isDependent;
	boolean stall_fetch, stall_decode, stall_exstage, stall_mem, stall_wb;
	Instruction inFetch, inDecode, inEx1ALU1, inEx1ALU2, inEx2ALU1, inEx2ALU2, inMem, inWb;
	//Commenting as we may not need it
	//List<Instruction> pipelineValues = new ArrayList<Instruction>();

	HashMap<String, Register> registerFile = new HashMap<String, Register>();


	void operations() throws IOException
	{
		while(true)
		{
			System.out.println("Select an Option for Simulator: ");
			System.out.println("1. Initialise \n2. Simulate\n3. Display\n4. Help\n5. Exit");
			int choice=sc.nextInt();
			switch(choice)
			{
			case 1:
				initialize();
				break;
			case 2:
				simulate();
				break;
			case 3:
				display();
				break;
			case 4:
			case 5: System.exit(0);
			}
		}
	}

	void initialize()
	{
		initialized = true;
		System.out.println("Initialized...");
		for (int i = 0; i < MEM_LENGTH; i++){
			memory.put(i, null);
		}

		for (int n=0; n<16; n++)
		{
			Register r = new Register();
			r.setReg_name("R"+n);
			r.setStatus(0);
			registerFile.put("R"+n, r);
		}
		Register r = new Register();
		r.setReg_name("X");
		registerFile.put("X", r);
	}

	void simulate() throws IOException
	{
		if(!initialized){
			System.out.println("Please initialize the simulator");
		}
		else
		{
			InstructionLoader isl = new InstructionLoader("./Instructions.txt");
			ArrayList<Instruction> instructionsToProcess = new ArrayList<Instruction>();
			instructionsToProcess = (ArrayList<Instruction>) isl.loadInstructions();
			int i= PC_value,j=0;
			for (j=0; j < instructionsToProcess.size(); j++){
				memory.put(i, instructionsToProcess.get(j));
				i+=4;				
			}

//			System.out.println("Enter the Number of Cycles for Simulation"); // commenting as confusing 
			//int numberOfCycles = sc.nextInt();			//Actual Code Line
			int numberOfCycles = instructionsToProcess.size()+10;				//Temp Code Line +10 is just a random number
			for(int n=0; n<numberOfCycles; n++)
			{
				if(!(fetch&&decode&&ex1a1&&ex1a2&&ex2a1&&ex2a2&&mem&&wb)) //iff all stages are empty
				{
					wbStage(n);	
				}
			}
		}
	}


	void wbStage(int n)
	{
		wb = true;
		if(mem)
		{
			mem=false;
			inWb = inMem;
			result_inWb = result_inMem;
			//pipelineValues.add(5, inWb);
			registerFile.get(inWb.getDest()).setReg_value(result_inMem);
			registerFile.get(inWb.getDest()).setStatus(0);		//just added this line, nothing to do with it
			System.out.println(registerFile.get(inWb.getDest()));
			wb=false;
			memStage(n);
		}
		else
		{
			memStage(n);
			}
	}
	

	void memStage(int n)
	{

		if(ex1a2)
		{
			mem = true;
			ex1a2 = false;
			inMem = inEx1ALU2;
			result_inMem = result_inEx1ALU2;
			//pipelineValues.add(4, inMem);
			exStage(n);
		}
		else
		{
			mem = false;
				exStage(n);
		}
	}

	void exStage(int n){//this will ensure that both EX stages are called in one cycle
						//either this or we can also put them together in single function
		
		exStage1ALU2(n);
		exStage1ALU1(n);
		
		exStage2ALU2(n);
		exStage2ALU1(n);
		
		decodeStage(n);
	}
	
	void exStage1ALU2(int n)
	{
		if(ex1a1)
		{//LOAD and STORE are added in decode, need to write in EX
			ex1a2 = true;
			ex1a1=false;
			set_exStage1 = false;
			inEx1ALU2 = inEx1ALU1;
			switch (inEx1ALU2.getInstr_type()){
			case "ADD":
				result_inEx1ALU2 = inEx1ALU2.getSrc1_value() + inEx1ALU2.getSrc2_value();

				break;
			case "SUB":
				result_inEx1ALU2 = inEx1ALU2.getSrc1_value() - inEx1ALU2.getSrc2_value();

				break;
			case "MUL":
				result_inEx1ALU2 = inEx1ALU2.getSrc1_value() * inEx1ALU2.getSrc2_value();

				break;
			case "MOVC":
				result_inEx1ALU2 = inEx1ALU2.getSrc1_value() + literal_zero;
				break;
				
			case "HALT":
				ex1a2= false;
				break;
				
			}
		}
		else {
			ex1a2 = false;
		}

	}

	void exStage1ALU1(int n)
	{
		if(decode && !branch_instr)
		{
			ex1a1 = true;
			decode = false;
			inEx1ALU1 = inDecode;
		}
		else
		{
			ex1a1 = false;
		}
	}

	void exStage2ALU2(int n)
	{
		if(ex2a1)
		{
			ex2a1=false;
			set_exStage2 = false;
			inEx2ALU2 = inEx2ALU1;
			if(branch){
				branch=false;
				jump_flag = true;
				ex2a2=ex2a1=ex1a1=ex1a2=false;//flush instructions in EX stages
			}
		}
		else
			ex2a1 = false;


	}

	void exStage2ALU1(int n)
	{
		
		if(decode && branch_instr)
		{
			ex2a1 = true;
			decode = false;
			inEx2ALU1 = inDecode;
			branch_instr = false;
			switch(inEx2ALU1.getInstr_type()){
			case "BZ":
				if(result_inEx1ALU2==0){ // assuming forwarding 
					PC_value = PC_value - 8 + inEx2ALU1.getLiteral();//updated PC value
					System.out.println("PC value updated: "+PC_value);
					branch = true;
					break;
				}
			case "JUMP":
				System.out.println("PC value before: "+PC_value);
				PC_value = PC_value - 8 + inEx2ALU1.getLiteral();
				System.out.println("PC value updated: "+PC_value);
				branch=true;
				break;
				
		case "BNZ":
			if(result_inEx1ALU2!=0){ // assuming forwarding 
				PC_value = PC_value - 8 + inEx2ALU1.getLiteral();//updated PC value, please cross check destination address calculation
				System.out.println("PC value updated: "+PC_value);
				branch = true;
				break;
			}
			}
		}
		else
			ex2a1 = false;
		}

	

	void decodeStage(int n)
	{
		if(stall_decode || jump_flag){
			if (stall_decode)
				stall_fetch = true;
			decode = false;
			fetchStage(n);
		}
		else
			if(fetch)
		{
			decode = true;
			int source1,source2,source3,literal;
			Instruction previousInstruction = null;
			previousInstruction = inDecode;
			inDecode = inFetch;
			if(checkDependency(inDecode, previousInstruction)){//just added this line, nothing to do with it
				isDependent = true;
				stall_decode = true;
			}
			switch(inDecode.getInstr_type()){
			case "ADD":
			case "SUB":
			case "MUL":
			case "DIV":
			case "LOAD":
				if(registerFile.get(inDecode.getSrc1()).getStatus()==1){//dependency scenario yet to be finalized
					System.out.println("Destination register busy.");
					stall_fetch = true;
					stall_decode = true;
					break;
				}
				else if(inDecode.getSrc2()!=null){
					if(registerFile.get(inDecode.getSrc2()).getStatus()==1){
						System.out.println("Destination register busy.");
						stall_fetch = true;
						stall_decode = true;
						break;
					}
				}
				else{
				source1 = registerFile.get(inDecode.getSrc1()).getReg_value();
				inDecode.setSrc1_value(source1);
				if (inDecode.getSrc2()!=null){
					source2 = registerFile.get(inDecode.getSrc2()).getReg_value();
					inDecode.setSrc2_value(source2);
				}
				else {
					literal = inDecode.getLiteral();
					inDecode.setSrc2_value(literal);
				}
				branch_instr = false;
				set_exStage1 = true;
				registerFile.get(inDecode.getDest()).setStatus(1);
				break;
				}
			case "BZ":
			case "BNZ":
			case "JUMP":
			case "BAL":
				literal = inDecode.getLiteral();
				inDecode.setSrc1_value(literal);
				set_exStage2 = true;
				branch_instr = true;				
				break;
			case "MOVC":
				if(registerFile.get(inDecode.getDest()).getStatus()==1){
					System.out.println("Destination register busy in MOVC.");
					stall_fetch = true;
					stall_decode = true;
//					decode=false;
					break;
				}
				else{
				source1 = inDecode.getLiteral();
				inDecode.setSrc1_value(source1);
				set_exStage1 = true;
				branch_instr = false;
				registerFile.get(inDecode.getDest()).setStatus(1);
				break;
				}
			case "STORE":
				
				source1 = registerFile.get(inDecode.getSrc1()).getReg_value();
				inDecode.setSrc1_value(source1);
				source3 = registerFile.get(inDecode.getSrc3()).getReg_value();
				inDecode.setSrc3_value(source3);
				if (inDecode.getSrc2()!=null){
					source2 = registerFile.get(inDecode.getSrc2()).getReg_value();
					inDecode.setSrc2_value(source2);
				}
				else {
					literal = inDecode.getLiteral();
					inDecode.setSrc2_value(literal);
				}
				set_exStage1 = true;
				branch_instr = false;
				break;
				
			case "HALT":
				halt=true;
				break;
			}
			fetchStage(n);
		}
			else{
				fetchStage(n);
			}
		
}
	
	boolean checkDependency(Instruction current, Instruction previous){
		if(current == null || previous == null)
			return false;
		else{
		if(previous.getDest() == current.getSrc1()||previous.getDest()==current.getSrc2()||previous.getDest()==current.getSrc3())
			return true;
		else
			return false;
			}
		}

	void fetchStage(int n)
	{	
		if(memory.isEmpty() || stall_fetch || halt){
			if (stall_fetch)
				stall_fetch = false;
			if (jump_flag)
				jump_flag = false;
			if(stall_decode)
				stall_decode = false;
			fetch = false;
			
		}
		else{
			jump_flag = false;
			fetch = true;
			inFetch = (Instruction) memory.get(PC_value);
			PC_value = PC_value + 4;
			offset+=4;
		}
	}


	void display()
	{

	}

	public static void main(String[] args) throws IOException
	{
		System.out.println("Starting simulator...");
		Apex ap = new Apex();
		ap.operations();
	}

}