package ar.edu.itba.pod.mmxivii.sube.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Class regrouping useful input/output shortcuts.
 */
public class IO
{

	public enum DefaultYesNo
	{
		YES,
		NO,
		NO_DEFAULT
	};

	private static final BufferedReader stdin
		= new BufferedReader(new InputStreamReader(System.in));

	/**
	 * @see
	 * http://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println
	 */
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BOLD = "\u001B[1m";
	public static final String ANSI_RED = "\u001B[31m";

	/**
	 * Prints a string to the standard output, without newline caracter at the
	 * end.
	 *
	 * @param s the string to be printed
	 */
	public static void print(String s)
	{
		System.out.print(s);
	}

	/**
	 * Prints an information string to the standard output, without newline
	 * caracter at the end.
	 *
	 * @param s the string to be printed
	 */
	public static void printInfo(String s)
	{
		print(ANSI_BOLD);
		print("** " + s);
		print(ANSI_RESET);
	}

	/**
	 * Prints a string to the standard output, with newline caracter at the end.
	 *
	 * @param s the string to be printed
	 */
	public static void println(String s)
	{
		System.out.println(s);
	}

	/**
	 * Prints a newline caracter to the standard output.
	 */
	public static void println()
	{
		println("");
	}

	/**
	 * Prints an information string to the standard output, with newline
	 * caracter at the end.
	 *
	 * @param s the string to be printed
	 */
	public static void printlnInfo(String s)
	{
		print(ANSI_BOLD);
		print("** " + s);
		println(ANSI_RESET);
	}

	/**
	 * Prints an error string to the standard output, with newline caracter at
	 * the end.
	 *
	 * @param s the string to be printed
	 */
	public static void printlnError(String s)
	{
		print(ANSI_BOLD);
		print(ANSI_RED);
		print("** " + s);
		println(ANSI_RESET);
	}

	/**
	 * Reads a line from the standard input and returns it.
	 *
	 * @return the line read from standard input (without line-termination
	 * caracters), or an empty string in case of IO errors
	 */
	public static String readLine()
	{
		String in;
		try
		{
			in = stdin.readLine();
		} catch (IOException ex)
		{
			return "";
		}
		if (in == null)
		{
			return "";
		}
		return in;
	}

	/**
	 * Reads a double from the standard input and returns it. Blocks the
	 * execution flow until a valid float number is entered.
	 *
	 * @return the read double
	 */
	public static double readDouble()
	{
		String line;
		double res = 0.0;
		boolean validRead = false;
		while (!validRead)
		{
			try
			{
				IO.print("Enter a double value: ");
				line = readLine();
				res = Double.parseDouble(line);
				validRead = true;
			} catch (NumberFormatException nfe)
			{
				IO.printlnError("Reading error, please try again");
			}
		}
		return res;
	}

	private static void printYesNoPrompt(DefaultYesNo mode)
	{
		IO.print("[" + (mode == DefaultYesNo.YES ? 'Y' : 'y')
			+ '/' + (mode == DefaultYesNo.NO ? 'N' : 'n') + "]: ");
	}

	/**
	 * Reads a value as a Yes/No question from the standard input and returns
	 * it. If no default value is specified or incorrect value is entered,
	 * blocks the execution flow until a valid float number is entered.
	 *
	 * @param mode the default value specification
	 * @return the read double
	 */
	public static boolean readYesNo(DefaultYesNo mode)
	{
		String line;
		while (true)
		{
			printYesNoPrompt(mode);
			line = readLine();
			if (line.equalsIgnoreCase("Y"))
			{
				return true;
			}
			if (line.equalsIgnoreCase("N"))
			{
				return false;
			}
			if (line.isEmpty())
			{
				if (mode == DefaultYesNo.YES)
				{
					return true;
				}
				if (mode == DefaultYesNo.NO)
				{
					return false;
				}
			}
			// value entered is not valid or is empty (and required)
			IO.printlnError("Please type “Y” or “N”");
		}
	}

	/**
	 * Pauses the program's execution, waiting for the user to continue.
	 */
	public static void pause()
	{
		printInfo("Press “Return” to continue");
		readLine();
	}
}
