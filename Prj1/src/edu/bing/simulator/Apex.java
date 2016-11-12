package edu.bing.simulator;

import java.util.Scanner;

public class Apex {

	Scanner sc = new Scanner(System.in);

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

	}

	void simulate()
	{
		System.out.println("Enter the Number of Cycles for Simulation");
		int number = sc.nextInt();
		for(int i=0; i<number; i++)
		{
			wbStage();
		}
	}

	void display()
	{

	}

	void wbStage()
	{

	}

	void memStage()
	{

	}

	void exStage()
	{

	}

	void decodeStage()
	{

	}

	void fetchStage()
	{

	}

	public static void main(String[] args) 
	{
		Apex ap = new Apex();
		ap.operations();
	}

}