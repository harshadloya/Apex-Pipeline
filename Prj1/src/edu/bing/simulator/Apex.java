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
	int result_inEx1ALU2, result_inEx2ALU2;
	int result_inMem;
	HashMap<Integer, Object> memory = new HashMap <Integer, Object> ();

	Scanner sc = new Scanner(System.in);
	static ArrayList<Instruction> instructionsToProcess = new ArrayList<Instruction>();
	boolean mem, ex1a1, ex1a2, ex2a1, ex2a2, decode, fetch, halt;
	Instruction inFetch, inDecode, inEx1ALU1, inEx1ALU2, inEx2ALU1, inEx2ALU2, inMem, inWb;
	//Commenting as we may not need it
	//List<Instruction> pipelineValues = new ArrayList<Instruction>();

	HashMap<String, Register> registerStatus = new HashMap<String, Register>();


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
			registerStatus.put("R"+n, r);
		}
		Register r = new Register();
		r.setReg_name("X");
		registerStatus.put("X", r);
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

			System.out.println("Enter the Number of Cycles for Simulation");
			//int numberOfCycles = sc.nextInt();			//Actual Code Line
			int numberOfCycles = instructionsToProcess.size()+2;				//Temp Code Line
			for(int n=0; n<numberOfCycles; n++)
			{
				if(!halt)
				{
					wbStage(n);	
				}
			}
		}
	}


	void wbStage(int n)
	{
		if(mem)
		{
			mem=false;
			inWb = inMem;
			//pipelineValues.add(5, inWb);
			registerStatus.get(inWb.getDest()).setReg_value(result_inMem);
			System.out.println(registerStatus.get(inWb.getDest()));
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
			inMem = inEx1ALU1;
			result_inMem = result_inEx1ALU2;
			//pipelineValues.add(4, inMem);
			exStage1ALU2(n);
		}
		else if(ex2a2)
		{
			mem = true;
			ex2a2 = false;
			inMem = inEx1ALU1;
			result_inMem = result_inEx2ALU2;
			//pipelineValues.add(4, inMem);
			exStage2ALU2(n);
		}
		else
		{
			/*if(inDecode.getInstr_type()!=null)
			{
				switch(inDecode.getInstr_type())
				{
				case "BZ":
				case "BNZ":
				case "BAL":
					exStage2ALU2(n);
					break;
				case "ADD":
				case "SUB":
				case "MUL":
				case "MOVC":
					exStage1ALU2(n);
					break;
				}
			}
			else*/ 
			//keeping the above code if the below call needs to be based on instruction type
			//taking FU1 as it does not matter if decode stage is not hvaing any instruction
				exStage1ALU2(n);
		}
	}

	void exStage1ALU2(int n)
	{
		if(ex1a1)
		{
			ex1a2 = true;
			ex1a1=false;
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
				result_inEx1ALU2 = inEx1ALU2.getSrc1_value();
				break;

			}
		}
		else
			exStage1ALU1(n);

	}

	void exStage1ALU1(int n)
	{
		if(decode)
		{
			ex1a1 = true;
			decode = false;
			inEx1ALU1 = inDecode;
			//pipelineValues.add(2, inEx1ALU1);
			decodeStage(n);
		}
		else
		{
			decodeStage(n);
		}
	}

	void exStage2ALU2(int n)
	{
		if(ex2a1)
		{
			ex2a2 = true;
			ex2a1=false;
			inEx2ALU2 = inEx2ALU1;
			exStage2ALU1(n);
		}
		else
		{
			exStage2ALU1(n);
		}

	}

	void exStage2ALU1(int n)
	{
		if(decode)
		{
			ex2a1 = true;
			decode = false;
			inEx2ALU1 = inDecode;
			//pipelineValues.add(3, inEx2ALU1);
			decodeStage(n);
		}
		else
			decodeStage(n);
	}

	void decodeStage(int n)
	{
		if(fetch)
		{
			fetch=false;
			decode = true;
			inDecode = inFetch;
			//pipelineValues.add(1, inDecode);

			//will have to do something like below for each instruction type...
			//			registerStatus.get(inDecode.getSrc1()).setReg_name(inDecode.getSrc1());
			//			registerStatus.get(inDecode.getSrc1()).setReg_value(registerStatus.get(inDecode.getSrc1()).getReg_value());
			//			registerStatus.get(inDecode.getSrc1()).setStatus(1);
			//			fetchStage(n);
			int source1, source2, source3, literal;
			switch(inDecode.getInstr_type())
			{
			case "ADD":
			case "SUB":
			case "MUL":
			case "LOAD":
				source1 = registerStatus.get(inDecode.getSrc1()).getReg_value();
				inDecode.setSrc1_value(source1);

				if (inDecode.getSrc2()!=null){
					source2 = registerStatus.get(inDecode.getSrc2()).getReg_value();
					inDecode.setSrc2_value(source2);
				}
				else {
					literal = inDecode.getLiteral();
					inDecode.setSrc2_value(literal);
				}
				//dest = registerStatus.get(inDecode.getDest()).getReg_value();
				break;
			case "BAL":
			case "BNZ":
			case "BZ" :
				literal = inDecode.getLiteral();
				inDecode.setSrc1_value(literal);
				break;
			case "STORE":
				source1 = registerStatus.get(inDecode.getSrc1()).getReg_value();
				inDecode.setSrc1_value(source1);
				source3 = registerStatus.get(inDecode.getSrc3()).getReg_value();
				inDecode.setSrc3_value(source3);
				if (inDecode.getSrc2()!=null){
					source2 = registerStatus.get(inDecode.getSrc2()).getReg_value();
					inDecode.setSrc2_value(source2);
				}
				else {
					literal = inDecode.getLiteral();
					inDecode.setSrc2_value(literal);
				}
				break;
			case "MOVC":
				source1 = inDecode.getLiteral();
				inDecode.setSrc1_value(source1);
				break;

			}
			fetchStage(n);
		}
		else
		{
			fetchStage(n);
		}
	}

	void fetchStage(int n)
	{
		fetch=true;
		inFetch = (Instruction) memory.get(PC_value);
		//pipelineValues.add(1, inFetch);
		PC_value=PC_value+4;
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