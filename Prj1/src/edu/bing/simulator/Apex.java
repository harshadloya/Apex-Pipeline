package edu.bing.simulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import edu.bing.beans.Instruction;
import edu.bing.loader.InstructionLoader;


public class Apex {

	boolean initialized = false; 
	private static int MEM_LENGTH = 10000;
	int PC_value = 4000;
	
	HashMap<Integer, Object> memory = new HashMap <Integer, Object> ();

	Scanner sc = new Scanner(System.in);
	static ArrayList<Instruction> instructionsToProcess = new ArrayList<Instruction>();
	boolean mem, ex, decode, fetch;
	Instruction inFetch, inDecode, inEx1ALU1, inEx1ALU2, inEx2ALU1, inEx2ALU2, inMem, inWb;
	List<Instruction> pipelineValues = Arrays.asList(new Instruction[6]);

	Apex()
	{
		
	}

	void operations()
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
	}

	void simulate()
	{
		if(!initialized){
			System.out.println("Please initialize the simulator.");
		}
		else
		{
			InstructionLoader isl = new InstructionLoader("./Instructions.txt");
			ArrayList<Instruction> instructionsToProcess = new ArrayList<Instruction>();
			instructionsToProcess = (ArrayList<Instruction>) isl.loadInstructions();
			int i,j=0;
			for (i=PC_value; i < PC_value+instructionsToProcess.size(); i=i+4){
				memory.put(i, instructionsToProcess.get(j));
				j++;				
			}
			
			System.out.println("Enter the Number of Cycles for Simulation");
			int numberOfCycles = sc.nextInt();
			for(int n=0; n<numberOfCycles; n++)
			{
				wbStage(n);
			}
		}
	}


	void wbStage(int n)
	{
		if(mem)
		{
			mem=false;
			inWb = inMem;
			pipelineValues.add(5, inWb);
			memStage(n);
		}
		else
		{
			memStage(n);
		}
	}

	void memStage(int n)
	{
		if(ex)
		{
			ex = false;
			inMem = inEx1ALU1;
			pipelineValues.add(4, inMem);
			exStage(n);
		}
		else
		{
			exStage(n);
		}

	}

	void exStage(int n)
	{
		if(decode)
		{
			decode=false;
			switch(inDecode.getInstr_type())
			{
				case "ADD":
				case "SUB":
				case "MUL":
					exStage1ALU1(n);
				case "BAL":
				case "BNZ":
				case "BZ" :
					exStage2ALU1(n);
				case "HALT" :
					//set some boolean true to kill the main loop
					break;
			
			}
			decodeStage(n);
		}
		else
		{
			decodeStage(n);
		}

	}
	
	void exStage1ALU1(int n)
	{
			decode=false;
			inEx1ALU1 = inDecode;
			pipelineValues.add(2, inEx1ALU1);
			exStage1ALU2(n);

	}
	
	void exStage1ALU2(int n)
	{
		inEx1ALU2 = inEx1ALU1;
		//decodeStage(n);
	
	}
	
	void exStage2ALU1(int n)
	{
		inEx2ALU1 = inDecode;
		pipelineValues.add(3, inEx2ALU1);
		exStage2ALU2(n);
	}
	
	void exStage2ALU2(int n)
	{
		inEx2ALU2 = inEx2ALU1;

	}

	void decodeStage(int n)
	{
		if(fetch)
		{
			fetch=false;
			inDecode = inFetch;
			pipelineValues.add(1, inDecode);
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
		pipelineValues.add(0, inFetch);
		PC_value++;
	}
	

	void display()
	{

	}

	public static void main(String[] args) 
	{
		Apex ap = new Apex();
		ap.operations();
	}

}