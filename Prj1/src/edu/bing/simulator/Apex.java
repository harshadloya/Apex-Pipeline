package edu.bing.simulator;

import java.util.ArrayList;
import java.util.HashMap;
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
			for (i=PC_value; i < PC_value+instructionsToProcess.size(); i++){
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
			inEx1ALU1 = inDecode;
		}
		else
		{
			decodeStage(n);
		}

	}

	void decodeStage(int n)
	{
		if(fetch)
		{
			fetch=false;
			inDecode = inFetch;
			
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