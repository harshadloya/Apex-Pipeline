package edu.bing.simulator;

import java.awt.List;
import java.util.ArrayList;
import java.util.Scanner;

import edu.bing.beans.Instruction;
import edu.bing.loader.InstructionLoader;


public class Apex {

	boolean initialized = false; 

	Scanner sc = new Scanner(System.in);
	static ArrayList<Instruction> instructionsToProcess = new ArrayList<Instruction>();

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
	}

	void simulate()
	{
		if(!initialized){
			System.out.println("Please initialize the simulator.");
		}
		else
		{
			InstructionLoader isl = new InstructionLoader("./Instructions.txt");
			instructionsToProcess = (ArrayList<Instruction>) isl.loadInstructions();
			
			System.out.println("Enter the Number of Cycles for Simulation");
			int number = sc.nextInt();
			for(int n=0; n<number; n++)
			{
				wbStage(n);
			}
		}
	}

	void display()
	{

	}

	void wbStage(int n)
	{

	}

	void memStage(int n)
	{

	}

	void exStage(int n)
	{

	}

	void decodeStage(int n)
	{

	}

	void fetchStage(int n)
	{

	}

	public static void main(String[] args) 
	{
		Apex ap = new Apex();
		ap.operations();
	}

}